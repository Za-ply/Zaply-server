package org.zapply.product.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.user.dto.request.CertificateRequest;
import org.zapply.product.domain.user.dto.request.SmsRequest;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.coolSMS.SMSClient;
import org.zapply.product.global.redis.RedisClient;

@Service
@RequiredArgsConstructor
public class SmsService{

    private final SMSClient smsClient;
    private final RedisClient redisClient;

    public void SendSms(SmsRequest smsRequestDto) {
        String phoneNum = smsRequestDto.phoneNum();
        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성
        smsClient.sendSMS(phoneNum, certificationCode); // SMS 인증 유틸리티를 사용하여 SMS 발송
        redisClient.setValue(phoneNum, certificationCode,60000L * 5);
    }

    public String certificate(CertificateRequest certificateRequest) {
        String phoneNum = certificateRequest.phoneNum();
        if (redisClient.getValue(phoneNum).equals(certificateRequest.authNum())){
            redisClient.deleteValue(phoneNum);
            return "휴대폰 인증 성공";
        }
        else{
            throw new CoreException(GlobalErrorType.SMS_UNAUTHENTICATED);
        }
    }
}
