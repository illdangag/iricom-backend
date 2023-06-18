package com.illdangag.iricom.server.restdocs.snippet;

import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ResponseFieldSnippet {
    private static final Map<String, String> searchListMap = new LinkedHashMap<>();
    private static final Map<String, String> accountMap = new LinkedHashMap<>();
    private static final Map<String, String> postMap = new LinkedHashMap<>();
    private static final Map<String, String> postReportMap = new LinkedHashMap<>();

    static {
        searchListMap.put("total", "모든 결과 수");
        searchListMap.put("skip", "건너 뛸 결과 수");
        searchListMap.put("limit", "조회 할 최대 결과 수");

        accountMap.put("id", "아이디");
        accountMap.put("email", "이메일");
        accountMap.put("createDate", "생성일");
        accountMap.put("lastActivityDate", "최근 활동일");
        accountMap.put("nickname", "닉네임");
        accountMap.put("description", "설명");
        accountMap.put("auth", "권한");

        postMap.put("id", "아이디");
        postMap.put("type", "종류");
        postMap.put("createDate", "생성일");
        postMap.put("updateDate", "수정일");
        postMap.put("status", "상태");
        postMap.put("title", "제목");
        postMap.put("content", "내용");
        postMap.put("viewCount", "조회수");
        postMap.put("upvote", "좋아요");
        postMap.put("downvote", "싫어요");
        postMap.put("commentCount", "댓글 수");
        postMap.put("isAllowComment", "댓글 허용 여부");
        postMap.put("isPublish", "발행 여부");
        postMap.put("hasTemporary", "임시 저장 여부");
        postMap.put("boardId", "게시물이 작성된 게시판 아이디");
        postMap.put("isReport", "신고 여부");
        postMap.put("isBan", "차단 여부");
        postMap.put("account", "작성자");

        postReportMap.put("id", "아이디");
        postReportMap.put("createDate", "생성일");
        postReportMap.put("updateDate", "수정일");
        postReportMap.put("type", "종류");
        postReportMap.put("reason", "사유");
        postReportMap.put("post", "신고 게시물");
    }
    private ResponseFieldSnippet() {
    }

    private static List<FieldDescriptor> getFieldDescriptors(Map<String, String> keyDescriptionMap, String keyPrefix) {
        return keyDescriptionMap.entrySet().stream()
                .map(item -> fieldWithPath(keyPrefix + item.getKey()).description(item.getValue()))
                .collect(Collectors.toList());
    }

    private static Map<String, String> getKeyDescriptionMap(Map<String, String> sourceMap, String keyPrefix) {
        Map<String, String> resultMap = new LinkedHashMap<>();
        Set<String> keySet = sourceMap.keySet();
        for (String key : keySet) {
            resultMap.put(keyPrefix + key, sourceMap.get(key));
        }
        return resultMap;
    }

    public static List<FieldDescriptor> getSearchList(String keyPrefix) {
        return getFieldDescriptors(searchListMap, keyPrefix);
    }

    public static List<FieldDescriptor> getAccount(String keyPrefix) {
        return getFieldDescriptors(accountMap, keyPrefix);
    }

    public static List<FieldDescriptor> getPost(String keyPrefix, boolean hasContent) {
        Map<String, String> resultMap = new LinkedHashMap<>();
        resultMap.putAll(postMap);
        if (!hasContent) {
            resultMap.remove("content");
        }
        return getFieldDescriptors(resultMap, keyPrefix);
    }

    public static List<FieldDescriptor> getPostReport(String keyPrefix) {
        return getFieldDescriptors(postReportMap, keyPrefix);
    }
}
