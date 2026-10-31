package org.sopt.repository;

import org.sopt.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsByMember_Id(Long memberId);
}
