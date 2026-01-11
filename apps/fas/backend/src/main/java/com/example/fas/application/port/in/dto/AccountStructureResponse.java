package com.example.fas.application.port.in.dto;

import com.example.fas.domain.model.account.AccountStructure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 勘定科目構成レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStructureResponse {
    private String accountCode;
    private String accountName;
    private String accountPath;
    private String parentCode;
    private int depth;

    /**
     * ドメインモデルからレスポンス DTO を生成.
     *
     * @param structure 勘定科目構成エンティティ
     * @param accountName 勘定科目名
     * @return レスポンス DTO
     */
    public static AccountStructureResponse from(AccountStructure structure, String accountName) {
        return AccountStructureResponse.builder()
                .accountCode(structure.getAccountCode())
                .accountName(accountName)
                .accountPath(structure.getAccountPath())
                .parentCode(structure.getParentCode())
                .depth(structure.getDepth())
                .build();
    }
}
