package com.itsuda.perfume.dto.response.post;

import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.dto.response.PageInfoDto;
import org.springframework.data.domain.Page;

import java.util.List;

public record PostMainDto(
        List<PostDto> dataList,
        PageInfoDto pageInfoDto
) {

    public static PostMainDto from(Page<Post> posts) {
        return new PostMainDto(posts.stream().map(PostDto::from).toList(), PageInfoDto.from(posts));
    }
}
