package org.zapply.product.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zapply.product.domain.user.entity.Account;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.enumerate.SNSType;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmailAndAccountTypeAndMember(String email, SNSType accountType, Member member);
    Optional<Account> findByAccountNameAndAccountTypeAndMember(String accountName, SNSType accountType, Member member);
    Optional<Account> findByAccountTypeAndMember(SNSType accountType, Member member);
}
