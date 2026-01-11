package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.DefectRepository;
import com.example.pms.domain.model.defect.Defect;
import com.example.pms.infrastructure.out.persistence.mapper.DefectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DefectRepositoryImpl implements DefectRepository {

    private final DefectMapper defectMapper;

    public DefectRepositoryImpl(DefectMapper defectMapper) {
        this.defectMapper = defectMapper;
    }

    @Override
    public void save(Defect defect) {
        defectMapper.insert(defect);
    }

    @Override
    public Optional<Defect> findByDefectCode(String defectCode) {
        return defectMapper.findByDefectCode(defectCode);
    }

    @Override
    public List<Defect> findAll() {
        return defectMapper.findAll();
    }

    @Override
    public void update(Defect defect) {
        defectMapper.update(defect);
    }

    @Override
    public void deleteByDefectCode(String defectCode) {
        defectMapper.deleteByDefectCode(defectCode);
    }

    @Override
    public void deleteAll() {
        defectMapper.deleteAll();
    }
}
