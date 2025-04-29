package org.zapply.product.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.zapply.product.domain.user.enumerate.SNSType;

@Getter
@Entity
@Table(name = "tb_account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Account(String accountName, SNSType accountType, String tokenKey, Member member)
    {
        this.accountName = accountName;
        this.accountType = accountType;
        this.tokenKey = tokenKey;
        this.member = member;
    }
}
