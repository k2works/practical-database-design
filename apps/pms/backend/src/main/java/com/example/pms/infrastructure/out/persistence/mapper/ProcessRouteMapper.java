package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.process.ProcessRoute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProcessRouteMapper {
    void insert(ProcessRoute processRoute);
    Optional<ProcessRoute> findByItemCodeAndSequence(@Param("itemCode") String itemCode,
                                                      @Param("sequence") Integer sequence);
    List<ProcessRoute> findByItemCode(String itemCode);
    List<ProcessRoute> findAll();
    void update(ProcessRoute processRoute);
    void deleteByItemCode(String itemCode);
    void deleteAll();

    List<ProcessRoute> findWithPagination(
            @Param("itemCode") String itemCode,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long count(@Param("itemCode") String itemCode);

    void deleteByItemCodeAndSequence(@Param("itemCode") String itemCode,
                                     @Param("sequence") Integer sequence);
}
