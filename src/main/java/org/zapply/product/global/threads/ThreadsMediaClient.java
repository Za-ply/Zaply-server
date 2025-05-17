package org.zapply.product.global.threads;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThreadsMediaClient {

    private static final String THREADS_API_BASE = "https://graph.threads.net/v1.0";

    private final RestClient restClient = RestClient.create();

    /**
     * 사용자가 발행한 게시물의 미디어를 가져오는 메서드
     * curl -s -X GET \
     * @param accessToken
     * @return 게시글 리스트 가져오기
     */
    public List<ThreadsMediaResponse.ThreadsMedia> getAllThreadsMedia(String accessToken) {
        List<ThreadsMediaResponse.ThreadsMedia> allMedia = new ArrayList<>();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String after = null;

        while (true) {
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(THREADS_API_BASE + "/me/threads")
                    .queryParam("fields", "id,media_product_type,media_type,media_url,permalink,owner,username,text,timestamp,shortcode,thumbnail_url,children,is_quote_post")
                    .queryParam("since", "2023-07-06")
                    .queryParam("until", now)
                    .queryParam("limit", "25")
                    .queryParam("access_token", accessToken)
                    .queryParamIfPresent("after", Optional.ofNullable(after))
                    .build()
                    .encode()
                    .toUri();

            //is_quote_post가 true인 경우는 제외
            ThreadsMediaResponse response = Objects.requireNonNull(restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(ThreadsMediaResponse.class));

            List<ThreadsMediaResponse.ThreadsMedia> filteredMedia = response.data()
                    .stream()
                    .filter(media -> !media.is_quote_post())
                    .toList();

            if (response.data() != null) {
                allMedia.addAll(filteredMedia);
            }

            if (response.paging() == null || response.paging().cursors() == null || response.paging().cursors().after() == null) {
                break;
            }

            after = response.paging().cursors().after();
        }

        return allMedia;
    }

    /**
     * 스레드 단일 미디어 조회하기
     * @param accessToken
     * @param mediaId
     * @return 스레드 미디어 조회 결과
     */
    public ThreadsMediaResponse.ThreadsMedia getSingleThreadsMedia(String accessToken, String mediaId) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(THREADS_API_BASE + "/" + mediaId)
                .queryParam("fields", "id,media_product_type,media_type,media_url,permalink,owner,username,text,timestamp,shortcode,thumbnail_url,children,is_quote_post")
                .queryParam("access_token", accessToken)
                .build()
                .encode()
                .toUri();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(ThreadsMediaResponse.ThreadsMedia.class);
    }
}
