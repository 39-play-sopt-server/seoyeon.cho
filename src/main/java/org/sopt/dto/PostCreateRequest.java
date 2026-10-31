package org.sopt.dto;

import org.sopt.model.PostCategory;

public class PostCreateRequest {
    private String title;
    private String content;
    private PostCategory category;
    private Long memberId;

    public PostCreateRequest(){}

    public PostCreateRequest(String title, String content, PostCategory category, Long memberId) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.memberId = memberId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public PostCategory getCategory() {
        return category;
    }

    public Long getMemberId() {
        return memberId;
    }
}

