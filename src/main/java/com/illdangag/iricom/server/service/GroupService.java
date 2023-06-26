package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.request.GroupInfoCreate;
import com.illdangag.iricom.server.data.request.GroupInfoSearch;
import com.illdangag.iricom.server.data.request.GroupInfoUpdate;
import com.illdangag.iricom.server.data.response.GroupInfo;
import com.illdangag.iricom.server.data.response.GroupInfoList;

import javax.validation.Valid;

public interface GroupService {
    GroupInfo createGroupInfo(@Valid GroupInfoCreate groupInfoCreate);

    GroupInfoList getGroupInfoList(@Valid GroupInfoSearch groupInfoSearch);

    GroupInfo getGroupInfo(String groupId);

    GroupInfo updateGroupInfo(String groupId, @Valid GroupInfoUpdate groupInfoUpdate);

    GroupInfo deleteGroupInfo(String groupId);

    GroupInfo addGroupBoard(String groupId, String boardId);

    GroupInfo removeGroupBoard(String groupId, String boardId);

    GroupInfo addGroupAccount(String groupId, String accountId);

    GroupInfo removeGroupAccount(String groupId, String accountId);
}
