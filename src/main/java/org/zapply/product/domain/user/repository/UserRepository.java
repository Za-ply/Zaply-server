package org.zapply.product.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zapply.product.domain.user.entity.Member;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}
