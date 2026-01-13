package com.example.pms.application.service;

import com.example.pms.application.port.in.LocationUseCase;
import com.example.pms.application.port.out.LocationRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.location.Location;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 場所サービス（Application Service）.
 */
@Service
@Transactional
public class LocationService implements LocationUseCase {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Location> getLocations(int page, int size, String keyword) {
        int offset = page * size;
        List<Location> locations = locationRepository.findWithPagination(keyword, size, offset);
        long totalElements = locationRepository.count(keyword);
        return new PageResult<>(locations, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location createLocation(Location location) {
        locationRepository.save(location);
        return location;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Location> getLocation(String locationCode) {
        return locationRepository.findByLocationCode(locationCode);
    }

    @Override
    public Location updateLocation(String locationCode, Location location) {
        locationRepository.update(location);
        return location;
    }

    @Override
    public void deleteLocation(String locationCode) {
        locationRepository.deleteByLocationCode(locationCode);
    }
}
