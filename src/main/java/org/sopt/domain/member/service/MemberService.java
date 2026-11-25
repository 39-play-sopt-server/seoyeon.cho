package org.sopt.domain.member.service;

import org.sopt.global.exception.BusinessException;
import org.sopt.global.exception.ErrorCode;
import org.sopt.domain.member.entity.Member;
import org.sopt.domain.member.repository.MemberRepository;
import org.sopt.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public MemberService(MemberRepository memberRepository, PostRepository postRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    // 회원 생성
    public void createMember(String name) {
        Member member = new Member(name);
        memberRepository.save(member);
    }

    // 회원 삭제
    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        boolean hasPosts = postRepository.existsByMember_Id(id);

        if (hasPosts) {
            throw new BusinessException(ErrorCode.MEMBER_HAS_POSTS);
        }

        memberRepository.delete(member);
    }
}
