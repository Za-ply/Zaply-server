package org.zapply.product.domain.posting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zapply.product.domain.user.entity.Account;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.global.snsClients.instagram.InstagramPostingClient;
import org.zapply.product.global.snsClients.instagram.InstagramPostingRequest;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstagramPostingService {

    private final InstagramPostingClient instagramPostingClient;

    @Transactional
    public String publishInstagramPost(Member member, InstagramPostingRequest request, Long postingId) {
        return instagramPostingClient.createSingleMedia(member, request, postingId);
        //instagramPostingClient.publishMediaContainer(member, creationId);
    }

    @Transactional
    public String publishInstagramCarousel(Member member, InstagramPostingRequest requests, Long postingId) {
        return instagramPostingClient.createCarouselMedia(member, requests.mediaUrls(), postingId);
    }
}
