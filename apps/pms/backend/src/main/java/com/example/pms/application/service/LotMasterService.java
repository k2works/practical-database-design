package com.example.pms.application.service;

import com.example.pms.application.port.in.LotMasterUseCase;
import com.example.pms.application.port.in.command.CreateLotMasterCommand;
import com.example.pms.application.port.in.command.UpdateLotMasterCommand;
import com.example.pms.application.port.out.LotMasterRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.quality.LotMaster;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ロットマスタサービス.
 */
@Service
@Transactional
public class LotMasterService implements LotMasterUseCase {

    private final LotMasterRepository lotMasterRepository;

    public LotMasterService(LotMasterRepository lotMasterRepository) {
        this.lotMasterRepository = lotMasterRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<LotMaster> getLotMasterList(int page, int size, String keyword) {
        int offset = page * size;
        List<LotMaster> content = lotMasterRepository.findWithPagination(offset, size, keyword);
        long totalElements = lotMasterRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LotMaster> getLotMaster(String lotNumber) {
        return lotMasterRepository.findByLotNumber(lotNumber);
    }

    @Override
    public LotMaster createLotMaster(CreateLotMasterCommand command) {
        LotMaster lotMaster = LotMaster.builder()
            .lotNumber(command.lotNumber())
            .itemCode(command.itemCode())
            .lotType(command.lotType())
            .manufactureDate(command.manufactureDate())
            .expirationDate(command.expirationDate())
            .quantity(command.quantity())
            .warehouseCode(command.warehouseCode())
            .remarks(command.remarks())
            .build();
        lotMasterRepository.save(lotMaster);
        return lotMasterRepository.findByLotNumber(lotMaster.getLotNumber())
                .orElse(lotMaster);
    }

    @Override
    public LotMaster updateLotMaster(String lotNumber, UpdateLotMasterCommand command) {
        LotMaster lotMaster = LotMaster.builder()
            .lotNumber(lotNumber)
            .itemCode(command.itemCode())
            .lotType(command.lotType())
            .manufactureDate(command.manufactureDate())
            .expirationDate(command.expirationDate())
            .quantity(command.quantity())
            .warehouseCode(command.warehouseCode())
            .remarks(command.remarks())
            .build();
        lotMasterRepository.update(lotMaster);
        return lotMasterRepository.findByLotNumber(lotNumber)
                .orElse(lotMaster);
    }

    @Override
    public void deleteLotMaster(String lotNumber) {
        lotMasterRepository.deleteByLotNumber(lotNumber);
    }
}
