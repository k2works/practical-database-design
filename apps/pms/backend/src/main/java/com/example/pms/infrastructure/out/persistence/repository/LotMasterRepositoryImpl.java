package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.LotMasterRepository;
import com.example.pms.domain.model.quality.LotMaster;
import com.example.pms.infrastructure.out.persistence.mapper.LotMasterMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ロットマスタリポジトリ実装.
 */
@Repository
public class LotMasterRepositoryImpl implements LotMasterRepository {

    private final LotMasterMapper lotMasterMapper;

    public LotMasterRepositoryImpl(LotMasterMapper lotMasterMapper) {
        this.lotMasterMapper = lotMasterMapper;
    }

    @Override
    public void save(LotMaster lot) {
        lotMasterMapper.insert(lot);
    }

    @Override
    public Optional<LotMaster> findById(Integer id) {
        return Optional.ofNullable(lotMasterMapper.findById(id));
    }

    @Override
    public Optional<LotMaster> findByLotNumber(String lotNumber) {
        return Optional.ofNullable(lotMasterMapper.findByLotNumber(lotNumber));
    }

    @Override
    public Optional<LotMaster> findByLotNumberWithCompositions(String lotNumber) {
        return Optional.ofNullable(lotMasterMapper.findByLotNumberWithCompositions(lotNumber));
    }

    @Override
    public List<LotMaster> findByItemCode(String itemCode) {
        return lotMasterMapper.findByItemCode(itemCode);
    }

    @Override
    public List<LotMaster> findAll() {
        return lotMasterMapper.findAll();
    }

    @Override
    public List<LotMaster> traceForward(String lotNumber) {
        return lotMasterMapper.traceForward(lotNumber);
    }

    @Override
    public List<LotMaster> traceBack(String lotNumber) {
        return lotMasterMapper.traceBack(lotNumber);
    }

    @Override
    public int update(LotMaster lot) {
        return lotMasterMapper.update(lot);
    }

    @Override
    public void deleteByLotNumber(String lotNumber) {
        lotMasterMapper.deleteByLotNumber(lotNumber);
    }

    @Override
    public void deleteAll() {
        lotMasterMapper.deleteAll();
    }
}
