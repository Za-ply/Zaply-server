package org.zapply.product.global.snsClients.facebook;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zapply.product.global.snsClients.facebook.service.FacebookMediaClient;

@SpringBootTest
class FacebookMediaClientTest {

    @Autowired
    private FacebookMediaClient facebookMediaClient;

    @Test
    void getPagePosts() {
        facebookMediaClient.getPagePosts();
    }

    @Test
    void getPageId() {
        facebookMediaClient.getPageId();
    }

    @Test
    void getProfilePictureUrl() {
        facebookMediaClient.getProfilePicUrl();
    }
}