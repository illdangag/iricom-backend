package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.PostInfoSearch;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.MyInformation;
import com.illdangag.iricom.server.data.response.PostInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/v1/infos")
public class InformationController {
    private final PostService postService;

    @Autowired
    public InformationController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 내 계정 정보 조회
     */
    @ApiCallLog(apiCode = "IF_001")
    @Auth(role = AuthRole.UNREGISTERED_ACCOUNT)
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<MyInformation> getMyAccountInfo(@RequestContext Account account) {
        AccountInfo accountInfo = new AccountInfo(account);

        MyInformation myInformation = MyInformation.builder()
                .account(accountInfo)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(myInformation);
    }

    /**
     * 내 계정이 작성한 게시물 조회
     */
    @ApiCallLog(apiCode = "IF_002")
    @Auth(role = AuthRole.UNREGISTERED_ACCOUNT)
    @RequestMapping(method = RequestMethod.GET, value = "/posts")
    public ResponseEntity<PostInfoList> getMyPostInfoList(@RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                          @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                          @RequestParam(name = "title", defaultValue = "", required = false) String title,
                                                          @RequestContext Account account) {
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

        PostInfoSearch postInfoSearch = PostInfoSearch.builder()
                .skip(skip)
                .limit(limit)
                .title(title)
                .build();

        PostInfoList postInfoList = this.postService.getPostInfoList(account, postInfoSearch);
        return ResponseEntity.status(HttpStatus.OK).body(postInfoList);
    }
}
