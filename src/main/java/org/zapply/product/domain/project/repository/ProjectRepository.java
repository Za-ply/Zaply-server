package org.zapply.product.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zapply.product.domain.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
