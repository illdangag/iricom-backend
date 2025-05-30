package com.illdangag.iricom.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * code는 8자리
 * message는 not null
 */
@AllArgsConstructor
@Getter
public enum IricomErrorCode implements IricomError {
    // common: 0000XXXX
    NOT_EXIST_REQUEST_BODY("00000001", 400, "Not exist request body."),
    INVALID_REQUEST_BODY("00000002", 400, "Invalid request body."),

    // auth: 0100XXXX
    NOT_REGISTERED_ACCOUNT("01000001", 401, "Invalid authorization."),
    NOT_REGISTERED_ACCOUNT_DETAIL("01000002", 401, "Invalid authorization."),
    NOT_REGISTERED_BOARD_ADMIN("01000003", 401, "Invalid authorization."),
    NOT_REGISTERED_SYSTEM_ADMIN("01000004", 401, "Invalid authorization."),

    // auth firebase: 0101XXXX
    NOT_EXIST_FIREBASE_ID_TOKEN("01010000", 401, "Not exist token."),
    INVALID_FIREBASE_ID_TOKEN("01010001", 401, "Parse token error."),
    EXPIRED_FIREBASE_ID_TOKEN("01010002", 401, "Expired token."),
    // global error: 0102XXXX
    INVALID_REQUEST("01020000", 400, "Invalid request."),
    // board admin: 0103XXXX
    NOT_EXIST_BOARD_ADMIN("01030000", 404, "Invalid request."),
    NOT_EXIST_ACCOUNT_DETAIL_TO_UPDATE_BOARD_ADMIN("01030001", 400, "Invalid request."),

    // account: 02XXXXXX
    NOT_EXIST_ACCOUNT("02000000", 404, "Not exist account."),
    NOT_EXIST_ACCOUNT_DETAIL_INFO("02000001", 404, "Not exist account detail info."),
    MISSING_ACCOUNT_NICKNAME_FILED("02000002", 400, "Missing required fields: nickname."),
    ALREADY_ACCOUNT_NICKNAME("02000002", 400, "Already exists nickname."),

    // board: 03XXXXXX
    NOT_EXIST_BOARD("03000000", 404, "Not exist board."),
    DISABLED_BOARD("03000001", 400, "Board is disabled."),
    INVALID_AUTHORIZATION_TO_CREATE_BOARD("03000002", 401, "Invalid authorization."),

    // post: 04XXXXXX
    NOT_EXIST_POST("04000000", 404, "Not exist post."), // 존재하지 않는 게시물
    INVALID_AUTHORIZATION_TO_NOTIFICATION("04000001", 401, "Invalid authorization."), // 해당 게시판에 공지 사항 게시물 권한 없음
    INVALID_AUTHORIZATION_TO_UPDATE_POST_OR_NOTIFICATION("04000002", 401, "Invalid authorization."), // 수정 하려는 게시물에 권한 없음
    INVALID_POST_STATE("04000003", 400, "Invalid state."),
    NOT_EXIST_TEMPORARY_CONTENT("04000004", 404, "Not exist temporary content."),
    NOT_EXIST_PUBLISH_CONTENT("04000005", 404, "Not exist publish content."),
    ALREADY_VOTE_POST("04000006", 400, "Already vote post."),
    INVALID_AUTHORIZATION_TO_GET_TEMPORARY_CONTENT("04000007", 401, "Invalid authorization."),
    NOT_EXIST_ACCOUNT_NICKNAME_TO_POST("04000008", 400, "Not exist nickname."),
    INVALID_AUTHORIZATION_TO_BLOCK_POST("04000009", 401, "Invalid authorization."),
    ALREADY_BLOCK_POST("04000010", 400, "Already blocked post."),
    ALREADY_UNBLOCK_POST("04000011", 400, "Already unblocked post."),
    INVALID_AUTHORIZATION_TO_POST_ONLY_NOTIFICATION_BOARD("04000012", 400, "Board is for notification only."),
    INVALID_UPDATE_POST_IN_NOTIFICATION_ONLY_BOARD("04000013", 400, "Board is for notification only."),
    BLOCKED_POST("04000014", 400, "This is a blocked post."),
    NOT_BLOCKED_POST("04000015", 400, "This post has not been blocked."),

    // comment: 05XXXXXX
    NOT_EXIST_COMMENT("05000000", 404, "Not exist comment."),
    NOT_EXIST_REFERENCE_COMMENT("05000001", 404, "Not exist reference comment."),
    NOT_ALLOW_COMMENT("05000002", 400, "This post does not allow comments."),
    INVALID_AUTHORIZATION_TO_UPDATE_COMMENT("05000003", 401, "Invalid authorization."),
    INVALID_AUTHORIZATION_TO_DELETE_COMMENT("05000004", 401, "Invalid authorization."),
    ALREADY_VOTE_COMMENT("05000005", 400, "Already vote comment."),
    INVALID_VOTE_COMMENT("05000006", 400, "Invalid vote type."),
    COMMENT_ON_BLOCKED_POST("05000007", 400, "This comment on a blocked post."),
    ALREADY_BLOCKED_COMMENT("05000008", 400, "Already blocked comment."),
    INVALID_AUTHORIZATION_TO_BLOCK_COMMENT("05000009", 401, "Invalid authorization."),
    NOT_BLOCK_COMMENT("050000010", 400, "This comment has not been blocked."),

    // report: 06XXXXXX
    // report post: 0600XXXX
    NOT_EXIST_POST_REPORT("06000000", 404, "Not exist post report."),
    ALREADY_REPORT_POST("06000001", 400, "Already report post."),
    INVALID_AUTHORIZATION_TO_GET_POST_REPORT_LIST("06000002", 401, "Invalid authorization."),
    // report comment: 0601XXXX
    NOT_EXIST_COMMENT_REPORT("06010000", 404, "Not exist comment report."),
    ALREADY_REPORT_COMMENT("06010001", 400, "Already report comment."),
    // report account: 0602XXXX

    // block: 07XXXXXX
    // block post: 0700XXXX
    NOT_EXIST_POST_BLOCK("07000000", 404, "Not exist post block."),
    // comment post: 0701XXXX
    NOT_EXIST_COMMENT_BLOCK("07010000", 404, "Not exist comment block."),

    // account group: 08XXXXXX
    NOT_EXIST_ACCOUNT_GROUP("08000000", 404, "Not exist account group."),
    INVALID_ACCOUNT_LIST("08000001", 400, "Invalid account id list."),
    INVALID_BOARD_LIST("08000002", 400, "Invalid board id list."),

    // personal message: 09XXXXXX
    NOT_EXIST_PERSONAL_MESSAGE("09000000", 404, "Not exist personal message.");

    private final String code;
    private final int httpStatusCode;
    private final String message;

    @Override
    public String toString() {
        return "[" + this.code + "](" + this.httpStatusCode + ") " + this.message;
    }
}
