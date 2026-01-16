package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.location.LocationType;

/**
 * 場所更新コマンド.
 */
public record UpdateLocationCommand(
    String locationName,
    LocationType locationType,
    String parentLocationCode
) {
}
