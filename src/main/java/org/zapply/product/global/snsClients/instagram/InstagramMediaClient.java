package org.zapply.product.global.snsClients.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;
import org.zapply.product.domain.user.entity.Account;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.repository.AccountRepository;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.clova.enuermerate.SNSType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class InstagramMediaClient {

    private static final Logger log = LoggerFactory.getLogger(InstagramMediaClient.class);
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public Account getInstagramAccount(Member member) {
        return accountRepository.findByAccountTypeAndMember(SNSType.INSTAGRAM, member)
                .orElseThrow(() -> new CoreException(GlobalErrorType.ACCOUNT_NOT_FOUND));
    }

    private String getAccessToken(Member member) {
        return accountService.getAccessToken(member, SNSType.INSTAGRAM);
    }

    public List<InstagramMediaResponse> getAllMedia(Member member) {
        Account account = getInstagramAccount(member);
        String userId = account.getUserId();
        String accessToken = getAccessToken(member);

        List<InstagramMediaResponse> allMedia = new ArrayList<>();
        String url = String.format("https://graph.facebook.com/v22.0/%s/media?fields=id,caption,media_type,media_url,permalink,timestamp&access_token=%s", userId, accessToken);

        while (url != null) {
            try {
                String rawResponse = restClient.get()
                        .uri(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(String.class);

                JsonNode root = objectMapper.readTree(rawResponse);
                JsonNode dataArray = root.get("data");

                if (dataArray != null && dataArray.isArray()) {
                    List<InstagramMediaResponse> mediaList = StreamSupport.stream(dataArray.spliterator(), false)
                            .map(this::parseMediaNode)
                            .map(media -> {
                                if ("CAROUSEL_ALBUM".equals(media.mediaType())) {
                                    Set<String> childMediaUrls = getCarouselChildren(media.id(), accessToken);

                                    return InstagramMediaResponse.builder()
                                            .id(media.id())
                                            .caption(media.caption())
                                            .mediaType(media.mediaType())
                                            .mediaUrls(new ArrayList<>(childMediaUrls))
                                            .permalink(media.permalink())
                                            .timestamp(media.timestamp())
                                            .build();
                                }
                                return media;
                            })
                            .toList();

                    allMedia.addAll(mediaList);
                }


                JsonNode paging = root.get("paging");
                if (paging != null && paging.has("next")) {
                    url = paging.get("next").asText();
                } else {
                    url = null;
                }
            } catch (RestClientException | com.fasterxml.jackson.core.JsonProcessingException e) {
                log.error("Instagram API Error: {}", e.getMessage());
                throw new CoreException(GlobalErrorType.INSTAGRAM_API_ERROR);
            }
        }

        return allMedia;
    }

    private InstagramMediaResponse parseMediaNode(JsonNode mediaNode) {
        String id = mediaNode.get("id").asText();
        String caption = mediaNode.hasNonNull("caption") ? mediaNode.get("caption").asText() : null;
        String mediaType = mediaNode.get("media_type").asText();

        List<String> mediaUrls = null;
        if (mediaNode.hasNonNull("media_url")) {
            mediaUrls = List.of(mediaNode.get("media_url").asText());
        }

        String permalink = mediaNode.get("permalink").asText();
        String timestamp = mediaNode.hasNonNull("timestamp") ? mediaNode.get("timestamp").asText() : null;

        return InstagramMediaResponse.builder()
                .id(id)
                .caption(caption)
                .mediaType(mediaType)
                .mediaUrls(mediaUrls)
                .permalink(permalink)
                .timestamp(timestamp)
                .build();
    }

    private Set<String> getCarouselChildren(String carouselId, String accessToken) {
        String url = String.format("https://graph.facebook.com/v22.0/%s/children?fields=media_url&access_token=%s", carouselId, accessToken);

        Set<String> mediaUrls = new LinkedHashSet<>();

        try {
            String rawResponse = restClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(rawResponse);
            JsonNode dataArray = root.get("data");

            if (dataArray != null && dataArray.isArray()) {
                mediaUrls.addAll(StreamSupport.stream(dataArray.spliterator(), false)
                        .map(node -> node.get("media_url").asText())
                        .collect(Collectors.toSet()));
            }
        } catch (Exception e) {
            log.error("Error fetching carousel children for id {}: {}", carouselId, e.getMessage());
        }

        return mediaUrls;
    }

}