package org.zapply.product.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.posting.entity.Image;
import org.zapply.product.domain.posting.entity.Posting;
import org.zapply.product.domain.project.repository.ImageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public List<String> getImagesURLByPosting(Posting Posting) {
        List<Image> images = imageRepository.findAllByPosting(Posting);

        List<String> urls = images.stream()
                .map(Image::getImageUrl) // Image 객체에서 URL을 추출
                .collect(Collectors.toList()); // List<String>으로 변환

        return urls;
    }
}
