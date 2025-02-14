package com.synergy.backend.domain.ask.controller;

import com.synergy.backend.domain.ask.model.request.AskReq;
import com.synergy.backend.domain.ask.model.response.AskRes;
import com.synergy.backend.domain.ask.service.AskService;
import com.synergy.backend.global.common.BaseResponse;
import com.synergy.backend.global.common.BaseResponseStatus;
import com.synergy.backend.global.exception.BaseException;
import com.synergy.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ask")
@RequiredArgsConstructor
public class AskController {
    private final AskService askService;

    // 문의 생성 API
    @PostMapping("/")
    public BaseResponse<AskRes> createAsk(@RequestBody AskReq askReq, @AuthenticationPrincipal CustomUserDetails customUserDetails) throws BaseException{
        if(customUserDetails==null){
            throw new BaseException(BaseResponseStatus.NEED_TO_LOGIN);
        }
        AskRes response = askService.createAsks(askReq,customUserDetails.getIdx());
        return new BaseResponse<>(response);  // BaseResponse만 반환
   }

    // 문의 조회 API
    //TODO : @RequestParam 은 필터링에 적합하고, 특정 자원 ID 가 있는경우 @PathVariable이 적합하다.
    @GetMapping("/list/read")
    public BaseResponse<List<AskRes>> getAsksList(@RequestParam Long productIdx,Integer page, Integer size, @AuthenticationPrincipal CustomUserDetails customUserDetails) throws BaseException {
        Long memberIdx = null;
        if(customUserDetails!=null){
            memberIdx= customUserDetails.getIdx();
        }
        List<AskRes> askResList = askService.getAskListByProduct(productIdx, memberIdx, page, size);
        return new BaseResponse<>(askResList);  // BaseResponse만 반환
    }
}



