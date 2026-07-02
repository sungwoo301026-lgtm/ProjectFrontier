package com.frontier.core;

/**
 * 모든 시스템 Manager가 구현하는 라이프사이클 규약.
 * Base, Research, Pet 등 이후 모든 시스템은 이 인터페이스를 구현한다.
 */
public interface Manager {

    /** 로그 및 조회용 이름 */
    String getName();

    /** 플러그인 활성화 시 호출. 실패 시 예외를 던지면 플러그인이 안전하게 중단된다. */
    void initialize() throws Exception;

    /** 플러그인 비활성화 시 역순으로 호출. 저장/정리 작업을 수행한다. */
    void shutdown();
}