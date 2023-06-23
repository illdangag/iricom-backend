package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.PostBanInfoCreate;
import com.illdangag.iricom.server.data.request.PostBanInfoSearch;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.data.response.PostBanInfo;
import com.illdangag.iricom.server.service.BanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1/")
public class BanController {
    private final BanService banService;

    @Autowired
    public BanController(BanService banService) {
        this.banService = banService;
    }

    @ApiCallLog(apiCode = "BP_001")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.POST, value = "/ban/post/boards/{board_id}/posts/{post_id}")
    public ResponseEntity<PostBanInfo> banPost(@PathVariable(value = "board_id") String boardId,
                                               @PathVariable(value = "post_id") String postId,
                                               @RequestBody @Valid PostBanInfoCreate postBanInfoCreate,
                                               @RequestContext Account account) {
        PostBanInfo postBanInfo = this.banService.banPost(account, boardId, postId, postBanInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(postBanInfo);
    }
}
