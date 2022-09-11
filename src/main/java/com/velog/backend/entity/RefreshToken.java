package com.velog.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long TokenId;

    @JoinColumn(name = "memberId", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    private String tokenValue;

    public void updateValue(String tokenValue){
        this.tokenValue = tokenValue;
    }

    public RefreshToken(Member member, String tokenValue){
        this.member = member;
        this.tokenValue = tokenValue;
    }
}
