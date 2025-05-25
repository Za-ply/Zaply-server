package org.zapply.product.global.snsClients.facebook;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.zapply.product.global.snsClients.facebook.service.FacebookClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FacebookClientTest {

    @Value("${facebook.pageId}")
    String pageId;

    @Value("${facebook.userToken}")
    String accessToken;

    @Autowired
    private FacebookClient facebookClient;

    @Test
    void getPageToken() {
        FacebookToken facebookToken = facebookClient.getPageAccessToken(pageId, accessToken);

        assertThat(facebookToken).isNotNull();
        String token = facebookToken.accessToken();
        assertThat(token)
                .as("페이지 액세스 토큰은 null이 아니고, 빈 문자열이 아니어야 합니다")
                .isNotNull()
                .isNotEmpty()
                .startsWith("EAA");   // ← 앞 세 글자가 "EAA" 인지 체크
    }
}