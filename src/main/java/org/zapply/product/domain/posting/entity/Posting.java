package org.zapply.product.domain.posting.entity;

import jakarta.persistence.*;
import lombok.*;
import org.zapply.product.domain.project.entity.Project;
import org.zapply.product.global.BaseTimeEntity;
import org.zapply.product.domain.posting.enumerate.PostingState;
import org.zapply.product.domain.user.enumerate.SNSType;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Posting extends BaseTimeEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postingId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column
    private String postingTitle;

    @Column
    @Enumerated(EnumType.STRING)
    private SNSType postingType;

    @Column
    @Enumerated(EnumType.STRING)
    private PostingState postingState;

    @Column
    private String postingLink;

    @Column
    private String mediaId;

    @Builder
    public Posting(Project project, String postingTitle, SNSType postingType,
                   PostingState postingState, String postingLink, String mediaId) {
        this.project       = project;
        this.postingTitle  = postingTitle;
        this.postingType   = postingType;
        this.postingState  = postingState;
        this.postingLink   = postingLink;
        this.mediaId       = mediaId;
    }

}
