package org.sopt.domain.member.dto;

public class MemberCreateRequest {
    private String name;

    public MemberCreateRequest() {}
    public MemberCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
