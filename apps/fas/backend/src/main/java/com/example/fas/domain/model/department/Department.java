package com.example.fas.domain.model.department;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部門エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    private static final int MIN_ANCESTORS_FOR_PARENT = 2;

    /** 部門コード（5桁）. */
    private String departmentCode;

    /** 部門名. */
    private String departmentName;

    /** 部門略名. */
    private String departmentShortName;

    /** 組織階層（0:全社, 1:本部, 2:部, 3:課）. */
    private Integer organizationLevel;

    /** 部門パス（チルダ連結）. */
    private String departmentPath;

    /** 最下層フラグ（0:中間, 1:最下層）. */
    private Integer lowestLevelFlag;

    /**
     * 上位部門コードのリストを取得.
     *
     * @return 上位部門コードのリスト
     */
    public List<String> getAncestorCodes() {
        if (departmentPath == null || departmentPath.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(departmentPath.split("~"));
    }

    /**
     * 直上位の部門コードを取得.
     *
     * @return 親部門コード、ルートの場合はnull
     */
    public String getParentCode() {
        List<String> ancestors = getAncestorCodes();
        if (ancestors.size() < MIN_ANCESTORS_FOR_PARENT) {
            return null;
        }
        return ancestors.get(ancestors.size() - MIN_ANCESTORS_FOR_PARENT);
    }

    /**
     * 最下層かどうか.
     *
     * @return 最下層の場合true
     */
    public boolean isLowestLevel() {
        return lowestLevelFlag != null && lowestLevelFlag == 1;
    }

    /**
     * 指定部門の下位かどうか.
     *
     * @param ancestorCode 祖先部門コード
     * @return 下位の場合true
     */
    public boolean isDescendantOf(String ancestorCode) {
        return departmentPath != null && departmentPath.contains(ancestorCode);
    }
}
