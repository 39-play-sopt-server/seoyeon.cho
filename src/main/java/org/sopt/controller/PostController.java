package org.sopt.controller;

import org.sopt.dto.ApiResponse;
import org.sopt.dto.PostCreateRequest;
import org.sopt.dto.PostUpdateRequest;
import org.sopt.model.Post;
import org.sopt.service.PostService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/posts")
public class PostController {
    // Controller: 게시판의 기능 흐름을 제어
    // 사용자의 메뉴 선택에 맞는 기능을 제공하면서 model과 view에 결과를 반영
    // --> HTTP 요청을 받아 Service를 호출하고 처리 결과를 HTTP 응답으로 반환
    // private final PostView view;
    private final PostService service;

    public PostController(PostService service) {
        // this.view = view;
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Post>>> readPosts() {
        List<Post> posts = service.readPosts();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "게시물 목록 조회 성공", posts));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPost(@RequestBody PostCreateRequest request) {
        service.createPost(request.getTitle(), request.getContent(), request.getCategory());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "게시글 작성 성공", null));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<Post>> readPost(@PathVariable("postId") long postId) {
        Post post = service.readPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "게시물 조회 성공", post));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(@PathVariable("postId") long postId, @RequestBody PostUpdateRequest request) {
        service.updatePost(postId, request.getTitle(), request.getContent());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "게시글 수정 성공", null));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable("postId") long postId) {
        service.deletePost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "게시글 삭제 성공", null));
    }

}