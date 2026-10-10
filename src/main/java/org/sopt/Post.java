package org.sopt;

public class Post {
    // Model: 게시판 내용의 데이터(제목, 본문)를 관리, 검증하며 비즈니스 로직에 따라 처리
    // 비즈니스 로직 --> 게시판 데이터의 생성, 조회, 수정, 삭제 정도
    private String title;
    private String content;
    private PostCategory category;
    private final long id;

    public Post(long id, String title, String content, PostCategory category) {
        checkPost(title, content, category);

        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
    }

    private void checkPost(String title, String content, PostCategory category) {
        if (title == null || title.isBlank() || content == null || content.isBlank()) {
            throw new IllegalArgumentException("제목이나 내용은 비어있을 수 없습니다.");
        }

        if (category == null) {
            throw new IllegalArgumentException("카테고리 입력이 필요합니다.");
        }
    }

    public void updatePost(String title, String content) {
        checkPost(title, content, category);

        this.title = title;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public PostCategory getCategory() {
        return this.category;
    }
}