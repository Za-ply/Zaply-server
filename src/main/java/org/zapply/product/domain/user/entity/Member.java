package org.zapply.product.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.zapply.product.global.BaseTimeEntity;

import java.time.LocalDate;

@Getter
@Entity
@SQLDelete(sql = "UPDATE \"member\" SET deleted_at = NOW() WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @Column(nullable = false, columnDefinition = "varchar(320)")
    private String email;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String phoneNumber;

    @Column(nullable = false, columnDefinition = "varchar(30)")
    private String residentNumber;

    @Column(columnDefinition = "varchar(20)")
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credential_id", referencedColumnName = "id")
    private Credential credential;

    @Builder
    public Member(String name, String email, String phoneNumber, String residentNumber, Credential credential) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.residentNumber = residentNumber;
        this.credential = credential;
    }
}
