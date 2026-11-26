package org.sopt.domain.post.dto;

import org.sopt.domain.post.entity.PostCategory;

public class PostCreateRequest {
    private String title;
    private String content;
    private PostCategory category;

    public PostCreateRequest(){}

    public PostCreateRequest(String title, String content, PostCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
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
}
