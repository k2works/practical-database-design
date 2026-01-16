package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.location.Location;
import com.example.pms.domain.model.location.LocationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface LocationMapper {
    void insert(Location location);
    Optional<Location> findByLocationCode(String locationCode);
    List<Location> findByLocationType(LocationType locationType);
    List<Location> findAll();
    void update(Location location);
    void deleteByLocationCode(String locationCode);
    void deleteAll();
    List<Location> findWithPagination(@Param("keyword") String keyword,
                                      @Param("limit") int limit,
                                      @Param("offset") int offset);
    long count(@Param("keyword") String keyword);
}
