package org.zapply.product.global.snsClients.facebook;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zapply.product.global.snsClients.facebook.service.FacebookPostingClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FacebookPostingClientTest {

    @Autowired
    private FacebookPostingClient facebookPostingClient;

    @Test
    void createPagePost() {
        String postID = facebookPostingClient.createSinglePost("test 발행2");
        System.out.println("Post ID: " + postID);
    }

    @Test
    void createSingleImageAndPost(){
        String postId = facebookPostingClient.publishSinglePhoto("https://scontent-ssn1-1.cdninstagram.com/v/t51.2885-19/494392801_17844004305475697_7211205988381478781_n.jpg?stp=dst-jpg_s320x320_tt6&_nc_ht=scontent-ssn1-1.cdninstagram.com&_nc_cat=105&_nc_oc=Q6cZ2QFGN8zcnTn7g62VflgB9-59mMtmqXFLLEdDYY0yLBB1BKqlHQdViBgX5j5CuGEKCYDX2pCiMTjLxaP8Bt2xi90-&_nc_ohc=RHuqsIUE07sQ7kNvwF1R513&_nc_gid=rdWEPNu23i-HVGm-TbdWUA&edm=APs17CUBAAAA&ccb=7-5&oh=00_AfIdJuw_MVxqKMBT1aCtKbyNRm5zF6Jsx1yERW2XqDET6w&oe=683618D1&_nc_sid=10d13b","test_meeesgae" );
        System.out.println("Post ID: " + postId);
    }

    @Test
    void createMultiImagesAndPost(){
        List<String> imageUrls = List.of("https://scontent-ssn1-1.cdninstagram.com/v/t51.2885-19/494392801_17844004305475697_7211205988381478781_n.jpg?stp=dst-jpg_s320x320_tt6&_nc_ht=scontent-ssn1-1.cdninstagram.com&_nc_cat=105&_nc_oc=Q6cZ2QFGN8zcnTn7g62VflgB9-59mMtmqXFLLEdDYY0yLBB1BKqlHQdViBgX5j5CuGEKCYDX2pCiMTjLxaP8Bt2xi90-&_nc_ohc=RHuqsIUE07sQ7kNvwF1R513&_nc_gid=rdWEPNu23i-HVGm-TbdWUA&edm=APs17CUBAAAA&ccb=7-5&oh=00_AfIdJuw_MVxqKMBT1aCtKbyNRm5zF6Jsx1yERW2XqDET6w&oe=683618D1&_nc_sid=10d13b",
                "https://scontent-ssn1-1.cdninstagram.com/v/t51.2885-19/494392801_17844004305475697_7211205988381478781_n.jpg?stp=dst-jpg_s320x320_tt6&_nc_ht=scontent-ssn1-1.cdninstagram.com&_nc_cat=105&_nc_oc=Q6cZ2QFGN8zcnTn7g62VflgB9-59mMtmqXFLLEdDYY0yLBB1BKqlHQdViBgX5j5CuGEKCYDX2pCiMTjLxaP8Bt2xi90-&_nc_ohc=RHuqsIUE07sQ7kNvwF1R513&_nc_gid=rdWEPNu23i-HVGm-TbdWUA&edm=APs17CUBAAAA&ccb=7-5&oh=00_AfIdJuw_MVxqKMBT1aCtKbyNRm5zF6Jsx1yERW2XqDET6w&oe=683618D1&_nc_sid=10d13b");

        String message = "test";

        String postId = facebookPostingClient.createPagePost(message, imageUrls);

        // then
        assertNotNull(postId, "Post ID는 null이 아니어야 합니다.");
        System.out.println("Multi-image Post ID: " + postId);
    }
}