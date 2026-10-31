package org.sopt.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    MEMBER_HAS_POSTS(HttpStatus.CONFLICT, "작성한 게시글이 있어 삭제할 수 없습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
}
