package org.zapply.product.domain.posting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zapply.product.domain.posting.dto.response.PostingInfoResponse;
import org.zapply.product.domain.posting.entity.Posting;
import org.zapply.product.domain.posting.repository.PostingRepository;
import org.zapply.product.domain.project.entity.Project;
import org.zapply.product.domain.project.repository.ProjectRepository;
import org.zapply.product.domain.project.service.ImageService;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.domain.posting.enumerate.PostingState;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PostingServiceTest {

    @Mock
    private PostingRepository postingRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private PostingService postingService;

    @Test
    void whenProjectNotFound_thenThrowsProjectNotFound() {
        Long projectId = 1L;
        Member member = mock(Member.class);
        given(projectRepository.findById(projectId)).willReturn(Optional.empty());

        CoreException exception = assertThrows(CoreException.class,
                () -> postingService.getPostings(member, projectId));
        assertThat(exception.getErrorType()).isEqualTo(GlobalErrorType.PROJECT_NOT_FOUND);
    }

    @Test
    void whenMemberNotOwner_thenThrowsIsNotUserProject() {
        Long projectId = 1L;
        Member member = mock(Member.class);
        given(member.getId()).willReturn(10L);

        Project project = mock(Project.class);
        Member owner = mock(Member.class);
        given(owner.getId()).willReturn(20L);
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(project.getMember()).willReturn(owner);

        CoreException exception = assertThrows(CoreException.class,
                () -> postingService.getPostings(member, projectId));
        assertThat(exception.getErrorType()).isEqualTo(GlobalErrorType.IS_NOT_USER_PROJECT);
    }

    @Test
    void whenNoPostings_thenThrowsPostingNotFound() {
        Long projectId = 2L;
        Member member = mock(Member.class);
        given(member.getId()).willReturn(10L);

        Project project = mock(Project.class);
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        Member owner = mock(Member.class);
        given(owner.getId()).willReturn(10L);
        given(project.getMember()).willReturn(owner);

        given(postingRepository.findAllByProject_ProjectIdAndDeletedAtIsNull(projectId))
                .willReturn(Collections.emptyList());

        CoreException exception = assertThrows(CoreException.class,
                () -> postingService.getPostings(member, projectId));
        assertThat(exception.getErrorType()).isEqualTo(GlobalErrorType.POSTING_NOT_FOUND);
    }

    @Test
    void whenValid_thenReturnsPostingInfoResponses() {
        Long projectId = 3L;
        Member member = mock(Member.class);
        given(member.getId()).willReturn(30L);

        Project project = mock(Project.class);
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        Member owner = mock(Member.class);
        given(owner.getId()).willReturn(30L);
        given(project.getMember()).willReturn(owner);

        Posting posting = mock(Posting.class);
        given(posting.getPostingId()).willReturn(100L);
        given(posting.getPostingContent()).willReturn("Content");
        given(posting.getPostingType()).willReturn(SNSType.THREADS);
        given(posting.getPostingState()).willReturn(PostingState.POSTED);
        given(posting.getPostingLink()).willReturn("http://example.com");
        given(postingRepository.findAllByProject_ProjectIdAndDeletedAtIsNull(projectId))
                .willReturn(List.of(posting));

        List<String> urls = List.of("url1", "url2");
        given(imageService.getImagesURLByPosting(posting)).willReturn(urls);

        List<PostingInfoResponse> responses = postingService.getPostings(member, projectId);

        assertThat(responses).hasSize(1);
        PostingInfoResponse dto = responses.get(0);
        assertThat(dto.postingId()).isEqualTo(100L);
        assertThat(dto.postingContent()).isEqualTo("Content");
        assertThat(dto.postingType()).isEqualTo(SNSType.THREADS);
        assertThat(dto.postingState()).isEqualTo(PostingState.POSTED);
        assertThat(dto.postingLink()).isEqualTo("http://example.com");
        assertThat(dto.postingImages()).containsExactlyElementsOf(urls);
    }
}
