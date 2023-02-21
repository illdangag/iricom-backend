package com.illdangag.iricom.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * code는 8자리
 * message는 not null
 */
@AllArgsConstructor
@Getter
public enum IricomErrorCode {
    // auth: 0100XXXX
    NOT_REGISTERED_ACCOUNT("01000001", 401, "Invalid authorization."),
    NOT_REGISTERED_ACCOUNT_DETAIL("01000002", 401, "Invalid authorization."),
    NOT_REGISTERED_BOARD_ADMIN("01000003", 401, "Invalid authorization."),
    NOT_REGISTERED_SYSTEM_ADMIN("01000004", 401, "Invalid authorization."),

    // auth firebase: 0101XXXX
    NOT_EXIST_FIREBASE_ID_TOKEN("01010000", 401, "Invalid header."),
    INVALID_FIREBASE_ID_TOKEN("01010001", 401, "Parse token error."),
    // global error: 0102XXXX
    INVALID_REQUEST("01020000", 400, "Invalid request."),
    // board admin: 0103XXXX
    NOT_EXIST_BOARD_ADMIN("01030000", 400, "Invalid request."),

    // account: 02XXXXXX
    NOT_EXIST_ACCOUNT("02000000", 404, "Not exist account."),
    NOT_EXIST_ACCOUNT_DETAIL_INFO("02000001", 404, "Not exist account detail info."),
    MISSING_ACCOUNT_NICKNAME_FILED("02000002", 400, IricomErrorMessage.MESSING_REQUIRED_FIELDS + ": nickname"),
    ALREADY_ACCOUNT_NICKNAME("02000002", 400, "Already exists nickname."),

    // board: 03XXXXXX
    NOT_EXIST_BOARD("03000000", 404, "Not exist board."),

    // post: 04XXXXXX
    DISABLED_BOARD_TO_POST("04000000", 400, "Board is disabled."), // 게시판이 비활성화 되어 있어서 게시물 작성이 불가능
    INVALID_AUTHORIZATION_TO_NOTIFICATION("04000001", 401, "Invalid authorization."), // 해당 게시판에 공지 사항 게시물 권한 없음
    INVALID_AUTHORIZATION_TO_UPDATE_POST_OR_NOTIFICATION("04000002", 401, "Invalid authorization."), // 수정 하려는 게시물에 권한 없음
    NOT_EXIST_POST("04000002", 404, "Not exist post."), // 존재하지 않는 게시물
    INVALID_POST_STATE("04000003", 400, "Invalid state."),
    NOT_EXIST_TEMPORARY_CONTENT("04000004", 404, "Not exist temporary content."),
    NOT_EXIST_PUBLISH_CONTENT("04000005", 404, "Not exist publish content."),
    DISABLED_BOARD_TO_VOTE("04000005", 400, "Board is disabled."),
    ALREADY_VOTE_POST("04000006", 400, "Already vote post."),
    INVALID_VOTE_POST("04000007", 400, "Invalid vote type."),

    // comment: 05XXXXXX
    NOT_EXIST_COMMENT("05000000", 404, "Not exist comment."),
    NOT_EXIST_REFERENCE_COMMENT("05000001", 404, "Not exist reference comment."),
    NOT_ALLOW_COMMENT("05000002", 400, "This post does not allow comments."),
    DISABLED_BOARD_TO_COMMENT("05000003", 400, "Board is disabled."),
    INVALID_AUTHORIZATION_TO_UPDATE_COMMENT("05000004", 401, "Invalid authorization."),
    INVALID_AUTHORIZATION_TO_DELETE_COMMENT("05000005", 401, "Invalid authorization."),
    ALREADY_VOTE_COMMENT("05000006", 400, "Already vote comment."),
    INVALID_VOTE_COMMENT("05000007", 400, "Invalid vote type.");


    private final String code;
    private final int httpStatusCode;
    private final String message;

    @Override
    public String toString() {
        return "[" + this.code + "](" + this.httpStatusCode + ") " + this.message;
    }

    static class IricomErrorMessage {
        public static final String MESSING_REQUIRED_FIELDS = "Missing required fields";
        public static final String INVALID_SEARCH_OPTION = "Invalid search option.";
    }
}
