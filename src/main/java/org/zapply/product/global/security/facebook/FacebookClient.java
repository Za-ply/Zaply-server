package org.zapply.product.global.security.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FacebookClient {

    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String clientSecret;

    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.create();

    /**
     * 페이스북으로부터 받은 인가코드를 통해 액세스 토큰 요청하기
     * @param code
     * @param redirectUri
     * @return FacebookToken
     */
    public FacebookToken getFacebookAccessToken(String code, String redirectUri) {
        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("graph.facebook.com")
                        .path("/v22.0/oauth/access_token")
                        .queryParam("client_id", clientId)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .body(String.class);


        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return new FacebookToken(jsonNode.get("access_token").asText());
        } catch (IOException e) {
            throw new CoreException(GlobalErrorType.FACEBOOK_API_ERROR);
        }
    }

    /**
     * 페이스북에 있는 사용자 정보 반환
     * @param token
     * @return FacebookProfile
     */
    public FacebookProfile getMemberInfo(FacebookToken token) {
        String response = restClient.get()
                .uri("https://graph.facebook.com/me?fields=id,name,email&access_token=" + token.accessToken())
                .retrieve()
                .body(String.class);
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return new FacebookProfile(
                    jsonNode.get("id").asText(),
                    jsonNode.get("name").asText(),
                    jsonNode.has("email") ? jsonNode.get("email").asText() : null
            );
        } catch (IOException e) {
            throw new CoreException(GlobalErrorType.FACEBOOK_API_ERROR);
        }
    }

    public FacebookToken getLongLivedToken(String shortLivedToken) {
        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("graph.facebook.com")
                        .path("/v22.0/oauth/access_token")
                        .queryParam("grant_type", "fb_exchange_token")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("fb_exchange_token", shortLivedToken)
                        .build())
                .retrieve()
                .body(String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return new FacebookToken(jsonNode.get("access_token").asText());
        } catch (IOException e) {
            throw new CoreException(GlobalErrorType.FACEBOOK_API_ERROR);
        }
    }
}