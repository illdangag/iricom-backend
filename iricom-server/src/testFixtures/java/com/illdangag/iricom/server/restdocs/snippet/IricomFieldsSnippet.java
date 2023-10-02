package com.illdangag.iricom.server.restdocs.snippet;

import com.illdangag.iricom.server.test.data.ResponseField;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class IricomFieldsSnippet {
    private static final List<ResponseField> accountResponseFieldList = Arrays.asList(
            ResponseField.builder().path("id").description("아이디").type(JsonFieldType.STRING).build(),
            ResponseField.builder().path("email").description("이메일").type(JsonFieldType.STRING).build(),
            ResponseField.builder().path("createDate").description("생성일").type(JsonFieldType.NUMBER).build(),
            ResponseField.builder().path("lastActivityDate").description("최근 활동일").type(JsonFieldType.NUMBER).build(),
            ResponseField.builder().path("nickname").description("닉네임").type(JsonFieldType.STRING).build(),
            ResponseField.builder().path("description").description("설명").type(JsonFieldType.STRING).build(),
            ResponseField.builder().path("auth").description("권한").type(JsonFieldType.STRING).build()
    );

    private static final List<ResponseField> boardResponseFieldList = Arrays.asList(
            ResponseField.builder().path("id").description("아이디").build(),
            ResponseField.builder().path("title").description("제목").build(),
            ResponseField.builder().path("description").description("설명").build(),
            ResponseField.builder().path("enabled").description("활성화 여부").build(),
            ResponseField.builder().path("unDisclosed").description("비공개 여부").build(),
            ResponseField.builder().path("notificationOnly").description("공지사항 전용 여부").build()
    );

    private static final List<ResponseField> postResponseFieldList = Arrays.asList(
            ResponseField.builder().path("id").description("아이디").build(),
            ResponseField.builder().path("type").description("종류").build(),
            ResponseField.builder().path("createDate").description("생성일").build(),
            ResponseField.builder().path("updateDate").description("수정일").build(),
            ResponseField.builder().path("status").description("상태").build(),
            ResponseField.builder().path("title").description("제목").build(),
            ResponseField.builder().path("content").description("내용").isOptional(true).type(JsonFieldType.STRING).build(),
            ResponseField.builder().path("viewCount").description("조회수").build(),
            ResponseField.builder().path("upvote").description("좋아요").build(),
            ResponseField.builder().path("downvote").description("싫어요").build(),
            ResponseField.builder().path("commentCount").description("댓글 수").build(),
            ResponseField.builder().path("allowComment").description("댓글 허용 여부").build(),
            ResponseField.builder().path("publish").description("임시 저장 여부").build(),
            ResponseField.builder().path("hasTemporary").description("임시 저장 여부").build(),
            ResponseField.builder().path("boardId").description("게시물이 작성된 게시판 아이디").build(),
            ResponseField.builder().path("report").description("신고 여부").build(),
            ResponseField.builder().path("ban").description("차단 여부").build(),
            ResponseField.builder().path("account").description("작성자").build(),
            ResponseField.builder().path("deleted").description("삭제 여부").type(JsonFieldType.BOOLEAN).build()
    );

    private static final List<ResponseField> commentResponseFieldList = Arrays.asList(
            ResponseField.builder().path("id").description("아이디").type(JsonFieldType.STRING).build(),
            ResponseField.builder().path("content").description("내용").isOptional(true).type(JsonFieldType.STRING).build(),
            ResponseField.builder().path("referenceCommentId").description("상위 댓글 아이디").isOptional(true).type(JsonFieldType.STRING).build(),
            ResponseField.builder().path("createDate").description("작성일").type(JsonFieldType.NUMBER).build(),
            ResponseField.builder().path("updateDate").description("수정일").type(JsonFieldType.NUMBER).build(),
            ResponseField.builder().path("upvote").description("좋아요").type(JsonFieldType.NUMBER).build(),
            ResponseField.builder().path("downvote").description("싫어요").type(JsonFieldType.NUMBER).build(),
            ResponseField.builder().path("hasNestedComment").description("하위 댓글 여부").type(JsonFieldType.BOOLEAN).build(),
            ResponseField.builder().path("deleted").description("삭제 여부").type(JsonFieldType.BOOLEAN).build(),
            ResponseField.builder().path("report").description("신고 여부").type(JsonFieldType.BOOLEAN).build(),
            ResponseField.builder().path("nestedComments").description("대댓글 목록").isOptional(true).type(JsonFieldType.ARRAY).build()
    );

    private static final List<ResponseField> postReportResponseFieldList = Arrays.asList(
            ResponseField.builder().path("id").description("이이디").build(),
            ResponseField.builder().path("createDate").description("생성일").build(),
            ResponseField.builder().path("updateDate").description("수정일").build(),
            ResponseField.builder().path("type").description("종류").build(),
            ResponseField.builder().path("reason").description("종류").build(),
            ResponseField.builder().path("reporter").description("신고자").build(),
            ResponseField.builder().path("post").description("신고 게시물").build()
    );

    private static final List<ResponseField> commentReportResponseFieldList = Arrays.asList(
            ResponseField.builder().path("id").description("아이디").build(),
            ResponseField.builder().path("createDate").description("생성일").build(),
            ResponseField.builder().path("updateDate").description("수정일").build(),
            ResponseField.builder().path("type").description("종류").build(),
            ResponseField.builder().path("reason").description("사유").build(),
            ResponseField.builder().path("reporter").description("신고자").build(),
            ResponseField.builder().path("comment").description("신고 댓글").build()
    );

    private static final List<ResponseField> postBanResponseFieldList = Arrays.asList(
            ResponseField.builder().path("id").description("아이디").build(),
            ResponseField.builder().path("createDate").description("생성일").build(),
            ResponseField.builder().path("updateDate").description("수정일").build(),
            ResponseField.builder().path("reason").description("사유").build(),
            ResponseField.builder().path("enabled").description("활성화 여부").build()
    );

    private static final List<ResponseField> accountGroupResponseFieldList = Arrays.asList(
            ResponseField.builder().path("id").description("아이디").build(),
            ResponseField.builder().path("title").description("제목").build(),
            ResponseField.builder().path("description").description("설명").build(),
            ResponseField.builder().path("boards").description("게시판 목록").build(),
            ResponseField.builder().path("accounts").description("사용자 목록").build()
    );

    private static final List<ResponseField> searchListResponseFieldList = Arrays.asList(
            ResponseField.builder().path("total").description("모든 결과 수").build(),
            ResponseField.builder().path("skip").description("건너 뛸 결과 수").build(),
            ResponseField.builder().path("limit").description("조회 할 최대 결과 수").build()
    );

    protected IricomFieldsSnippet() {
    }

    private static List<FieldDescriptor> getFieldDescriptors(Map<String, String> keyDescriptionMap, String keyPrefix) {
        return keyDescriptionMap.entrySet().stream()
                .map(item -> fieldWithPath(keyPrefix + item.getKey())
                        .description(item.getValue()))
                .collect(Collectors.toList());
    }

    protected static List<FieldDescriptor> getFieldDescriptors(List<ResponseField> responseFieldList, String keyPrefix) {
        return responseFieldList.stream()
                .map(item -> {
                    FieldDescriptor fieldDescriptor = fieldWithPath(keyPrefix + item.getPath()).description(item.getDescription());
                    if (item.isOptional()) {
                        fieldDescriptor.optional();
                    }
                    if (item.getType() != null) {
                        fieldDescriptor.type(item.getType());
                    }
                    return fieldDescriptor;
                }).collect(Collectors.toList());
    }

    public static List<FieldDescriptor> getSearchList(String keyPrefix) {
        return getFieldDescriptors(searchListResponseFieldList, keyPrefix);
    }

    public static List<FieldDescriptor> getAccount(String keyPrefix) {
        return getFieldDescriptors(accountResponseFieldList, keyPrefix);
    }

    public static List<FieldDescriptor> getBoard(String keyPrefix) {
        return getFieldDescriptors(boardResponseFieldList, keyPrefix);
    }

    public static List<FieldDescriptor> getPost(String keyPrefix) {
        return getFieldDescriptors(postResponseFieldList, keyPrefix);
    }

    public static List<FieldDescriptor> getComment(String keyPrefix) {
        return getFieldDescriptors(commentResponseFieldList, keyPrefix);
    }

    public static List<FieldDescriptor> getPostReport(String keyPrefix) {
        return getFieldDescriptors(postReportResponseFieldList, keyPrefix);
    }

    public static List<FieldDescriptor> getCommentReport(String keyPrefix) {
        return getFieldDescriptors(commentReportResponseFieldList, keyPrefix);
    }

    public static List<FieldDescriptor> getPostBan(String keyPrefix) {
        return getFieldDescriptors(postBanResponseFieldList, keyPrefix);
    }

    public static List<FieldDescriptor> getAccountGroup(String keyPrefix) {
        return getFieldDescriptors(accountGroupResponseFieldList, keyPrefix);
    }
}
