package org.sopt.controller;

import org.sopt.dto.ApiResponse;
import org.sopt.dto.MemberCreateRequest;
import org.sopt.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService service;

    public MemberController(MemberService memberService) {
        this.service = memberService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createMember(
            @RequestBody MemberCreateRequest request) {

        service.createMember(
                request.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "회원 생성 성공", null));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @PathVariable("memberId") Long memberId) {

        service.deleteMember(memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "회원 삭제 성공", null));
    }
}
