package org.zapply.product.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.user.dto.request.AuthRequest;
import org.zapply.product.domain.user.entity.Credential;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.repository.UserRepository;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Credential을 생성하고 저장하는 메서드
     * @param credential
     * @param authRequest
     * @return Member
     */
    public Member createUser(Credential credential, AuthRequest authRequest) {
        userRepository.findByEmail(authRequest.email()).ifPresent(existingUser -> {
           throw new CoreException(GlobalErrorType.MEMBER_ALREADY_EXISTS);
        });
        Member member = authRequest.toMember(credential);
        return userRepository.save(member);
    }

    /**
     * 이메일로 사용자를 조회하는 메서드
     * @param email
     * @return Member
     */
    public Member getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CoreException(GlobalErrorType.MEMBER_NOT_FOUND));
    }
}