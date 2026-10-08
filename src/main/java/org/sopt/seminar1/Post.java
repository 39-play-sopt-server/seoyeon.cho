package org.sopt.seminar1;

public class Post {
    // Model: 게시판 내용의 데이터(제목, 본문)를 관리, 검증하며 비즈니스 로직에 따라 처리
    // 비즈니스 로직 --> 게시판 데이터의 생성, 조회, 수정, 삭제 정도
    private String title;
    private String content;

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}