package org.sopt.model;

import jakarta.persistence.*;

@Entity
public class Post extends BaseEntity {
    // Model: 게시판 내용의 데이터(제목, 본문)를 관리, 검증하며 비즈니스 로직에 따라 처리
    // 비즈니스 로직 --> 게시판 데이터의 생성, 조회, 수정, 삭제 정도
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private long viewCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public Post(String title, String content, PostCategory category, Member member) {
        checkPost(title, content, category);
        if (member == null) {
            throw new IllegalArgumentException("작성자는 필수입니다.");
        }

        this.title = title;
        this.content = content;
        this.category = category;
        this.member = member;
    }

    protected Post() {}

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

    public void increaseViewCount() {
        this.viewCount++;
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

    public long getViewCount() {
        return viewCount;
    }

    public Member getMember() {
        return member;
    }
}
