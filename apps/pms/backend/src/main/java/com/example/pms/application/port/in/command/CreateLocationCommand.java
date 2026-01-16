package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.location.LocationType;
import lombok.Builder;
import lombok.Value;

/**
 * 場所登録コマンド.
 */
@Value
@Builder
public class CreateLocationCommand {
    String locationCode;
    String locationName;
    LocationType locationType;
    String parentLocationCode;
}
