package com.synergy.backend.domain.member.model.request;

import com.synergy.backend.domain.grade.model.entity.Grade;
import com.synergy.backend.domain.member.model.entity.Member;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record MemberSignupReq (

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "비밀번호는 8자 이상, 영문과 숫자를 포함해야 합니다.")
    String password,

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자 사이여야 합니다.")
    String nickname,

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$",
             message = "전화번호 형식은 xxx-xxxx-xxxx 이어야 합니다.")
    String cellPhone,

    @NotNull(message = "생년월일은 필수입니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$",
             message = "생년월일 형식은 yyyy-MM-dd 이어야 합니다.")
    String birthday,

    String defaultAddress
){

    public Member toEntity(String encodedPassword, Grade grade) {
        return Member.builder()
                .email(this.email)
                .password(encodedPassword)
                .nickname(this.nickname)
                .cellPhone(this.cellPhone)
                .birthday(this.getBirthdayAsDate())
                .grade(grade)
                .profileImageUrl("https://ajh-project.s3.ap-northeast-2.amazonaws.com/member/default_profile.png")
                .build();
    }

    // 카카오용 별도 생성자를 만들 수 없음. record는 생성자 오버로딩을 지원하지 않음.
    // 대신 별도의 팩토리 메서드를 제공하는 방식으로 해결 가능
    public static MemberSignupReq kakaoSignup(String email, String nickname) {
        return new MemberSignupReq(email, "kakao", nickname, "010-0000-0000", "1990-01-01", null);
    }

    private LocalDate getBirthdayAsDate() {
        return LocalDate.parse(this.birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
