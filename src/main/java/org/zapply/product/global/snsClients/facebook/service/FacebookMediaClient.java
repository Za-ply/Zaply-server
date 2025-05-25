package org.zapply.product.global.snsClients.facebook.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;

import java.net.URI;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacebookMediaClient {

    private static final String FB_GRAPH_BASE = "https://graph.facebook.com/v22.0";
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public String getPagePosts(String accessToken, String pageId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(FB_GRAPH_BASE + "/" + pageId + "/feed").build().encode().toUri();

        try {
            String response = restClient.get().uri(uri)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve().body(String.class);

            JsonNode root = objectMapper.readTree(response);
            System.out.println(root.path("data").toString());
            return root.path("data").toString();
        } catch (Exception e) {
            log.error("Error fetching Facebook page posts", e);
            throw new CoreException(GlobalErrorType.FACEBOOK_API_ERROR);
        }
    }

    // 페이지아이디 조회
    public String getPageId(String accessToken) {
        URI uri = UriComponentsBuilder.fromHttpUrl(FB_GRAPH_BASE + "/me").build().encode().toUri();

        try {
            String response = restClient.get().uri(uri)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve().body(String.class);
            Map<String, Object> mapResponse = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});

            return mapResponse.get("id").toString();
        } catch (Exception e) {
            log.error("Error fetching Facebook page posts", e);
            throw new CoreException(GlobalErrorType.FACEBOOK_API_ERROR);
        }
    }

//    // 페이지아이디 조회
//    public String getProfilePicUrl(String pageId, String accessToken) {
//        URI uri = UriComponentsBuilder.fromHttpUrl(FB_GRAPH_BASE + "/" + pageId + "/picture?fields=url&redirect=false").build().encode().toUri();
//
//        try {
//            String response = restClient.get().uri(uri)
//                    .header("Authorization", "Bearer " + accessToken)
//                    .retrieve().body(String.class);
//
//            JsonNode root = objectMapper.readTree(response);
//            return root.path("data").path("url").asText();
//        } catch (Exception e) {
//            log.error("Error fetching Facebook page posts", e);
//            throw new CoreException(GlobalErrorType.FACEBOOK_API_ERROR);
//        }
//    }
}
