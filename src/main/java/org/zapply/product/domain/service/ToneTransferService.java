package org.zapply.product.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.clova.ClovaAiClient;
import org.zapply.product.global.clova.dto.request.ClovaRequest;
import org.zapply.product.global.clova.dto.response.ClovaResponse;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ToneTransferService {

    @Value("${cloud.ncp.clova.api-key}")
    private String apiKey;

    @Value("${cloud.ncp.clova.model-name}")
    private String modelName;

    private final ClovaAiClient clovaAiClient;

    public String TransferToSNSTone(SNSType snsType, String userPrompt) {
        String authorizationHeader = "Bearer " + apiKey;
        try{
            ClovaRequest clovaRequset = snsType.buildRequest(userPrompt);
            ClovaResponse clovaResponse = clovaAiClient.getCompletions(authorizationHeader, modelName, clovaRequset);
            return clovaResponse.result().message().content();
        } catch (Exception e) {
            log.error("변환 중 오류 발생 요청 데이터: {}", userPrompt, e);
            throw new CoreException(GlobalErrorType.CLOVA_API_ERROR);
        }

    }
}
