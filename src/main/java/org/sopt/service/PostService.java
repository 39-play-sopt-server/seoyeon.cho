package org.sopt.service;

import org.sopt.model.Post;
import org.sopt.model.PostCategory;
import org.sopt.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    // 애플리케이션의 비즈니스 로직 수행
    // 비즈니스 규칙, 트랜잭션 경계, 여러 Repository 조합
    private final PostRepository repository;
    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public void createPost(String title, String content, PostCategory category) {
        // long id = repository.grantID();
        Post post = new Post(title, content, category);
        repository.save(post);
    }

    public List<Post> readPosts() {
        return repository.findAll();
    }

    public Post readPost(long id) {
        Post post = repository.findById(id).orElseThrow();
        post.increaseViewCount();
        return post;
    }

    public void updatePost(long id, String title, String content) {
        Post post = repository.findById(id).orElseThrow();
        post.updatePost(title, content);
    }

    public void deletePost(long id) {
        repository.deleteById(id);
    }
}
