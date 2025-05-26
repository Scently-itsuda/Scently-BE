package com.itsuda.perfume.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.dto.request.ootd.CreateOotdDto;
import com.itsuda.perfume.dto.request.ootd.OotdCommentRequestDto;
import com.itsuda.perfume.dto.response.ootd.CommentsDto;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.dto.response.perfume.OotdPerfumesDto;
import com.itsuda.perfume.service.OotdService;
import com.itsuda.perfume.service.PerfumeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OotdController.class)
@WithMockUser
@ActiveProfiles("test")
class OotdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OotdService ootdService;

    @MockBean
    private PerfumeService perfumeService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("OOTD 썸네일들을 조회한다.")
    @Test
    void getOotdThumbnails() throws Exception {
        // given
        OotdMainDto result = new OotdMainDto(null, null);

        Mockito.when(ootdService.getOotdThumbnailsByOrderType(anyInt(), anyInt(), eq(OotdOrderType.NEWEST_DESCENDING), anyLong()))
                .thenReturn(result);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/ootds")
                                .queryParam("order", "NEWEST")
                                .queryParam("page", "0")
                                .queryParam("size", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("OOTD 게시글 작성 중, 향수의 목록을 조회한다.")
    @Test
    void getOotdPerfumes() throws Exception {
        // given
        OotdPerfumesDto result = new OotdPerfumesDto(null);
        Mockito.when(perfumeService.getAllPerfumes()).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ootds/perfumes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("OOTD 게시글을 작성할 때는 내용이 비어있거나 공백이면 안된다.")
    @Test
    void ootdMustHaveContent() throws Exception {
        // given
        CreateOotdDto request = new CreateOotdDto("", 10, List.of(10L), List.of("태그1", "태그2"));
        MockMultipartFile image1 = new MockMultipartFile("images", "1.png",
                MediaType.IMAGE_JPEG_VALUE, "1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "2.png",
                MediaType.IMAGE_JPEG_VALUE, "2".getBytes());
        MockMultipartFile image3 = new MockMultipartFile("images", "3.png",
                MediaType.IMAGE_JPEG_VALUE, "3".getBytes());
        MockMultipartFile json = new MockMultipartFile("createOotdDto", "createOotdDto.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/ootds")
                        .file(image1)
                        .file(image2)
                        .file(image3)
                        .file(json)
                        .with(csrf()))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("OOTD의 내용은 공백이 아닌 글자가 있어야합니다"));
    }

    @DisplayName("태그는 10개까지만 달 수 있다.")
    @Test
    void tagSizeIsSmallerOrEqualThan10() throws Exception {
        // given
        CreateOotdDto request = new CreateOotdDto("test content", 10, List.of(10L),
                List.of("태그1", "태그2", "태그3", "태그4", "태그5", "태그6", "태그7", "태그8", "태그9", "태그10", "태그11"));
        MockMultipartFile image1 = new MockMultipartFile("images", "1.png",
                MediaType.IMAGE_JPEG_VALUE, "1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "2.png",
                MediaType.IMAGE_JPEG_VALUE, "2".getBytes());
        MockMultipartFile image3 = new MockMultipartFile("images", "3.png",
                MediaType.IMAGE_JPEG_VALUE, "3".getBytes());
        MockMultipartFile json = new MockMultipartFile("createOotdDto", "createOotdDto.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/ootds")
                        .file(image1)
                        .file(image2)
                        .file(image3)
                        .file(json)
                        .with(csrf()))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("태그는 최대 10개까지 가능합니다"));
    }

    @DisplayName("OOTD 게시글을 작성할 때는 내용이 비어있거나 공백이면 안되고 공백문자를 포함해서는 안된다..")
    @ValueSource(strings = {"", " ", "이태그는총열다섯글자를넘습니다.", "공벡포함 태그"})
    @ParameterizedTest
    void tagIsNotBlankAndSmallerOrEqualThan15AndNotContainSpace(String tag) throws Exception {
        // given
        CreateOotdDto request = new CreateOotdDto("test content", 10, List.of(10L), List.of(tag));
        MockMultipartFile image1 = new MockMultipartFile("images", "1.png",
                MediaType.IMAGE_JPEG_VALUE, "1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "2.png",
                MediaType.IMAGE_JPEG_VALUE, "2".getBytes());
        MockMultipartFile image3 = new MockMultipartFile("images", "3.png",
                MediaType.IMAGE_JPEG_VALUE, "3".getBytes());
        MockMultipartFile json = new MockMultipartFile("createOotdDto", "createOotdDto.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/ootds")
                        .file(image1)
                        .file(image2)
                        .file(image3)
                        .file(json)
                        .with(csrf()))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message")
                        .value("태그는 공백이 아닌 1~15자여야 하며 공백문자를 포함하면 안됩니다"));
    }

    @DisplayName("OOTD 게시글을 작성할 때 향수를 반드시 등록해야 한다.")
    @Test
    void ootdMustHavePerfume() throws Exception {
        // given
        CreateOotdDto request = new CreateOotdDto("test content", 10, List.of(), List.of("태그1", "태그2"));
        MockMultipartFile image1 = new MockMultipartFile("images", "1.png",
                MediaType.IMAGE_JPEG_VALUE, "1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "2.png",
                MediaType.IMAGE_JPEG_VALUE, "2".getBytes());
        MockMultipartFile image3 = new MockMultipartFile("images", "3.png",
                MediaType.IMAGE_JPEG_VALUE, "3".getBytes());
        MockMultipartFile json = new MockMultipartFile("createOotdDto", "createOotdDto.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/ootds")
                        .file(image1)
                        .file(image2)
                        .file(image3)
                        .file(json)
                        .with(csrf()))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("향수는 최소 1개에서 최대 3개까지 등록해야 합니다"));
    }

    @DisplayName("OOTD 게시글을 작성할 때 향수는 최대 3개까지 등록할 수 있다.")
    @Test
    void ootdPerfumeIsGreaterTahn1SmallerOrEqualThan3() throws Exception {
        // given
        CreateOotdDto request = new CreateOotdDto("test content", 10, List.of(10L, 20L, 30L, 40L), List.of("태그1", "태그2"));
        MockMultipartFile image1 = new MockMultipartFile("images", "1.png",
                MediaType.IMAGE_JPEG_VALUE, "1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "2.png",
                MediaType.IMAGE_JPEG_VALUE, "2".getBytes());
        MockMultipartFile image3 = new MockMultipartFile("images", "3.png",
                MediaType.IMAGE_JPEG_VALUE, "3".getBytes());
        MockMultipartFile json = new MockMultipartFile("createOotdDto", "createOotdDto.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/ootds")
                        .file(image1)
                        .file(image2)
                        .file(image3)
                        .file(json)
                        .with(csrf()))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("향수는 최소 1개에서 최대 3개까지 등록해야 합니다"));
    }

    @DisplayName("OOTD 게시글을 작성할 때 이미지를 반드시 첨부해야 한다.")
    @Test
    void ootdMustHaveImage() throws Exception {
        // given
        CreateOotdDto request = new CreateOotdDto("test content", 10, List.of(10L), List.of("태그1", "태그2"));
        MockMultipartFile json = new MockMultipartFile("createOotdDto", "createOotdDto.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/ootds")
                        .file(json)
                        .with(csrf()))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message")
                        .value("이미지는 jpg, jpeq, png만 지원하며 최소 1장에서 최대 5장까지 첨부해야 합니다."));
    }

    @DisplayName("OOTD 게시글을 작성할 때 이미지를 최대 5장까지 첨부할 수 있다.")
    @Test
    void ootdImageIsSmallerOrEqualThan5() throws Exception {
        // given
        CreateOotdDto request = new CreateOotdDto("test content", 10, List.of(10L), List.of("태그1", "태그2"));
        MockMultipartFile image1 = new MockMultipartFile("images", "1.png",
                MediaType.IMAGE_JPEG_VALUE, "1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "2.png",
                MediaType.IMAGE_JPEG_VALUE, "2".getBytes());
        MockMultipartFile image3 = new MockMultipartFile("images", "3.png",
                MediaType.IMAGE_JPEG_VALUE, "3".getBytes());
        MockMultipartFile image4 = new MockMultipartFile("images", "4.png",
                MediaType.IMAGE_JPEG_VALUE, "1".getBytes());
        MockMultipartFile image5 = new MockMultipartFile("images", "5.png",
                MediaType.IMAGE_JPEG_VALUE, "2".getBytes());
        MockMultipartFile image6 = new MockMultipartFile("images", "6.png",
                MediaType.IMAGE_JPEG_VALUE, "3".getBytes());
        MockMultipartFile json = new MockMultipartFile("createOotdDto", "createOotdDto.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/ootds")
                        .file(image1)
                        .file(image2)
                        .file(image3)
                        .file(image4)
                        .file(image5)
                        .file(image6)
                        .file(json)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message")
                        .value("이미지는 jpg, jpeq, png만 지원하며 최소 1장에서 최대 5장까지 첨부해야 합니다."));
    }

    @DisplayName("OOTD 이미지 형식은 PNG, JPG, JPEG 중 하나이다.")
    @Test
    void ootdImageExtensionIsPngOrJpgOrJpeg() throws Exception {
        // given
        CreateOotdDto request = new CreateOotdDto("test content", 10, List.of(10L), List.of("태그1", "태그2"));
        MockMultipartFile image1 = new MockMultipartFile("images", "1.gif",
                MediaType.IMAGE_JPEG_VALUE, "1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "2.jpg",
                MediaType.IMAGE_JPEG_VALUE, "2".getBytes());
        MockMultipartFile image3 = new MockMultipartFile("images", "3.jpeq",
                MediaType.IMAGE_JPEG_VALUE, "3".getBytes());
        MockMultipartFile image4 = new MockMultipartFile("images", "4.png",
                MediaType.IMAGE_JPEG_VALUE, "1".getBytes());
        MockMultipartFile image5 = new MockMultipartFile("images", "5.png",
                MediaType.IMAGE_JPEG_VALUE, "2".getBytes());
        MockMultipartFile json = new MockMultipartFile("createOotdDto", "createOotdDto.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/ootds")
                        .file(image1)
                        .file(image2)
                        .file(image3)
                        .file(image4)
                        .file(image5)
                        .file(json)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message")
                        .value("이미지는 jpg, jpeq, png만 지원하며 최소 1장에서 최대 5장까지 첨부해야 합니다."));
    }

    @DisplayName("OOTD 게시글 아이디를 기반으로 OOTD의 세부 내용과 이미지들을 조회한다.")
    @Test
    void getOotdDetail() throws Exception {
        // given
        OotdDetailDto result = new OotdDetailDto(null, null, null);

        Mockito.when(ootdService.getOotdDetailByOotdId(anyLong(), anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ootds/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("자유게시판의 게시글 ID에 달린 댓글을 조회한다.")
    @Test
    void getComments() throws Exception {
        // given
        CommentsDto result = new CommentsDto(null, 0);
        Mockito.when(ootdService.getCommentsByOotdId(anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ootds/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("OOTD 게시글에 좋아요를 눌러서 좋아요를 요청한다.")
    @Test
    void sendLikeToOotd() throws Exception {
        // given
        Mockito.doNothing().when(ootdService).sendLikeToOotd(anyLong(), anyLong());

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ootds/1/like").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("OOTD 게시글에 내용이 없는(공백으로 찬) 댓글은 달 수 없다.")
    @Test
    void replyWithNoContentsIsNotPermitted() throws Exception {
        // given
        OotdCommentRequestDto ootdComment = new OotdCommentRequestDto(null, "");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ootds/1/comments").with(csrf())
                        .content(objectMapper.writeValueAsString(ootdComment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("댓글은 공백이 아닌 1자 이상이 포함되어야 합니다"));
    }

    @DisplayName("OOTD 게시글의 댓글에 좋아요를 눌러서 좋아요를 요청한다.")
    @Test
    void sendLikeToOotdComment() throws Exception {
        // given
        Mockito.doNothing().when(ootdService).sendLikeToOotdComment(anyLong(), anyLong());

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ootds/1/comments/0/like").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }
}