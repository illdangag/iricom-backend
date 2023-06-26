package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.request.GroupInfoCreate;
import com.illdangag.iricom.server.data.request.GroupInfoSearch;
import com.illdangag.iricom.server.data.request.GroupInfoUpdate;
import com.illdangag.iricom.server.data.response.GroupInfo;
import com.illdangag.iricom.server.data.response.GroupInfoList;
import com.illdangag.iricom.server.service.GroupService;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {
    @Override
    public GroupInfo createGroupInfo(GroupInfoCreate groupInfoCreate) {
        return null;
    }

    @Override
    public GroupInfoList getGroupInfoList(GroupInfoSearch groupInfoSearch) {
        return null;
    }

    @Override
    public GroupInfo getGroupInfo(String groupId) {
        return null;
    }

    @Override
    public GroupInfo updateGroupInfo(String groupId, GroupInfoUpdate groupInfoUpdate) {
        return null;
    }

    @Override
    public GroupInfo deleteGroupInfo(String groupId) {
        return null;
    }

    @Override
    public GroupInfo addGroupBoard(String groupId, String boardId) {
        return null;
    }

    @Override
    public GroupInfo removeGroupBoard(String groupId, String boardId) {
        return null;
    }

    @Override
    public GroupInfo addGroupAccount(String groupId, String accountId) {
        return null;
    }

    @Override
    public GroupInfo removeGroupAccount(String groupId, String accountId) {
        return null;
    }
}
