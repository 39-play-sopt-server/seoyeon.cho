package org.sopt.controller;

import org.sopt.dto.ApiResponse;
import org.sopt.dto.PostCreateRequest;
import org.sopt.dto.PostResponse;
import org.sopt.dto.PostUpdateRequest;
import org.sopt.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping(path = "/api/v1/posts")
public class PostController {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> readPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<PostResponse> posts = service.readPosts(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "게시물 목록 조회 성공", posts));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPost(@RequestBody PostCreateRequest request) {
        service.createPost(request.getTitle(), request.getContent(), request.getCategory(), request.getMemberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "게시글 작성 성공", null));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> readPost(@PathVariable("postId") long postId) {
        PostResponse post = service.readPost(postId);
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