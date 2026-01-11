package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.DefectRepository;
import com.example.pms.domain.model.defect.Defect;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("欠点マスタリポジトリ")
class DefectRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private DefectRepository defectRepository;

    @BeforeEach
    void setUp() {
        defectRepository.deleteAll();
    }

    private Defect createDefect(String code, String name, String type) {
        return Defect.builder()
                .defectCode(code)
                .defectName(name)
                .defectType(type)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("欠点を登録できる")
        void canRegisterDefect() {
            Defect defect = createDefect("DEF001", "傷", "外観");
            defectRepository.save(defect);

            Optional<Defect> found = defectRepository.findByDefectCode("DEF001");
            assertThat(found).isPresent();
            assertThat(found.get().getDefectName()).isEqualTo("傷");
        }

        @Test
        @DisplayName("複数の欠点を登録できる")
        void canRegisterMultipleDefects() {
            defectRepository.save(createDefect("DEF001", "傷", "外観"));
            defectRepository.save(createDefect("DEF002", "寸法不良", "寸法"));
            defectRepository.save(createDefect("DEF003", "汚れ", "外観"));

            List<Defect> found = defectRepository.findAll();
            assertThat(found).hasSize(3);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            defectRepository.save(createDefect("DEF001", "傷", "外観"));
            defectRepository.save(createDefect("DEF002", "寸法不良", "寸法"));
        }

        @Test
        @DisplayName("欠点コードで検索できる")
        void canFindByDefectCode() {
            Optional<Defect> found = defectRepository.findByDefectCode("DEF002");
            assertThat(found).isPresent();
            assertThat(found.get().getDefectName()).isEqualTo("寸法不良");
        }

        @Test
        @DisplayName("存在しないコードで検索すると空を返す")
        void returnsEmptyForNonExistent() {
            Optional<Defect> found = defectRepository.findByDefectCode("NOTEXIST");
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<Defect> found = defectRepository.findAll();
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("欠点を更新できる")
        void canUpdateDefect() {
            defectRepository.save(createDefect("DEF001", "傷", "外観"));

            Optional<Defect> saved = defectRepository.findByDefectCode("DEF001");
            assertThat(saved).isPresent();
            Defect toUpdate = saved.get();
            toUpdate.setDefectName("傷（深刻）");
            defectRepository.update(toUpdate);

            Optional<Defect> updated = defectRepository.findByDefectCode("DEF001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getDefectName()).isEqualTo("傷（深刻）");
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {
        @Test
        @DisplayName("欠点を削除できる")
        void canDeleteDefect() {
            defectRepository.save(createDefect("DEF001", "傷", "外観"));
            defectRepository.save(createDefect("DEF002", "寸法不良", "寸法"));

            defectRepository.deleteByDefectCode("DEF001");

            assertThat(defectRepository.findByDefectCode("DEF001")).isEmpty();
            assertThat(defectRepository.findAll()).hasSize(1);
        }
    }
}
