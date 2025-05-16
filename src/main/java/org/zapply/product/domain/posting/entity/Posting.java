package org.zapply.product.domain.posting.entity;

import jakarta.persistence.*;
import lombok.*;
import org.zapply.product.domain.project.entity.Project;
import org.zapply.product.global.BaseTimeEntity;
import org.zapply.product.domain.posting.enumerate.PostingState;
import org.zapply.product.global.clova.enuermerate.SNSType;

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
    @Enumerated(EnumType.STRING)
    private SNSType postingType;

    @Column
    private String postingContent;

    @Column
    @Enumerated(EnumType.STRING)
    private PostingState postingState;

    @Column
    private String postingLink;

    @Column
    private String mediaId;

    @Builder
    public Posting(Project project, SNSType postingType, String postingContent,
                   PostingState postingState, String postingLink, String mediaId) {
        this.project       = project;
        this.postingType   = postingType;
        this.postingContent = postingContent;
        this.postingState  = postingState;
        this.postingLink   = postingLink;
        this.mediaId       = mediaId;
    }

}
