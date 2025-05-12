package org.zapply.product.global.threads;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.zapply.product.domain.posting.dto.request.ThreadsPostingRequest;
import org.zapply.product.domain.posting.dto.response.ThreadsPostingResponse;
import org.zapply.product.domain.posting.entity.Posting;
import org.zapply.product.domain.posting.enumerate.PostingState;
import org.zapply.product.domain.posting.repository.PostingRepository;
import org.zapply.product.domain.project.entity.Project;
import org.zapply.product.domain.project.repository.ProjectRepository;
import org.zapply.product.domain.user.entity.Account;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.enumerate.SNSType;
import org.zapply.product.domain.user.repository.AccountRepository;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;

import java.net.URI;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThreadsPostingClient {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String THREADS_API_BASE = "https://graph.threads.net/v1.0";
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ProjectRepository projectRepository;
    private final PostingRepository postingRepository;

    private Account getThreadsAccount(Member member) {
        return accountRepository.findByAccountTypeAndMember(SNSType.THREADS, member)
                .orElseThrow(() -> new CoreException(GlobalErrorType.ACCOUNT_NOT_FOUND));
    }

    private String getAccessToken(Member member) {
        return accountService.getAccessToken(member, SNSType.THREADS);
    }

    /**
     * 스레드에 미디어 컨테이너 생성하기
     * @param member
     * @param request
     * @param projectId
     * @return 생성된 미디어 컨테이너 ID
     */
    public ThreadsPostingResponse createMedia(Member member, ThreadsPostingRequest request, Long projectId) {
        try {
            Account account = getThreadsAccount(member);
            String accessToken = getAccessToken(member);

            URI uri = UriComponentsBuilder
                    .fromHttpUrl(THREADS_API_BASE + "/" + account.getUserId() + "/threads")
                    .queryParam("media_type", request.mediaType())
                    .queryParam("image_url", request.media())
                    .queryParam("text", request.text())
                    .queryParam("access_token", accessToken)
                    .build()
                    .encode()
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<Map> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    new HttpEntity<>(headers),
                    Map.class
            );

            Map<String, Object> body = response.getBody();

            if (body == null || body.get("id") == null) {
                throw new CoreException(GlobalErrorType.THREADS_CREATION_ID_NOT_FOUND);
            }
            return publishMedia(member, String.valueOf(body.get("id")), projectId,
                    request.mediaType(), request.media(), request.text());

        } catch (Exception e) {
            throw new CoreException(GlobalErrorType.THREADS_API_ERROR);
        }
    }

    /**
     * 스레드에 미디어 컨테이너 게시하기
     * @param member
     * @param creationId
     * @param projectId
     * @param mediaType
     * @param media
     * @param text
     * @return 게시된 미디어 컨테이너 ID
     */
    public ThreadsPostingResponse publishMedia(Member member, String creationId, Long projectId,
                                                        String mediaType, String media, String text) {
        try {

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new CoreException(GlobalErrorType.PROJECT_NOT_FOUND));
            Account account = getThreadsAccount(member);
            String accessToken = getAccessToken(member);

            URI uri = UriComponentsBuilder
                    .fromHttpUrl(THREADS_API_BASE + "/" + account.getUserId() + "/threads_publish")
                    .queryParam("creation_id", creationId)
                    .queryParam("access_token", accessToken)
                    .build()
                    .encode()
                    .toUri();

            ResponseEntity<Map> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    new HttpEntity<>(new HttpHeaders()),
                    Map.class
            );

            Map<String, Object> body = response.getBody();

            Posting posting = Posting.builder()
                    .postingLink("")
                    .postingTitle("")
                    .postingType(SNSType.THREADS)
                    .postingState(PostingState.POSTED)
                    .project(project)
                    .mediaId(String.valueOf(body.get("id")))
                    .build();

            postingRepository.save(posting);
            return ThreadsPostingResponse.of(posting, mediaType, media, text);

        } catch (Exception e) {
            throw new CoreException(GlobalErrorType.THREADS_API_ERROR);
        }
    }
}