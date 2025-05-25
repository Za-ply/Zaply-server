package org.zapply.product.domain.posting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.global.snsClients.facebook.service.FacebookMediaClient;
import org.zapply.product.global.snsClients.facebook.service.FacebookPostingClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacebookPostingService {
    private final AccountService accountService;
    private final FacebookMediaClient facebookMediaClient;
    private final FacebookPostingClient facebookPostingClient;

    // 페이스북 엑세스 토큰을 가져오는 함수
    public String getAccessToken(Member member) {
        return accountService.getAccessToken(member, SNSType.THREADS);
    }

    // 페이스북 미디어를 가져오는 함수
    public String getMediaUrl(Member member) {
        String accessToken = getAccessToken(member);
        String pageId = facebookMediaClient.getPageId(accessToken);
        return facebookMediaClient.getPagePosts(pageId, accessToken);
    }

    // 페이스북 단일 포스트를 발행하는 함수(text만 포함)
    public String publishOnlyText(Member member, String message) {
        String accessToken = getAccessToken(member);
        String pageId = facebookMediaClient.getPageId(accessToken);
        return facebookPostingClient.createSinglePost(accessToken, pageId, message);
    }

    // 페이스북 글, 그림 1개를 발행하는 함수
    public String publishPostWithSinglePhotoAndText(Member member, String photoUrl, String message) {
        String accessToken = getAccessToken(member);
        String pageId = facebookMediaClient.getPageId(accessToken);
        return facebookPostingClient.publishSinglePhotoAndPost(accessToken, pageId, photoUrl, message);
    }

    // 페이스북 글, 그림 여러개를 발행하는 함수
    public String publishPostWithMultiPhotoAndText(Member member, String message, List<String> photoUrls) {
        String accessToken = getAccessToken(member);
        String pageId = facebookMediaClient.getPageId(accessToken);
        return facebookPostingClient.createPagePost(accessToken, pageId, message, photoUrls);
    }
}
