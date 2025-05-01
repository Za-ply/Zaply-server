package org.zapply.product.global.objectStorage.dto;

public record ReadPreSignedUrlResponse (
        String preSignedUrl,

        String objectUrl
){
    public static ReadPreSignedUrlResponse of(String preSignedUrl, String objectUrl) {
        return new ReadPreSignedUrlResponse(preSignedUrl, objectUrl);
    }
}
