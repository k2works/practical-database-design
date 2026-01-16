package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.quality.LotMaster;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ロットマスタ Mapper.
 */
@Mapper
public interface LotMasterMapper {
    void insert(LotMaster lot);

    LotMaster findById(Integer id);

    LotMaster findByLotNumber(String lotNumber);

    LotMaster findByLotNumberWithCompositions(String lotNumber);

    List<LotMaster> findByItemCode(String itemCode);

    List<LotMaster> findAll();

    /**
     * ページネーションでロットマスタを取得する.
     *
     * @param offset オフセット
     * @param limit リミット
     * @param keyword 検索キーワード
     * @return ロットマスタリスト
     */
    List<LotMaster> findWithPagination(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("keyword") String keyword);

    /**
     * ロットマスタの件数を取得する.
     *
     * @param keyword 検索キーワード
     * @return 件数
     */
    long count(@Param("keyword") String keyword);

    List<LotMaster> traceForward(String lotNumber);

    List<LotMaster> traceBack(String lotNumber);

    int update(LotMaster lot);

    void deleteByLotNumber(String lotNumber);

    void deleteAll();
}
