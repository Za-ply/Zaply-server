package org.zapply.product.global.vault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VaultClient {

    private final VaultTemplate vaultTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Vault에 시크릿을 저장 (Base64 인코딩)
     * @param path 저장할 경로
     * @param key 키
     * @param value
     */
    public void saveSecret(String path, String key, String value) {
        String encodedValue = Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));

        String fullPath = "secret/data/" + path;
        vaultTemplate.write(fullPath, Map.of("data", Map.of(key, encodedValue)));
    }

    /**
     * Vault에서 시크릿을 가져옴 (Base64 디코딩)
     * @param path 시크릿 경로
     * @param key 키
     * @return secret 값
     */
    public String getSecret(String path, String key) {
        String fullPath = "secret/data/" + path;
        VaultResponse response = vaultTemplate.read(fullPath);

        if (response == null || response.getData() == null) {
            return null;
        }

        JsonNode root = objectMapper.valueToTree(response.getData());
        JsonNode dataNode = root.path("data");
        JsonNode valueNode = dataNode.path(key);

        if (valueNode.isMissingNode()) {
            return null;
        }

        byte[] decodedBytes = Base64.getDecoder().decode(valueNode.asText());
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}