package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.dto.response.post.PostInfoDto;
import com.itsuda.perfume.dto.response.post.PostMainDto;
import com.itsuda.perfume.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    public PostMainDto getPostsByOrderType(int page, int size, PostOrderType postOrderType) {
        // Todo: PostOrderType 정해지는 대로 그에 맞는 정렬 로직 도입
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Post> posts = postRepository.findAll(pageable);
        List<PostInfoDto> postInfoDtos = posts.stream().map(PostInfoDto::from).toList();
        return new PostMainDto(postInfoDtos, PageInfoDto.from(posts));
    }
}
