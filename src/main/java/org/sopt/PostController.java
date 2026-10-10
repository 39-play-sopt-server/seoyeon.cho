package org.sopt;

import java.util.List;
import java.util.NoSuchElementException;

public class PostController {
    // Controller: 게시판의 기능 흐름을 제어
    // 사용자의 메뉴 선택에 맞는 기능을 제공하면서 model과 view에 결과를 반영
    private final PostView view;
    private final PostService service;

    public PostController(PostView view, PostService service) {
        this.view = view;
        this.service = service;
    }

    public void run() {
        while (true) {
            view.printMenu();

            try {
                int command = view.readCommand();

                switch (command) {
                    case 1 -> createPost();
                    case 2 -> readPosts();
                    case 3 -> readPost();
                    case 4 -> updatePost();
                    case 5 -> deletePost();
                    case 6 -> {
                        view.printMessage("프로그램을 종료합니다.");
                        return;
                    }
                    default -> view.printMessage("잘못된 입력입니다.");
                }
            } catch (NumberFormatException e) {
                view.printMessage("숫자를 입력해주세요.");
            }
        }
    }

    private void createPost() {
        String title = view.readTitle();
        String content = view.readContent();

        try {
            view.printCategory();
            PostCategory category = view.readCategory();

            service.createPost(title, content, category);
            view.printMessage("게시글이 작성되었습니다.");
        } catch (IllegalArgumentException e) {
            view.printMessage(e.getMessage());
        }
    }

    private void readPosts() {
        List<Post> posts = service.readPosts();
        if (posts.isEmpty()) {
            view.printMessage("게시글이 없습니다.");
            return;
        }
        view.printPosts(posts);
    }

    private void readPost() {
        if (service.readPosts().isEmpty()) {
            view.printMessage("게시글이 없습니다.");
            return;
        }

        try {
            long id = view.readPostNumber("조회할 게시글 ID: ");

            Post post = service.readPost(id);
            view.printPost(post);
        } catch (NumberFormatException e) {
            view.printMessage("게시글 ID는 숫자로 입력해주세요.");
        } catch (NoSuchElementException e) {
            view.printMessage(e.getMessage());
        }
    }

    private void updatePost() {
        if (service.readPosts().isEmpty()) {
            view.printMessage("게시글이 없습니다.");
            return;
        }

        try {
            long id = view.readPostNumber("수정할 게시글 ID: ");

            String newTitle = view.readTitle();
            String newContent = view.readContent();

            service.updatePost(id, newTitle, newContent);
            view.printMessage("게시글이 수정되었습니다.");
        } catch (NumberFormatException e) {
            view.printMessage("게시글 ID는 숫자로 입력해주세요.");
        } catch (IllegalArgumentException | NoSuchElementException e) {
            view.printMessage(e.getMessage());
        }
    }

    private void deletePost() {
        if (service.readPosts().isEmpty()) {
            view.printMessage("게시글이 없습니다.");
            return;
        }


        try {
            long id = view.readPostNumber("삭제할 게시글 ID: ");

            service.deletePost(id);
            view.printMessage("게시글이 삭제되었습니다.");
        } catch (NumberFormatException e) {
            view.printMessage("게시글 ID는 숫자로 입력해주세요.");
        } catch (NoSuchElementException e) {
            view.printMessage(e.getMessage());
        }
    }
}