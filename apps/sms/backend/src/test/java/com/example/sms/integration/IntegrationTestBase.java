package com.example.sms.integration;

import com.example.sms.TestcontainersConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

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
     * テストデータのクリーンアップ（トランザクションデータのみ）.
     */
    protected void cleanupTransactionData() {
        jdbcTemplate.execute("DELETE FROM \"入金消込明細\"");
        jdbcTemplate.execute("DELETE FROM \"前受金データ\"");
        jdbcTemplate.execute("DELETE FROM \"入金データ\"");
        jdbcTemplate.execute("DELETE FROM \"請求明細\"");
        jdbcTemplate.execute("DELETE FROM \"請求データ\"");
        jdbcTemplate.execute("DELETE FROM \"売上明細\"");
        jdbcTemplate.execute("DELETE FROM \"売上データ\"");
        jdbcTemplate.execute("DELETE FROM \"出荷明細\"");
        jdbcTemplate.execute("DELETE FROM \"出荷データ\"");
        jdbcTemplate.execute("DELETE FROM \"受注明細\"");
        jdbcTemplate.execute("DELETE FROM \"受注データ\"");
    }

    /**
     * マスタデータを含む全データのクリーンアップ.
     */
    protected void cleanupAllData() {
        cleanupTransactionData();
        jdbcTemplate.execute("DELETE FROM \"商品マスタ\"");
        jdbcTemplate.execute("DELETE FROM \"商品分類マスタ\"");
        jdbcTemplate.execute("DELETE FROM \"出荷先マスタ\"");
        jdbcTemplate.execute("DELETE FROM \"顧客マスタ\"");
        jdbcTemplate.execute("DELETE FROM \"仕入先マスタ\"");
        jdbcTemplate.execute("DELETE FROM \"取引先マスタ\"");
        jdbcTemplate.execute("DELETE FROM \"取引先グループマスタ\"");
    }

    /**
     * 商品分類マスタのテストデータを作成.
     *
     * @param classificationCode 商品分類コード
     * @param classificationName 商品分類名
     */
    protected void createProductClassification(String classificationCode, String classificationName) {
        jdbcTemplate.update(
            "INSERT INTO \"商品分類マスタ\" (\"商品分類コード\", \"商品分類名\", \"商品分類階層\", \"最下層区分\") "
            + "VALUES (?, ?, 1, true) ON CONFLICT DO NOTHING",
            classificationCode, classificationName
        );
    }

    /**
     * 取引先グループマスタのテストデータを作成.
     *
     * @param groupCode 取引先グループコード
     * @param groupName 取引先グループ名
     */
    protected void createPartnerGroup(String groupCode, String groupName) {
        jdbcTemplate.update(
            "INSERT INTO \"取引先グループマスタ\" (\"取引先グループコード\", \"取引先グループ名\") "
            + "VALUES (?, ?) ON CONFLICT DO NOTHING",
            groupCode, groupName
        );
    }

    /**
     * 取引先マスタのテストデータを作成（顧客として）.
     *
     * @param partnerCode 取引先コード
     * @param partnerName 取引先名
     */
    protected void createPartnerAsCustomer(String partnerCode, String partnerName) {
        jdbcTemplate.update(
            "INSERT INTO \"取引先マスタ\" (\"取引先コード\", \"取引先名\", \"顧客区分\", \"仕入先区分\") "
            + "VALUES (?, ?, true, false) ON CONFLICT DO NOTHING",
            partnerCode, partnerName
        );
    }

    /**
     * 顧客マスタのテストデータを作成.
     *
     * @param customerCode 顧客コード（取引先コード）
     * @param branchNumber 顧客枝番
     */
    protected void createCustomer(String customerCode, String branchNumber) {
        jdbcTemplate.update(
            "INSERT INTO \"顧客マスタ\" (\"顧客コード\", \"顧客枝番\", \"顧客請求区分\") "
            + "VALUES (?, ?, '締め') ON CONFLICT DO NOTHING",
            customerCode, branchNumber
        );
    }

    /**
     * 商品マスタのテストデータを作成.
     *
     * @param productCode 商品コード
     * @param productName 商品名
     * @param sellingPrice 販売単価
     * @param purchasePrice 仕入単価
     */
    protected void createProduct(String productCode, String productName,
            int sellingPrice, int purchasePrice) {
        jdbcTemplate.update(
            "INSERT INTO \"商品マスタ\" (\"商品コード\", \"商品名\", \"商品区分\", "
            + "\"販売単価\", \"仕入単価\", \"税区分\") "
            + "VALUES (?, ?, '商品', ?, ?, '外税') ON CONFLICT DO NOTHING",
            productCode, productName, sellingPrice, purchasePrice
        );
    }

    /**
     * トランザクションテスト用のマスタデータをセットアップ.
     */
    protected void setupTransactionTestMasterData() {
        // 商品分類
        createProductClassification("CAT-TEST", "テスト分類");

        // 商品
        createProduct("PRD-INT-001", "統合テスト商品1", 5000, 3000);
        createProduct("PRD-INT-002", "統合テスト商品2", 8000, 5000);

        // 取引先・顧客
        createPartnerAsCustomer("CUS-INT-001", "統合テスト顧客");
        createCustomer("CUS-INT-001", "00");
    }
}
