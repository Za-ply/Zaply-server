package org.zapply.product.domain.posting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zapply.product.domain.posting.entity.Posting;

public interface PostingRepository extends JpaRepository<Posting, Long> {
}
