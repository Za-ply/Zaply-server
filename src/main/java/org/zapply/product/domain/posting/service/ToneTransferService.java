package org.zapply.product.domain.posting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zapply.product.domain.posting.dto.request.ToneTransferRequest;
import org.zapply.product.global.clova.dto.ClovaMessage;
import org.zapply.product.global.clova.enuermerate.ClovaRole;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.clova.ClovaAiClient;
import org.zapply.product.global.clova.dto.request.ClovaRequest;
import org.zapply.product.global.clova.dto.response.ClovaResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ToneTransferService {

    @Value("${cloud.ncp.clova.api-key}")
    private String apiKey;

    @Value("${cloud.ncp.clova.model-name}")
    private String modelName;

    @Value("${prompt.snstype.threads}")
    private String threadsPrompt;

    @Value("${prompt.snstype.instagram}")
    private String instagramPrompt;

    @Value("${prompt.snstype.facebook}")
    private String facebookPrompt;

    private final ClovaAiClient clovaAiClient;

    public String TransferToSNSTone(ToneTransferRequest toneTransferRequest) {
        String authorizationHeader = "Bearer " + apiKey;
        SNSType snsType = toneTransferRequest.snsType();
        String userPrompt = toneTransferRequest.userPrompt();

        String systemPrompt;
        switch (snsType) {
            case THREADS    -> systemPrompt = threadsPrompt;
            case INSTAGRAM  -> systemPrompt = instagramPrompt;
            case FACEBOOK   -> systemPrompt = facebookPrompt;
            default         -> throw new CoreException(GlobalErrorType.CLOVA_API_ERROR);
        }

        try{
            ClovaRequest clovaRequset = ClovaRequest.from(List.of(
                    ClovaMessage.of(ClovaRole.SYSTEM.getValue(), systemPrompt),
                    ClovaMessage.of(ClovaRole.USER.getValue(),   userPrompt)
            ));
            ClovaResponse clovaResponse = clovaAiClient.getCompletions(authorizationHeader, modelName, clovaRequset);
            return clovaResponse.result().message().content();
        } catch (Exception e) {
            throw new CoreException(GlobalErrorType.CLOVA_API_ERROR);
        }
    }
}
