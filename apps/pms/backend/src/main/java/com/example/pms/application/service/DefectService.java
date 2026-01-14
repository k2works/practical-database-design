package com.example.pms.application.service;

import com.example.pms.application.port.in.DefectUseCase;
import com.example.pms.application.port.out.DefectRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.defect.Defect;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 欠点マスタサービス（Application Service）.
 */
@Service
@Transactional
public class DefectService implements DefectUseCase {

    private final DefectRepository defectRepository;

    public DefectService(DefectRepository defectRepository) {
        this.defectRepository = defectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Defect> getDefectList(int page, int size, String keyword) {
        int offset = page * size;
        List<Defect> content = defectRepository.findWithPagination(offset, size, keyword);
        long totalElements = defectRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Defect> getAllDefects() {
        return defectRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Defect> getDefect(String defectCode) {
        return defectRepository.findByDefectCode(defectCode);
    }

    @Override
    public Defect createDefect(Defect defect) {
        defectRepository.save(defect);
        return defectRepository.findByDefectCode(defect.getDefectCode())
            .orElseThrow(() -> new IllegalStateException("欠点の登録に失敗しました"));
    }

    @Override
    public Defect updateDefect(String defectCode, Defect defect) {
        Defect existing = defectRepository.findByDefectCode(defectCode)
            .orElseThrow(() -> new IllegalStateException("欠点が見つかりません: " + defectCode));

        existing.setDefectName(defect.getDefectName());
        existing.setDefectCategory(defect.getDefectCategory());

        defectRepository.update(existing);
        return defectRepository.findByDefectCode(defectCode)
            .orElseThrow(() -> new IllegalStateException("欠点の更新に失敗しました"));
    }

    @Override
    public void deleteDefect(String defectCode) {
        defectRepository.deleteByDefectCode(defectCode);
    }
}
