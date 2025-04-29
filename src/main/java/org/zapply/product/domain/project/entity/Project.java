package org.zapply.product.domain.project.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.global.BaseTimeEntity;

@Getter
@Entity
@Table(name = "tb_project")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseTimeEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column
    private String projectTitle;

    @Column
    private String projectThumbnail;

    @Builder
    public Project(Member member, String projectTitle, String projectThumbnail) {
        this.member = member;
        this.projectTitle = projectTitle;
        this.projectThumbnail = projectThumbnail;
    }
}
