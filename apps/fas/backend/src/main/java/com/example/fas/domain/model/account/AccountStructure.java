package com.example.fas.domain.model.account;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 勘定科目構成エンティティ.
 * チルダ連結方式で階層構造を表現する.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStructure {

    /** パス区切り文字. */
    private static final String PATH_SEPARATOR = "~";

    /** 親科目取得に必要な最小パーツ数. */
    private static final int MIN_PARTS_FOR_PARENT = 2;

    private String accountCode;
    private String accountPath;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /**
     * パスの深さ（階層レベル）を取得する.
     *
     * @return 階層の深さ（1から始まる）
     */
    public int getDepth() {
        if (accountPath == null || accountPath.isEmpty()) {
            return 0;
        }
        return accountPath.split(PATH_SEPARATOR).length;
    }

    /**
     * 親科目コードを取得する.
     *
     * @return 親科目コード。ルート科目の場合はnull
     */
    public String getParentCode() {
        if (accountPath == null || !accountPath.contains(PATH_SEPARATOR)) {
            return null;
        }
        String[] parts = accountPath.split(PATH_SEPARATOR);
        if (parts.length < MIN_PARTS_FOR_PARENT) {
            return null;
        }
        return parts[parts.length - MIN_PARTS_FOR_PARENT];
    }
}
