package com.synergy.backend.domain.likes.controller;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import com.synergy.backend.domain.likes.model.entity.Likes;
import com.synergy.backend.domain.likes.model.response.LikesInfoResponse;
import com.synergy.backend.global.common.BaseResponseStatus;
import com.synergy.backend.global.exception.BaseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.synergy.backend.domain.atelier.model.entity.Atelier;
import com.synergy.backend.domain.atelier.repository.AtelierRepository;
import com.synergy.backend.domain.likes.repository.LikesRepository;
import com.synergy.backend.domain.likes.service.LikesService;
import com.synergy.backend.domain.member.model.entity.Member;
import com.synergy.backend.domain.member.repository.MemberRepository;
import com.synergy.backend.domain.product.model.entity.Product;
import com.synergy.backend.domain.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class LikesControllerTest {

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AtelierRepository atelierRepository;

    @Autowired
    private LikesService likesService;

    @Test
    @DisplayName("좋아요가 되어있는 상태면 취소 테스트")
    public void testToggleLike() throws BaseException {
        // Given
        Long memberIdx = 1L;
        Long productIdx = 1L;
        Long atelierIdx = 1L;

        // Member와 Product 객체를 실제로 생성
        Member member = Member.builder().idx(memberIdx).build();
        Atelier atelier = Atelier.builder().idx(atelierIdx).havingProductsLikeCount(20).build();
        Product product = Product.builder().idx(productIdx).likeCounts(10L).atelier(atelier).build();
        Likes likes = Likes.builder().product(product).member(member).build();

        memberRepository.save(member);
        atelierRepository.save(atelier);
        productRepository.save(product);
        likesRepository.save(likes);

        // When
        LikesInfoResponse result = likesService.toggleLike(memberIdx, productIdx);

        // Then
        assertFalse(result.isMemberIsLiked());  // 찜 해제되었으므로 false
        assertEquals(9L,result.getProductLikesCount());  // 상품 찜 카운트 한 개 감소
        assertEquals(19L,result.getAtelierHavingProductsLikesCount());  // 아틀리에 총 상품 찜 카운트 한 개 감소

        // like 테이블에 존재하면 안됨
        Optional<Likes> deletedLikes = likesRepository.findByMemberAndProduct(member, product);
        assertFalse(deletedLikes.isPresent());
    }

    @Test
    @DisplayName("동시성 테스트: 여러 회원이 동시에 좋아요/좋아요 취소 요청")
    public void testToggleLikeConcurrency() throws BaseException, InterruptedException, ExecutionException {
        // Given
        Long memberIdx1 = 1L;
        Long memberIdx2 = 2L;
        Long memberIdx3 = 3L;
        Long productIdx = 1L;
        Long atelierIdx = 1L;

        // Member, Atelier, Product 객체를 실제로 생성
        Member member1 = Member.builder().idx(memberIdx1).build();
        Member member2 = Member.builder().idx(memberIdx2).build();
        Member member3 = Member.builder().idx(memberIdx3).build();

        Atelier atelier = Atelier.builder().idx(atelierIdx).havingProductsLikeCount(20).build();
        Product product = Product.builder().idx(productIdx).likeCounts(10L).atelier(atelier).build();

        Likes likes1 = Likes.builder().product(product).member(member1).build();
        Likes likes2 = Likes.builder().product(product).member(member2).build();
        Likes likes3 = Likes.builder().product(product).member(member3).build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        atelierRepository.save(atelier);
        productRepository.save(product);
        likesRepository.save(likes1);
        likesRepository.save(likes2);
        likesRepository.save(likes3);

        // 동시성 테스트를 위한 ExecutorService 설정
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // 각 회원의 좋아요/취소 요청을 비동기적으로 실행
        List<Callable<Void>> tasks = new ArrayList<>();

        for(int i=0; i<100; i++) {
            tasks.add(() -> {
                likesService.toggleLike(1L, productIdx);  // member1
                return null;
            });

            tasks.add(() -> {
                likesService.toggleLike(2L, productIdx);  // member2
                return null;
            });

            tasks.add(() -> {
                likesService.toggleLike(3L, productIdx);  // member3
                return null;
            });
        }
        // 모든 작업을 실행하고 결과를 기다림
        List<Future<Void>> futures = executorService.invokeAll(tasks);

        // 모든 스레드가 완료될 때까지 기다림
        for (Future<Void> future : futures) {
            future.get();
        }

        // 최종 결과 확인
        Product updatedProduct = productRepository.findById(productIdx).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_PRODUCT));
        Atelier updatedAtelier = atelierRepository.findById(atelierIdx).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_ATELIER));

        // Then
        // 기대값: 상품 찜 수는 10 -> 좋아요 요청이 100번 들어오면 총 갯수는 그대로 여야함
        assertEquals(10L, updatedProduct.getLikeCounts(), "상품의 찜 갯수가 잘못되었습니다.");

        // 아틀리에 찜 수: 20 -> 좋아요가 요청이 100번 들어오면 총 갯수는 그대로 여야함
        assertEquals(20L, updatedAtelier.getHavingProductsLikeCount(), "아틀리에의 상품 찜 수가 잘못되었습니다.");

        // like 테이블에서, 해당 상품과 회원에 대한 찜 정보가 존재하는지 확인
        Optional<Likes> likes1Check = likesRepository.findByMemberAndProduct(member1, product);
        Optional<Likes> likes2Check = likesRepository.findByMemberAndProduct(member2, product);
        Optional<Likes> likes3Check = likesRepository.findByMemberAndProduct(member3, product);

        // 3명의 회원이 찜 정보가 존재해야 함
        assertTrue(likes1Check.isPresent(), "회원 1의 찜 정보가 존재하지 않습니다.");
        assertTrue(likes2Check.isPresent(), "회원 2의 찜 정보가 존재하지 않습니다.");
        assertTrue(likes3Check.isPresent(), "회원 3의 찜 정보가 존재하지 않습니다.");

        // ExecutorService 종료
        executorService.shutdown();
    }
}