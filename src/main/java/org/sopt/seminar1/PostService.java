package org.sopt.seminar1;

import java.util.List;

public class PostService {
    // 애플리케이션의 비즈니스 로직 수행
    // 비즈니스 규칙, 트랜잭션 경계, 여러 Repository 조합
    private final PostRepository repository;
    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public void createPost(String title, String content, PostCategory category) {
        long id = repository.grantID();
        Post post = new Post(id, title, content, category);
        repository.save(post);
    }

    public List<Post> readPosts() {
        return repository.findAll();
    }

    public Post readPost(long id) {
        return repository.findById(id);
    }

    public void updatePost(long id, String title, String content) {
        Post post = repository.findById(id);
        post.updatePost(title, content);
    }

    public void deletePost(long id) {
        repository.delete(id);
    }
}
