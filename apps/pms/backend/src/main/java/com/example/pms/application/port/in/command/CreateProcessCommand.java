package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

/**
 * 工程登録コマンド.
 */
@Value
@Builder
public class CreateProcessCommand {
    String processCode;
    String processName;
    String processType;
    String locationCode;
}
