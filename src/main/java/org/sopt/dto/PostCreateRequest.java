package org.sopt.dto;

import org.sopt.model.PostCategory;

public class PostCreateRequest {
    private String title;
    private String content;
    private PostCategory category;

    public PostCreateRequest(){}

    public PostCreateRequest(String title, String content, PostCategory postCategory) {
        this.title = title;
        this.content = content;
        this.category = postCategory;
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
