package com.frontier.data;

import java.io.IOException;

/**
 * 저장 방식(YAML, PostgreSQL 등)을 추상화하는 인터페이스.
 */
public interface DataStore<T, ID> {

    /**
     * 데이터를 불러온다.
     */
    T load(ID id) throws IOException;

    /**
     * 데이터를 저장한다.
     */
    void save(T data) throws IOException;

    /**
     * 데이터 존재 여부 확인.
     */
    boolean exists(ID id);

    /**
     * 데이터를 삭제한다.
     */
    void delete(ID id) throws IOException;
}