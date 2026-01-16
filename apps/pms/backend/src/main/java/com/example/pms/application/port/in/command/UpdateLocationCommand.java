package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.location.LocationType;
import lombok.Builder;
import lombok.Value;

/**
 * 場所更新コマンド.
 */
@Value
@Builder
public class UpdateLocationCommand {
    String locationName;
    LocationType locationType;
    String parentLocationCode;
}
