package com.example.pms.application.service;

import com.example.pms.application.port.in.BomUseCase;
import com.example.pms.application.port.out.BomRepository;
import com.example.pms.domain.model.bom.Bom;
import com.example.pms.domain.model.bom.BomExplosion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * BOM サービス（Application Service）.
 */
@Service
@Transactional(readOnly = true)
public class BomService implements BomUseCase {

    private final BomRepository bomRepository;

    public BomService(BomRepository bomRepository) {
        this.bomRepository = bomRepository;
    }

    @Override
    public List<Bom> getBomByParentItem(String parentItemCode) {
        return bomRepository.findByParentItemCode(parentItemCode);
    }

    @Override
    public List<BomExplosion> explodeBom(String itemCode, BigDecimal quantity) {
        return bomRepository.explode(itemCode, quantity);
    }

    @Override
    public List<Bom> whereUsed(String childItemCode) {
        return bomRepository.findByChildItemCode(childItemCode);
    }
}
