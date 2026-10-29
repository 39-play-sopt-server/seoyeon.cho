package org.sopt.service;

import org.sopt.exception.BusinessException;
import org.sopt.exception.ErrorCode;
import org.sopt.model.Member;
import org.sopt.repository.MemberRepository;
import org.sopt.repository.PostRepository;
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
