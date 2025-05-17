package org.zapply.product.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.project.entity.Project;
import org.zapply.product.domain.project.repository.ProjectRepository;
import org.zapply.product.domain.user.entity.Member;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public Long createEmptyProject(Member member) {
        Project project = Project.builder()
                .member(member)
                .projectTitle("")       // 빈 타이틀
                .projectThumbnail("")   // 빈 썸네일
                .build();

        return projectRepository.save(project).getProjectId();
    }
}
