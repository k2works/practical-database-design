package com.example.fas.application.port.in.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 勘定科目構成登録コマンド.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountStructureCommand {
    private String accountCode;
    private String parentCode;
}
