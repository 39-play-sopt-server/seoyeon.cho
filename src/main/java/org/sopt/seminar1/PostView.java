package org.sopt.seminar1;

import java.util.List;
import java.util.Scanner;

public class PostView {
    // View: 게시판이 동작하는데에 필요한 정보 출력
    // 게시판 데이터의 조회 뿐만 아니라 각종 입력 메시지와 결과 출력도 포함
    private final Scanner scanner =  new Scanner(System.in);

    public void printMenu() {
        System.out.println("\n=== 게시판 ===");
        System.out.println("1. 게시글 작성");
        System.out.println("2. 게시글 목록 조회");
        System.out.println("3. 게시글 단건 조회");
        System.out.println("4. 게시글 수정");
        System.out.println("5. 게시글 삭제");
        System.out.println("6. 종료");
    }

    public void printCategory() {
        System.out.println("\n=== 카테고리 ===");
        System.out.println("1. NOTICE");
        System.out.println("2. NORMAL");
        System.out.println("3. QUESTION");
        System.out.println("4. REVIEW");
        System.out.println("5. ETC");
    }

    public void printPosts(List<Post> posts) {
        for (Post post : posts) {
            System.out.println(post.getId() + ": " + post.getTitle());
        }
    }

    public int readCommand() {
        System.out.print("선택: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public PostCategory readCategory() {
        System.out.print("선택: ");
        int num = Integer.parseInt(scanner.nextLine());
        return switch (num) {
            case 1 -> PostCategory.NOTICE;
            case 2 -> PostCategory.NORMAL;
            case 3 -> PostCategory.QUESTION;
            case 4 -> PostCategory.REVIEW;
            case 5 -> PostCategory.ETC;
            default -> throw new IllegalArgumentException("잘못된 카테고리 번호입니다.");
        };
    }

    public String readTitle() {
        System.out.println("제목: ");
        return scanner.nextLine();
    }

    public String readContent() {
        System.out.println("내용: ");
        return scanner.nextLine();
    }

    public long readPostNumber(String message) {
        System.out.print(message);
        return Long.parseLong(scanner.nextLine());
    }

    public void printPost(Post post) {
        System.out.println("\n=== 게시글 ===");
        System.out.println("카테고리: " + post.getCategory());
        System.out.println("제목: " + post.getTitle());
        System.out.println("내용: " + post.getContent());
    }

    public void printMessage(String message) {
        System.out.println(message);
    }
}
