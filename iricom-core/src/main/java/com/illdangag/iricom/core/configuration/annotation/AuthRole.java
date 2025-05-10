package com.illdangag.iricom.core.configuration.annotation;

public enum AuthRole {
    SYSTEM_ADMIN, // 전체 시스템 관리자, 가장 상위 등급
    BOARD_ADMIN, // 게시판 관리자, 각 게시판의 관리자 등급
    ACCOUNT, // 일반 계정
    UNREGISTERED_ACCOUNT, // 처음 로그인 하여 계정 정보가 입력되지 않은 계정
    NONE, // 권한을 확인하지 않음
}
