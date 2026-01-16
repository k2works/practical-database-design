package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

/**
 * 工程更新コマンド.
 */
@Value
@Builder
public class UpdateProcessCommand {
    String processName;
    String processType;
    String locationCode;
}
