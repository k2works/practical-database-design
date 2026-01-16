package com.example.pms.domain.model.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private String locationCode;
    private String locationName;
    private LocationType locationType;
    private String parentLocationCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
