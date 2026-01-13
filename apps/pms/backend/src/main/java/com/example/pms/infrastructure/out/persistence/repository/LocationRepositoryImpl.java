package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.LocationRepository;
import com.example.pms.domain.model.location.Location;
import com.example.pms.domain.model.location.LocationType;
import com.example.pms.infrastructure.out.persistence.mapper.LocationMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 場所マスタリポジトリ実装.
 */
@Repository
public class LocationRepositoryImpl implements LocationRepository {

    private final LocationMapper locationMapper;

    public LocationRepositoryImpl(LocationMapper locationMapper) {
        this.locationMapper = locationMapper;
    }

    @Override
    public void save(Location location) {
        locationMapper.insert(location);
    }

    @Override
    public Optional<Location> findByLocationCode(String locationCode) {
        return locationMapper.findByLocationCode(locationCode);
    }

    @Override
    public List<Location> findByLocationType(LocationType locationType) {
        return locationMapper.findByLocationType(locationType);
    }

    @Override
    public List<Location> findAll() {
        return locationMapper.findAll();
    }

    @Override
    public void update(Location location) {
        locationMapper.update(location);
    }

    @Override
    public void deleteByLocationCode(String locationCode) {
        locationMapper.deleteByLocationCode(locationCode);
    }

    @Override
    public void deleteAll() {
        locationMapper.deleteAll();
    }

    @Override
    public List<Location> findWithPagination(String keyword, int limit, int offset) {
        return locationMapper.findWithPagination(keyword, limit, offset);
    }

    @Override
    public long count(String keyword) {
        return locationMapper.count(keyword);
    }
}
