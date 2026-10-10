package org.sopt;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PostRepository {
    // 데이터 저장소 접근 --> CRUD, 쿼리 작성, 데이터 매핑
    private final List<Post> posts = new ArrayList<>();
    private long nextId = 1;

    // 저장
    public void save(Post post) {
        posts.add(post);
    }

    // 전체 조회
    public List<Post> findAll() {
        return new ArrayList<>(posts);
    }

    // 특정 게시글 조회
    /*
    public Post findByIndex(int index) {
        // 예외
        if (index < 0 || index >= posts.size()) {
            throw new IndexOutOfBoundsException("존재하지 않는 게시글입니다.");
        }
        return posts.get(index);
    }
    */

    public Post findById(long id) {
        for (Post post : posts) {
            if (post.getId() == id) {
                return post;
            }
        }
        throw new NoSuchElementException("존재하지 않는 게시글입니다.");
    }

    // 삭제
    public void delete(long id) {
        Post post = findById(id);
        posts.remove(post);
    }

    public long grantID() {
        return nextId++;
    }
}
