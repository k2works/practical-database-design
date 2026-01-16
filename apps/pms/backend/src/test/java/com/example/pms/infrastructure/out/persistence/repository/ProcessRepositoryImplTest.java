package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.LocationRepository;
import com.example.pms.application.port.out.ProcessRepository;
import com.example.pms.domain.model.location.Location;
import com.example.pms.domain.model.location.LocationType;
import com.example.pms.domain.model.process.Process;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("工程マスタリポジトリ")
class ProcessRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        processRepository.deleteAll();
        locationRepository.deleteAll();
    }

    private void setupLocation() {
        locationRepository.save(Location.builder()
                .locationCode("MF001")
                .locationName("製造ライン1")
                .locationType(LocationType.MANUFACTURING)
                .build());
    }

    private Process createProcess(String code, String name, String locCode) {
        return Process.builder()
                .processCode(code)
                .processName(name)
                .processType("製造")
                .locationCode(locCode)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("工程を登録できる")
        void canRegisterProcess() {
            setupLocation();
            Process process = createProcess("P001", "組立工程", "MF001");
            processRepository.save(process);

            Optional<Process> found = processRepository.findByProcessCode("P001");
            assertThat(found).isPresent();
            assertThat(found.get().getProcessName()).isEqualTo("組立工程");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            setupLocation();
            processRepository.save(createProcess("P001", "組立工程", "MF001"));
            processRepository.save(createProcess("P002", "検査工程", "MF001"));
        }

        @Test
        @DisplayName("工程コードで検索できる")
        void canFindByProcessCode() {
            Optional<Process> found = processRepository.findByProcessCode("P001");
            assertThat(found).isPresent();
            assertThat(found.get().getProcessName()).isEqualTo("組立工程");
        }

        @Test
        @DisplayName("存在しない工程コードで検索すると空を返す")
        void returnsEmptyForNonExistentProcessCode() {
            Optional<Process> found = processRepository.findByProcessCode("NOTEXIST");
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<Process> found = processRepository.findAll();
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("工程を更新できる")
        void canUpdateProcess() {
            setupLocation();
            processRepository.save(createProcess("P001", "組立工程", "MF001"));

            Optional<Process> saved = processRepository.findByProcessCode("P001");
            assertThat(saved).isPresent();
            Process toUpdate = saved.get();
            toUpdate.setProcessName("組立工程（更新）");
            processRepository.update(toUpdate);

            Optional<Process> updated = processRepository.findByProcessCode("P001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getProcessName()).isEqualTo("組立工程（更新）");
        }
    }
}
