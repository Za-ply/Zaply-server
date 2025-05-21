package org.zapply.product.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.zapply.product.global.BaseTimeEntity;
import org.zapply.product.global.clova.enuermerate.SNSType;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint", nullable = false)
    private Long accountId;

    @Column
    private String accountName;

    @Column
    @Enumerated(EnumType.STRING)
    private SNSType accountType;

    @Column
    private String tokenKey;

    @Column
    private String email;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime tokenExpireAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column
    private String userId;

    @Builder
    public Account(String accountName, SNSType accountType, String tokenKey, Member member, String email,
                   LocalDateTime tokenExpireAt, String userId) {
        this.accountName = accountName;
        this.accountType = accountType;
        this.tokenKey = tokenKey;
        this.member = member;
        this.email = email;
        this.tokenExpireAt = tokenExpireAt;
        this.userId = userId;
    }

    public void updateTokenExpireAt(LocalDateTime tokenExpireAt) {
        this.tokenExpireAt = tokenExpireAt;
    }
}
