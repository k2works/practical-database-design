package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.LotCompositionRepository;
import com.example.pms.domain.model.quality.LotComposition;
import com.example.pms.infrastructure.out.persistence.mapper.LotCompositionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ロット構成リポジトリ実装.
 */
@Repository
public class LotCompositionRepositoryImpl implements LotCompositionRepository {

    private final LotCompositionMapper lotCompositionMapper;

    public LotCompositionRepositoryImpl(LotCompositionMapper lotCompositionMapper) {
        this.lotCompositionMapper = lotCompositionMapper;
    }

    @Override
    public void save(LotComposition composition) {
        lotCompositionMapper.insert(composition);
    }

    @Override
    public Optional<LotComposition> findById(Integer id) {
        return Optional.ofNullable(lotCompositionMapper.findById(id));
    }

    @Override
    public List<LotComposition> findByParentLotNumber(String parentLotNumber) {
        return lotCompositionMapper.findByParentLotNumber(parentLotNumber);
    }

    @Override
    public List<LotComposition> findByChildLotNumber(String childLotNumber) {
        return lotCompositionMapper.findByChildLotNumber(childLotNumber);
    }

    @Override
    public List<LotComposition> findAll() {
        return lotCompositionMapper.findAll();
    }

    @Override
    public void update(LotComposition composition) {
        lotCompositionMapper.update(composition);
    }

    @Override
    public void deleteById(Integer id) {
        lotCompositionMapper.deleteById(id);
    }

    @Override
    public void deleteByParentLotNumber(String parentLotNumber) {
        lotCompositionMapper.deleteByParentLotNumber(parentLotNumber);
    }

    @Override
    public void deleteByChildLotNumber(String childLotNumber) {
        lotCompositionMapper.deleteByChildLotNumber(childLotNumber);
    }
}
