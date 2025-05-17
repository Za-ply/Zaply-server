package org.zapply.product.global.vault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.zapply.product.global.security.jasypt.JasyptStringEncryptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class VaultClient {

    private final VaultTemplate vaultTemplate;
    private final ObjectMapper objectMapper;
    private final JasyptStringEncryptor jasyptStringEncryptor;

    /**
     * Vault에 시크릿을 저장 (AES256 암호화)
     * @param path 저장할 경로
     * @param key 키
     * @param value 시크릿 값
     */
    public void saveSecret(String path, String key, String value) {
        String encryptedValue = jasyptStringEncryptor.encrypt(value);
        String fullPath = "secret/data/" + path;
        vaultTemplate.write(fullPath, Map.of("data", Map.of(key, encryptedValue)));
    }

    /**
     * Vault에서 시크릿을 가져옴 (AES256 복호화)
     * @param path 시크릿 경로
     * @param key 키
     * @return 복호화된 시크릿 값
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

        return jasyptStringEncryptor.decrypt(valueNode.asText());
    }
}