package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.LocationRepository;
import com.example.pms.domain.model.location.Location;
import com.example.pms.domain.model.location.LocationType;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 場所マスタリポジトリテスト.
 */
@DisplayName("場所マスタリポジトリ")
class LocationRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
    }

    private Location createLocation(String code, String name, LocationType type, String parentCode) {
        return Location.builder()
                .locationCode(code)
                .locationName(name)
                .locationType(type)
                .parentLocationCode(parentCode)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("場所を登録できる")
        void canRegisterLocation() {
            // Arrange
            Location location = createLocation("WH001", "本社倉庫", LocationType.WAREHOUSE, null);

            // Act
            locationRepository.save(location);

            // Assert
            Optional<Location> found = locationRepository.findByLocationCode("WH001");
            assertThat(found).isPresent();
            assertThat(found.get().getLocationName()).isEqualTo("本社倉庫");
            assertThat(found.get().getLocationType()).isEqualTo(LocationType.WAREHOUSE);
        }

        @Test
        @DisplayName("各場所区分を登録できる")
        void canRegisterAllLocationTypes() {
            // Arrange & Act & Assert
            for (LocationType type : LocationType.values()) {
                Location location = createLocation("LOC_" + type.name(),
                        "テスト " + type.getDisplayName(), type, null);
                locationRepository.save(location);

                Optional<Location> found = locationRepository.findByLocationCode("LOC_" + type.name());
                assertThat(found).isPresent();
                assertThat(found.get().getLocationType()).isEqualTo(type);
            }
        }

        @Test
        @DisplayName("親子関係のある場所を登録できる")
        void canRegisterLocationWithParent() {
            // Arrange
            Location parent = createLocation("WH001", "本社倉庫", LocationType.WAREHOUSE, null);
            locationRepository.save(parent);

            Location child = createLocation("WH001-A", "本社倉庫A棟", LocationType.WAREHOUSE, "WH001");

            // Act
            locationRepository.save(child);

            // Assert
            Optional<Location> found = locationRepository.findByLocationCode("WH001-A");
            assertThat(found).isPresent();
            assertThat(found.get().getParentLocationCode()).isEqualTo("WH001");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            locationRepository.save(createLocation("WH001", "本社倉庫", LocationType.WAREHOUSE, null));
            locationRepository.save(createLocation("MF001", "第一製造ライン", LocationType.MANUFACTURING, null));
            locationRepository.save(createLocation("MF002", "第二製造ライン", LocationType.MANUFACTURING, null));
            locationRepository.save(createLocation("INS001", "検査場", LocationType.INSPECTION, null));
        }

        @Test
        @DisplayName("場所コードで検索できる")
        void canFindByLocationCode() {
            // Act
            Optional<Location> found = locationRepository.findByLocationCode("MF001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getLocationName()).isEqualTo("第一製造ライン");
        }

        @Test
        @DisplayName("場所区分で検索できる")
        void canFindByLocationType() {
            // Act
            List<Location> found = locationRepository.findByLocationType(LocationType.MANUFACTURING);

            // Assert
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Location> found = locationRepository.findAll();

            // Assert
            assertThat(found).hasSize(4);
        }

        @Test
        @DisplayName("存在しないコードで検索すると空を返す")
        void returnsEmptyForNonExistent() {
            // Act
            Optional<Location> found = locationRepository.findByLocationCode("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("場所を更新できる")
        void canUpdateLocation() {
            // Arrange
            Location location = createLocation("WH001", "本社倉庫", LocationType.WAREHOUSE, null);
            locationRepository.save(location);

            // Act
            Optional<Location> saved = locationRepository.findByLocationCode("WH001");
            assertThat(saved).isPresent();
            Location toUpdate = saved.get();
            toUpdate.setLocationName("本社第一倉庫");
            locationRepository.update(toUpdate);

            // Assert
            Optional<Location> updated = locationRepository.findByLocationCode("WH001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getLocationName()).isEqualTo("本社第一倉庫");
        }
    }
}
