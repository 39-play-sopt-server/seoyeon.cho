package org.sopt.service;

import org.sopt.dto.PostResponse;
import org.sopt.exception.BusinessException;
import org.sopt.exception.ErrorCode;
import org.sopt.model.Member;
import org.sopt.model.Post;
import org.sopt.model.PostCategory;
import org.sopt.repository.MemberRepository;
import org.sopt.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostService(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void createPost(String title, String content, PostCategory category, Long memberId) {
        if (memberId == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Post post = new Post(title, content, category, member);
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> readPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")));
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(PostResponse::new);
    }

    @Transactional
    public PostResponse readPost(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        post.increaseViewCount();
        return new PostResponse(post);
    }

    @Transactional
    public void updatePost(long id, String title, String content) {
        Post post = postRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        post.updatePost(title, content);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        postRepository.delete(post);
    }
}
