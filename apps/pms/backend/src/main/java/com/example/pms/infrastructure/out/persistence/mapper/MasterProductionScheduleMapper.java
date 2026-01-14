package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.plan.MasterProductionSchedule;
import com.example.pms.domain.model.plan.PlanStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MasterProductionScheduleMapper {
    void insert(MasterProductionSchedule mps);
    MasterProductionSchedule findById(Integer id);
    MasterProductionSchedule findByMpsNumber(String mpsNumber);
    /**
     * MPS番号で検索（オーダを含む）.
     */
    MasterProductionSchedule findByMpsNumberWithOrders(String mpsNumber);
    List<MasterProductionSchedule> findByStatus(PlanStatus status);
    List<MasterProductionSchedule> findAll();

    /**
     * ページネーション付きで基準生産計画を検索する.
     */
    List<MasterProductionSchedule> findWithPagination(
        @Param("status") PlanStatus status,
        @Param("keyword") String keyword,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    /**
     * 検索条件に合致する基準生産計画の件数を取得する.
     */
    long count(
        @Param("status") PlanStatus status,
        @Param("keyword") String keyword
    );

    /**
     * 基準生産計画を更新する.
     */
    void update(MasterProductionSchedule mps);

    void updateStatus(@Param("id") Integer id, @Param("status") PlanStatus status);
    void deleteAll();
}
