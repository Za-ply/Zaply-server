package org.zapply.product.domain.posting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zapply.product.domain.posting.entity.Posting;
import org.zapply.product.domain.project.entity.Project;

import java.util.List;

public interface PostingRepository extends JpaRepository<Posting, Long> {
    List<Posting> findAllByProject_ProjectIdAndDeletedAtIsNull(Long projectId);
}
