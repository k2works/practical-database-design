package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.location.LocationType;

/**
 * 場所登録コマンド.
 */
public record CreateLocationCommand(
    String locationCode,
    String locationName,
    LocationType locationType,
    String parentLocationCode
) {
}
