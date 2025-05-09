package org.zapply.product.global.security.jasypt;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Value;

@Converter
public class JasyptStringEncryptor implements AttributeConverter<String, String> {

    private final AES256TextEncryptor encryptor;

    public JasyptStringEncryptor(@Value("${jasypt.encryptor.password}") String encryptorPassword) {
        encryptor = new AES256TextEncryptor();
        encryptor.setPassword(encryptorPassword);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptor.decrypt(dbData);
    }
}
