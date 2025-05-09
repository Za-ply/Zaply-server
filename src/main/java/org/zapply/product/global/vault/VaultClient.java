package org.zapply.product.global.vault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VaultClient {

    private final VaultTemplate vaultTemplate;
    private final ObjectMapper objectMapper;


    /**
     * Vault에 시크릿을 저장
     * @param path
     * @param key
     * @param value
     */
    public void saveSecret(String path, String key, String value) {
        Map<String, Object> data = new HashMap<>();
        data.put(key, value);

        Map<String, Object> payload = new HashMap<>();
        payload.put("data", data);

        String fullPath = "secret/data/" + path;
        vaultTemplate.write(fullPath, payload);
    }

    /**
     * Vault에서 시크릿을 가져옴
     * @param path
     * @param key
     * @return secret
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

        return valueNode.isMissingNode() ? null : valueNode.asText();
    }
}