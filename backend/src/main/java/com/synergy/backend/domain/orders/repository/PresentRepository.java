package com.synergy.backend.domain.orders.repository;

import com.synergy.backend.domain.member.model.entity.Member;
import com.synergy.backend.domain.orders.model.entity.Present;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PresentRepository extends JpaRepository<Present, Long> {
    List<Present> findAllByFromMember(Member member);
    @Query("SELECT p FROM Present p JOIN FETCH p.fromMember WHERE p.fromMember.idx = :memberIdx")
    List<Present> findAllByFromMemberWithMemberIdx(Long memberIdx);
    List<Present> findAllByToMember(Member member);
    @Query("SELECT p FROM Present p JOIN FETCH p.toMember WHERE p.toMember.idx = :memberIdx")
    List<Present> findAllByToMemberWithMemberIdx(Long memberIdx);
}
