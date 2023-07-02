package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.PostState;
import com.illdangag.iricom.server.data.entity.PostType;
import com.illdangag.iricom.server.data.entity.VoteType;
import com.illdangag.iricom.server.data.request.PostInfoCreate;
import com.illdangag.iricom.server.data.request.PostInfoSearch;
import com.illdangag.iricom.server.data.request.PostInfoUpdate;
import com.illdangag.iricom.server.data.request.PostInfoVote;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.data.response.PostInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1/boards/{board_id}")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 게시물 생성
     */
    @ApiCallLog(apiCode = "PS_001")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.POST, value = "/posts")
    public ResponseEntity<PostInfo> createPostInfo(@PathVariable(value = "board_id") String boardId,
                                                   @RequestBody @Valid PostInfoCreate postInfoCreate,
                                                   @RequestContext Account account) {
        PostInfo postInfo = this.postService.createPostInfo(account, boardId, postInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(postInfo);
    }

    /**
     * 게시물 목록 조회
     *
     * 발행된 게시물만 조회
     */
    @ApiCallLog(apiCode = "PS_002")
    @Auth(role = AuthRole.NONE)
    @RequestMapping(method = RequestMethod.GET, value = "/posts")
    public ResponseEntity<PostInfoList> getPostInfoList(@PathVariable(value = "board_id") String boardId,
                                                        @RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                        @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                        @RequestParam(name = "type", defaultValue = "post", required = false) String typeVariable,
                                                        @RequestParam(name = "title", defaultValue = "", required = false) String title,
                                                        @RequestContext Account account) {
        PostType type;
        int skip;
        int limit;

        try {
            skip = Integer.parseInt(skipVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Skip value is invalid.");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Limit value is invalid.");
        }

        try {
            type = PostType.setValue(typeVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Type value is invalid.");
        }

        PostInfoSearch postInfoSearch = PostInfoSearch.builder()
                .type(type)
                .skip(skip)
                .limit(limit)
                .title(title)
                .build();

        PostInfoList postInfoList;
        if (account != null) {
            postInfoList = this.postService.getPublishPostInfoList(account, boardId, postInfoSearch);
        } else {
            postInfoList = this.postService.getPublishPostInfoList(boardId, postInfoSearch);
        }

        return ResponseEntity.status(HttpStatus.OK).body(postInfoList);
    }

    /**
     * 게시물 정보 조회
     */
    @ApiCallLog(apiCode = "PS_003")
    @Auth(role = { AuthRole.NONE, AuthRole.ACCOUNT, })
    @RequestMapping(method = RequestMethod.GET, value = "/posts/{post_id}")
    public ResponseEntity<PostInfo> getPost(@PathVariable(value = "board_id") String boardId,
                                            @PathVariable(value = "post_id") String postId,
                                            @RequestParam(name = "state", defaultValue = "publish", required = false) String stateVariable,
                                            @RequestContext Account account) {
        PostState postState;
        try {
            postState = PostState.setValue(stateVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_POST_STATE);
        }

        PostInfo postInfo = this.postService.getPostInfo(account, boardId, postId, postState);
        return ResponseEntity.status(HttpStatus.OK).body(postInfo);
    }

    /**
     * 게시물 수정
     */
    @ApiCallLog(apiCode = "PS_004")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.PATCH, value = "/posts/{post_id}")
    public ResponseEntity<PostInfo> updatePost(@PathVariable(value = "board_id") String boardId,
                                               @PathVariable(value = "post_id") String postId,
                                               @RequestBody @Valid PostInfoUpdate postInfoUpdate,
                                               @RequestContext Account account) {
        PostInfo postInfo = this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(postInfo);
    }

    /**
     * 게시물 발행
     */
    @ApiCallLog(apiCode = "PS_005")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.POST, value = "/posts/{post_id}/publish")
    public ResponseEntity<PostInfo> publishPost(@PathVariable(value = "board_id") String boardIdVariable,
                                                @PathVariable(value = "post_id") String postIdVariable,
                                                @RequestContext Account account) {
        PostInfo postInfo = this.postService.publishPostInfo(account, boardIdVariable, postIdVariable);
        return ResponseEntity.status(HttpStatus.OK).body(postInfo);
    }

    /**
     * 게시물 삭제
     */
    @ApiCallLog(apiCode = "PS_006")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/posts/{post_id}")
    public ResponseEntity<PostInfo> deletePost(@PathVariable(value = "board_id") String boardId,
                                               @PathVariable(value = "post_id") String postId,
                                               @RequestContext Account account) {
        PostInfo postInfo = this.postService.deletePostInfo(account, boardId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(postInfo);
    }

    /**
     * 게시물 좋아요 싫어요
     */
    @ApiCallLog(apiCode = "PS_007")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.PATCH, value = "/posts/{post_id}/vote")
    public ResponseEntity<PostInfo> votePost(@PathVariable(value = "board_id") String boardId,
                                             @PathVariable(value = "post_id") String postId,
                                             @RequestBody PostInfoVote postInfoVote,
                                             @RequestContext Account account) {
        VoteType voteType = postInfoVote.getType();
        PostInfo postInfo = this.postService.votePost(account, boardId, postId, voteType);
        return ResponseEntity.status(HttpStatus.OK).body(postInfo);
    }
}
