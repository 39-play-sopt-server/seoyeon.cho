package org.sopt.dto;

import org.sopt.model.Post;
import org.sopt.model.PostCategory;

import java.time.LocalDateTime;

public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private PostCategory category;
    private String writer;
    private long viewCount;
    private LocalDateTime createdAt;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.writer = post.getMember().getName();
        this.viewCount = post.getViewCount();
        this.createdAt = post.getCreatedAt();
    }

    public Long getId() {
        return id;
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

    public String getWriter() {
        return writer;
    }

    public long getViewCount() {
        return viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
