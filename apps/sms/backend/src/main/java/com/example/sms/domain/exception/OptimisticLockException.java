package com.example.sms.domain.exception;

/**
 * 楽観ロック例外.
 * 更新対象のエンティティが他のユーザーによって既に更新されている場合にスローされる。
 */
@SuppressWarnings({"PMD.MissingSerialVersionUID", "PMD.DataClass"})
public class OptimisticLockException extends RuntimeException {

    private final String entityName;
    private final Integer entityId;
    private final String entityIdentifier;
    private final Integer expectedVersion;
    private final Integer actualVersion;

    /**
     * エンティティが削除されている場合のコンストラクタ.
     *
     * @param entityName エンティティ名
     * @param entityId エンティティID
     */
    public OptimisticLockException(String entityName, Integer entityId) {
        super(String.format("%s (ID: %d) は既に削除されています", entityName, entityId));
        this.entityName = entityName;
        this.entityId = entityId;
        this.entityIdentifier = null;
        this.expectedVersion = null;
        this.actualVersion = null;
    }

    /**
     * 文字列識別子を使用する場合のコンストラクタ.
     *
     * @param entityName エンティティ名
     * @param identifier エンティティ識別子（番号など）
     */
    public OptimisticLockException(String entityName, String identifier) {
        super(String.format("%s (%s) は他のユーザーによって更新されています", entityName, identifier));
        this.entityName = entityName;
        this.entityId = null;
        this.entityIdentifier = identifier;
        this.expectedVersion = null;
        this.actualVersion = null;
    }

    /**
     * バージョン不一致の場合のコンストラクタ.
     *
     * @param entityName エンティティ名
     * @param entityId エンティティID
     * @param expectedVersion 期待したバージョン
     * @param actualVersion 実際のバージョン
     */
    public OptimisticLockException(String entityName, Integer entityId,
                                    Integer expectedVersion, Integer actualVersion) {
        super(String.format("%s (ID: %d) は他のユーザーによって更新されています。"
                + "期待バージョン: %d, 実際のバージョン: %d",
                entityName, entityId, expectedVersion, actualVersion));
        this.entityName = entityName;
        this.entityId = entityId;
        this.entityIdentifier = null;
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }

    public String getEntityName() {
        return entityName;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public Integer getExpectedVersion() {
        return expectedVersion;
    }

    public Integer getActualVersion() {
        return actualVersion;
    }
}
