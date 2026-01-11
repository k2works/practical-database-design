package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ItemRepository;
import com.example.pms.application.port.out.LocationRepository;
import com.example.pms.application.port.out.ProcessRepository;
import com.example.pms.application.port.out.ProcessRouteRepository;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
import com.example.pms.domain.model.location.Location;
import com.example.pms.domain.model.location.LocationType;
import com.example.pms.domain.model.process.Process;
import com.example.pms.domain.model.process.ProcessRoute;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("工程表リポジトリ")
class ProcessRouteRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ProcessRouteRepository processRouteRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        processRouteRepository.deleteAll();
        processRepository.deleteAll();
        locationRepository.deleteAll();
        itemRepository.deleteAll();
    }

    private void setupMasterData() {
        locationRepository.save(Location.builder()
                .locationCode("MF001")
                .locationName("製造ライン1")
                .locationType(LocationType.MANUFACTURING)
                .build());
        processRepository.save(Process.builder()
                .processCode("P001")
                .processName("組立工程")
                .locationCode("MF001")
                .build());
        processRepository.save(Process.builder()
                .processCode("P002")
                .processName("検査工程")
                .locationCode("MF001")
                .build());
        itemRepository.save(Item.builder()
                .itemCode("PROD001")
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .itemName("製品A")
                .itemCategory(ItemCategory.PRODUCT)
                .build());
    }

    private ProcessRoute createProcessRoute(String itemCode, int seq, String processCode) {
        return ProcessRoute.builder()
                .itemCode(itemCode)
                .sequence(seq)
                .processCode(processCode)
                .standardTime(new BigDecimal("1.5"))
                .setupTime(new BigDecimal("0.5"))
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("工程表を登録できる")
        void canRegisterProcessRoute() {
            setupMasterData();
            ProcessRoute route = createProcessRoute("PROD001", 1, "P001");
            processRouteRepository.save(route);

            Optional<ProcessRoute> found = processRouteRepository.findByItemCodeAndSequence("PROD001", 1);
            assertThat(found).isPresent();
            assertThat(found.get().getProcessCode()).isEqualTo("P001");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            setupMasterData();
            processRouteRepository.save(createProcessRoute("PROD001", 1, "P001"));
            processRouteRepository.save(createProcessRoute("PROD001", 2, "P002"));
        }

        @Test
        @DisplayName("品目コードで検索できる")
        void canFindByItemCode() {
            List<ProcessRoute> found = processRouteRepository.findByItemCode("PROD001");
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<ProcessRoute> found = processRouteRepository.findAll();
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("工程表を更新できる")
        void canUpdateProcessRoute() {
            setupMasterData();
            processRouteRepository.save(createProcessRoute("PROD001", 1, "P001"));

            Optional<ProcessRoute> saved = processRouteRepository.findByItemCodeAndSequence("PROD001", 1);
            assertThat(saved).isPresent();
            ProcessRoute toUpdate = saved.get();
            toUpdate.setStandardTime(new BigDecimal("2.0"));
            processRouteRepository.update(toUpdate);

            Optional<ProcessRoute> updated = processRouteRepository.findByItemCodeAndSequence("PROD001", 1);
            assertThat(updated).isPresent();
            assertThat(updated.get().getStandardTime()).isEqualByComparingTo(new BigDecimal("2.0"));
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {
        @Test
        @DisplayName("品目コードで削除できる")
        void canDeleteByItemCode() {
            setupMasterData();
            processRouteRepository.save(createProcessRoute("PROD001", 1, "P001"));
            processRouteRepository.save(createProcessRoute("PROD001", 2, "P002"));

            processRouteRepository.deleteByItemCode("PROD001");

            List<ProcessRoute> found = processRouteRepository.findByItemCode("PROD001");
            assertThat(found).isEmpty();
        }
    }
}
