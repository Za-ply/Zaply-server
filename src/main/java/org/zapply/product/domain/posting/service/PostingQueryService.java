package org.zapply.product.domain.posting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.global.snsClients.threads.ThreadsMediaClient;
import org.zapply.product.global.snsClients.threads.ThreadsMediaResponse;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostingQueryService {

    private final ThreadsMediaClient threadsMediaClient;
    private final AccountService accountService;

    /**
     * 스레드 미디어 조회하기
     *
     * @param member
     * @param
     * @return 스레드 미디어 조회 결과
     */
    public List<ThreadsMediaResponse.ThreadsMedia> getAllThreadsMedia( Member member) {
        // accessToken 가져오기
        String accessToken = accountService.getAccessToken(member, SNSType.THREADS);
        // 스레드 API에 요청하여 미디어 데이터 가져오기
        return threadsMediaClient.getAllThreadsMedia(accessToken);
    }

    /**
     * 스레드 단일 게시물 조회하기
     * @param member
     * @param mediaId
     */
    public ThreadsMediaResponse.ThreadsMedia getSingleThreadsMedia(Member member, String mediaId) {
        // accessToken 가져오기
        String accessToken = accountService.getAccessToken(member, SNSType.THREADS);
        // 스레드 API에 요청하여 미디어 데이터 가져오기
        return threadsMediaClient.getSingleThreadsMedia(accessToken, mediaId);
    }
}
