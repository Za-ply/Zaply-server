package org.zapply.product.global.snsClients.instagram;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.zapply.product.domain.posting.repository.PostingRepository;
import org.zapply.product.domain.project.repository.ProjectRepository;
import org.zapply.product.domain.user.entity.Account;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.repository.AccountRepository;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.clova.enuermerate.SNSType;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstagramPostingClient {

    private static final String INSTAGRAM_API_BASE = "https://graph.facebook.com/v22.0";

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ProjectRepository projectRepository;
    private final PostingRepository postingRepository;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Instagram 계정 불러오기
     * @param member
     * @return account
     */
    public Account getInstagramAccount(Member member) {
        return accountRepository.findByAccountTypeAndMember(SNSType.INSTAGRAM, member)
                .orElseThrow(() -> new CoreException(GlobalErrorType.ACCOUNT_NOT_FOUND));
    }

    /**
     * Instagram API에 요청하기 위한 액세스 토큰 가져오기
     * @param member
     * @return accessToken
     */
    private String getAccessToken(Member member) {
        return accountService.getAccessToken(member, SNSType.INSTAGRAM);
    }

    /**
     * Instagram에 미디어 컨테이너 생성하기
     * @param userId
     * @param accessToken
     * @param imageUrl
     * @param caption
     * @return mediaContainerId
     */
    public String createMediaContainer(String userId, String accessToken, String imageUrl, String caption) {
        String url = INSTAGRAM_API_BASE + "/" + userId + "/media"; // POST 대상 엔드포인트

        Map<String, Object> body = new HashMap<>();
        body.put("ig_id", userId);
        body.put("access_token", accessToken);
        body.put("image_url", imageUrl);
        body.put("caption", caption);
        body.put("access_token", accessToken);

        log.info("Instagram API URL: {}", url);
        try {
            String rawResponse = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            Map<String, Object> response = objectMapper.readValue(rawResponse, Map.class);

            log.info("Instagram API Response: {}", response);
            if(response == null || response.isEmpty()) {
                throw new CoreException(GlobalErrorType.INSTAGRAM_API_ERROR);
            }

            return response.get("id").toString();

        } catch (Exception e) {
            log.error("Error creating media container: {}", e.getMessage());
            throw new CoreException(GlobalErrorType.INSTAGRAM_API_ERROR);
        }
    }

    /**
     * Instagram에 미디어 만들기
     * @param member
     * @param request
     * @param postingId
     */
    public String createMedia(Member member, InstagramPostingRequest request, Long postingId) {
        Account account = getInstagramAccount(member);
        String accessToken = getAccessToken(member);

        String mediaContainerId = createMediaContainer(account.getUserId(), accessToken, request.imageUrl(), request.caption());

        return mediaContainerId;
    }
}
