package com.synergy.backend.domain.ask.controller;

import com.synergy.backend.domain.ask.model.request.AskReq;
import com.synergy.backend.domain.ask.model.response.AskRes;
import com.synergy.backend.domain.ask.service.AskService;
import com.synergy.backend.global.common.BaseResponse;
import com.synergy.backend.global.common.BaseResponseStatus;
import com.synergy.backend.global.exception.BaseException;
import com.synergy.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ask")
@RequiredArgsConstructor
public class AskController {
    private final AskService askService;

    // 문의 생성 API
    @PostMapping("")
    public BaseResponse<AskRes> createAsk(@RequestBody AskReq askReq, @AuthenticationPrincipal CustomUserDetails customUserDetails) throws BaseException{
        if(customUserDetails==null){
            throw new BaseException(BaseResponseStatus.NEED_TO_LOGIN);
        }
        AskRes response = askService.createAsks(askReq,customUserDetails.getIdx());
        return new BaseResponse<>(response);
   }

    // 문의 조회 API
    @GetMapping("/list")
    public BaseResponse<List<AskRes>> getAsksList(@RequestParam Long productIdx,
                                                  @PageableDefault(size = 10, sort = "idx", direction = Sort.Direction.DESC) Pageable pageable,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails) throws BaseException {
        Long memberIdx = null;
        if(customUserDetails!=null){
            memberIdx= customUserDetails.getIdx();
        }
        List<AskRes> askResList = askService.getAskListByProduct(productIdx, memberIdx, pageable);
        return new BaseResponse<>(askResList);
    }
}



