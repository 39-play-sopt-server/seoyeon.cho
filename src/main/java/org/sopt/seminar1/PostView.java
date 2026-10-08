package org.sopt.seminar1;

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

    public int readCommand() {
        System.out.print("선택: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public String readTitle() {
        System.out.println("제목: ");
        return scanner.nextLine();
    }

    public String readContent() {
        System.out.println("내용: ");
        return scanner.nextLine();
    }

    public int readPostNumber(String message) {
        System.out.print(message);
        return Integer.parseInt(scanner.nextLine());
    }

    public void printPost(Post post) {
        System.out.println("\n=== 게시글 ===");
        System.out.println("제목: " + post.getTitle());
        System.out.println("내용: " + post.getContent());
    }

    public void printMessage(String message) {
        System.out.println(message);
    }
}
