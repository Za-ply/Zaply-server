package org.zapply.product.global.objectStorage.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.zapply.product.global.objectStorage.dto.ReadPreSignedUrlResponse;
import org.zapply.product.global.objectStorage.service.ObjectStorageService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectStorageService storageService;

    @Test
    void presignedUrlEndpoint_성공_200_JSON반환() throws Exception {

        // given
        String prefix = "users";
        String fileName = "avatar.png";
        String fakePre = "https://example.com/presigned";
        String fakeObj = "https://example.com/object/avatar.png";

        ReadPreSignedUrlResponse resp = ReadPreSignedUrlResponse.of(fakePre, fakeObj);
        when(storageService.getPreSignedUrl(prefix, fileName)).thenReturn(resp);

        // when & then
        mockMvc.perform(get("/v1/image/presigned-url")
                        .param("prefix", prefix)
                        .param("fileName", fileName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.preSignedUrl").value(fakePre))
                .andExpect(jsonPath("$.objectUrl").value(fakeObj));
    }

}