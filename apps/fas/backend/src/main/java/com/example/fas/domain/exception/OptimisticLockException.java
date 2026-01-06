package com.example.fas.domain.exception;

import lombok.Getter;

/**
 * 楽観ロック例外.
 * 他のユーザーによる更新または削除時にスローされる.
 */
@Getter
public class OptimisticLockException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String entityName;
    private final String entityId;
    private final Integer expectedVersion;
    private final Integer actualVersion;

    /**
     * 削除済みエンティティの場合のコンストラクタ.
     *
     * @param entityName エンティティ名
     * @param entityId エンティティID
     */
    public OptimisticLockException(String entityName, String entityId) {
        super(String.format("%s (ID: %s) は既に削除されています", entityName, entityId));
        this.entityName = entityName;
        this.entityId = entityId;
        this.expectedVersion = null;
        this.actualVersion = null;
    }

    /**
     * バージョン不一致の場合のコンストラクタ.
     *
     * @param entityName エンティティ名
     * @param entityId エンティティID
     * @param expectedVersion 期待バージョン
     * @param actualVersion 実際のバージョン
     */
    public OptimisticLockException(String entityName, String entityId,
                                   Integer expectedVersion, Integer actualVersion) {
        super(String.format("%s (ID: %s) は他のユーザーによって更新されています。"
                + "期待バージョン: %d, 実際のバージョン: %d",
                entityName, entityId, expectedVersion, actualVersion));
        this.entityName = entityName;
        this.entityId = entityId;
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }
}
