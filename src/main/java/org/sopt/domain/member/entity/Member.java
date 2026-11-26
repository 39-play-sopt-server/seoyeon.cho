package org.sopt.domain.member.entity;

import jakarta.persistence.*;
import org.sopt.global.entity.BaseEntity;

@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    protected Member() {}
    public Member(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("회원 이름은 비어 있을 수 없습니다.");
        }
        this.name = name;
    }
    public Member(String name, String email, String password) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("회원 이름은 비어 있을 수 없습니다.");
        }
        this.name = name;
        this.email = email;
        this.password = password;
    }
    public Member(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}
