package com.example.pms.integration;

import com.example.pms.TestcontainersConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

/**
 * API インテグレーションテストの基底クラス.
 * 実際の HTTP リクエストを使用してテストを実行する。
 * TestContainers を使用して PostgreSQL コンテナを起動し、
 * Spring Boot の @ServiceConnection で自動的にデータソースを設定する。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@SuppressWarnings({"PMD.AbstractClassWithoutAbstractMethod", "PMD.TooManyMethods"})
public abstract class IntegrationTestBase {

    @LocalServerPort
    protected int port;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private RestClient restClient;

    /**
     * 継承のみを許可するための protected コンストラクタ。
     */
    protected IntegrationTestBase() {
        // TestContainers の基底クラスのため空実装
    }

    /**
     * REST クライアントを取得.
     * ベース URL は実行中のサーバーのポートを使用.
     *
     * @return RestClient インスタンス
     */
    protected RestClient getRestClient() {
        if (restClient == null) {
            restClient = RestClient.builder()
                    .baseUrl("http://localhost:" + port)
                    .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .build();
        }
        return restClient;
    }

    /**
     * 品目関連データのクリーンアップ.
     * テスト用データ（TEST%プレフィックス）のみを削除する。
     */
    protected void cleanupItemData() {
        jdbcTemplate.execute("DELETE FROM \"品目マスタ\" WHERE \"品目コード\" LIKE 'TEST%'");
    }

    /**
     * 発注関連データのクリーンアップ.
     */
    protected void cleanupPurchaseOrderData() {
        jdbcTemplate.execute("DELETE FROM \"発注明細データ\" WHERE \"発注番号\" LIKE 'TEST%' OR \"発注番号\" LIKE 'PO-%'");
        jdbcTemplate.execute("DELETE FROM \"発注データ\" WHERE \"発注番号\" LIKE 'TEST%' OR \"発注番号\" LIKE 'PO-%'");
    }

    /**
     * 作業指示関連データのクリーンアップ.
     */
    protected void cleanupWorkOrderData() {
        jdbcTemplate.execute("DELETE FROM \"作業指示明細データ\" WHERE \"作業指示番号\" LIKE 'TEST%' OR \"作業指示番号\" LIKE 'WO-%'");
        jdbcTemplate.execute("DELETE FROM \"作業指示データ\" WHERE \"作業指示番号\" LIKE 'TEST%' OR \"作業指示番号\" LIKE 'WO-%'");
    }

    /**
     * テスト用品目を作成.
     *
     * @param itemCode 品目コード
     * @param itemName 品目名
     * @param itemCategory 品目区分
     */
    protected void createItem(String itemCode, String itemName, String itemCategory) {
        var today = LocalDate.now();
        jdbcTemplate.update(
                "INSERT INTO \"品目マスタ\" "
                        + "(\"品目コード\", \"適用開始日\", \"品名\", \"品目区分\", \"単位コード\") "
                        + "VALUES (?, ?, ?, ?::\"品目区分\", ?) "
                        + "ON CONFLICT (\"品目コード\", \"適用開始日\") DO NOTHING",
                itemCode, today, itemName, itemCategory, "個"
        );
    }

    /**
     * テスト用仕入先を作成.
     *
     * @param supplierCode 仕入先コード
     * @param supplierName 仕入先名
     */
    protected void createSupplier(String supplierCode, String supplierName) {
        var today = LocalDate.now();
        jdbcTemplate.update(
                "INSERT INTO \"取引先マスタ\" "
                        + "(\"取引先コード\", \"適用開始日\", \"取引先名\", \"取引先区分\") "
                        + "VALUES (?, ?, ?, '仕入先'::\"取引先区分\") "
                        + "ON CONFLICT (\"取引先コード\", \"適用開始日\") DO NOTHING",
                supplierCode, today, supplierName
        );
    }

    /**
     * テスト用場所を作成.
     *
     * @param locationCode 場所コード
     * @param locationName 場所名
     */
    protected void createLocation(String locationCode, String locationName) {
        jdbcTemplate.update(
                "INSERT INTO \"場所マスタ\" "
                        + "(\"場所コード\", \"場所名\", \"場所区分\") "
                        + "VALUES (?, ?, '倉庫'::\"場所区分\") "
                        + "ON CONFLICT (\"場所コード\") DO NOTHING",
                locationCode, locationName
        );
    }

    /**
     * テスト用単位を作成.
     *
     * @param unitCode 単位コード
     * @param unitSymbol 単位記号
     * @param unitName 単位名
     */
    protected void createUnit(String unitCode, String unitSymbol, String unitName) {
        jdbcTemplate.update(
                "INSERT INTO \"単位マスタ\" "
                        + "(\"単位コード\", \"単位記号\", \"単位名\") "
                        + "VALUES (?, ?, ?) "
                        + "ON CONFLICT (\"単位コード\") DO NOTHING",
                unitCode, unitSymbol, unitName
        );
    }

    /**
     * テスト用オーダ情報を作成.
     *
     * @param orderNo オーダNO
     * @param itemCode 品目コード
     * @param locationCode 場所コード
     */
    protected void createOrder(String orderNo, String itemCode, String locationCode) {
        var today = LocalDate.now();
        jdbcTemplate.update(
                "INSERT INTO \"オーダ情報\" "
                        + "(\"オーダNO\", \"オーダ種別\", \"品目コード\", \"着手予定日\", \"納期\", \"計画数量\", \"場所コード\", \"ステータス\") "
                        + "VALUES (?, ?::\"オーダ種別\", ?, ?, ?, ?, ?, '草案'::\"計画ステータス\") "
                        + "ON CONFLICT (\"オーダNO\") DO NOTHING",
                orderNo, "製造", itemCode, today, today.plusDays(7), 100, locationCode
        );
    }

    /**
     * オーダ関連データのクリーンアップ.
     */
    protected void cleanupOrderData() {
        jdbcTemplate.execute("DELETE FROM \"オーダ情報\" WHERE \"オーダNO\" LIKE 'TEST%' OR \"オーダNO\" LIKE 'TST-%'");
    }
}
