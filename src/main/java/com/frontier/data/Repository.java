package com.frontier.data;

/**
 * 도메인 저장소 공통 규약.
 * PlayerDataRepository, BaseRepository, ResearchRepository 등이 구현한다.
 */
public interface Repository<T, ID> {

    /**
     * 데이터를 불러온다.
     */
    T load(ID id);

    /**
     * 데이터를 저장한다.
     */
    void save(T data);

    /**
     * 데이터 존재 여부 확인.
     */
    boolean exists(ID id);

    /**
     * 데이터를 삭제한다.
     */
    void delete(ID id);
}