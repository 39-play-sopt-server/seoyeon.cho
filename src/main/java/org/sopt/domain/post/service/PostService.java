package org.sopt.domain.post.service;

import org.sopt.domain.post.dto.PostResponse;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.sopt.domain.member.entity.Member;
import org.sopt.domain.post.entity.Post;
import org.sopt.domain.post.entity.PostCategory;
import org.sopt.domain.member.repository.MemberRepository;
import org.sopt.domain.post.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 탈퇴한 회원은 게시글 작성 불가
        if (member.isDeleted()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "탈퇴한 회원은 게시글을 작성할 수 없습니다."
            );
        }

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
    public void updatePost(long id, String title, String content, Long memberId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        validatePostOwner(post, memberId);
        post.updatePost(title, content);
    }

    @Transactional
    public void deletePost(Long id, Long memberId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        validatePostOwner(post, memberId);
        postRepository.delete(post);
    }

    private void validatePostOwner(Post post, Long memberId) {
        if (memberId == null || !post.getMember().getId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 게시글만 수정하거나 삭제할 수 있습니다.");
        }
    }
}
