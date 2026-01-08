package com.example.fas.integration;

import com.example.fas.TestcontainersConfiguration;
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
     * トランザクションデータのクリーンアップ.
     * 仕訳関連テーブルを削除順に従ってクリーンアップする。
     */
    protected void cleanupTransactionData() {
        // 子テーブルから順に削除
        jdbcTemplate.execute("DELETE FROM \"仕訳貸借明細\"");
        jdbcTemplate.execute("DELETE FROM \"仕訳明細\"");
        jdbcTemplate.execute("DELETE FROM \"仕訳\"");
        // 残高テーブル
        jdbcTemplate.execute("DELETE FROM \"日次勘定科目残高\"");
        jdbcTemplate.execute("DELETE FROM \"月次勘定科目残高\"");
    }

    /**
     * マスタデータを含む全データのクリーンアップ.
     * Seed データは削除せず、テスト用データ（99xxxコード）のみ削除する.
     */
    protected void cleanupAllData() {
        cleanupTransactionData();
        // テスト用勘定科目のみ削除（99xxxのみ）
        jdbcTemplate.execute("DELETE FROM \"勘定科目構成マスタ\" WHERE \"勘定科目コード\" LIKE '99%'");
        jdbcTemplate.execute("DELETE FROM \"勘定科目マスタ\" WHERE \"勘定科目コード\" LIKE '99%'");
    }

    /**
     * テスト用勘定科目を作成.
     *
     * @param accountCode 勘定科目コード
     * @param accountName 勘定科目名
     * @param bsplType BSPL区分（BS/PL）
     * @param dcType 貸借区分（借方/貸方）
     */
    protected void createAccount(String accountCode, String accountName,
            String bsplType, String dcType) {
        jdbcTemplate.update(
                "INSERT INTO \"勘定科目マスタ\" "
                        + "(\"勘定科目コード\", \"勘定科目名\", \"勘定科目略名\", "
                        + "\"BSPL区分\", \"貸借区分\", \"取引要素区分\", \"集計区分\") "
                        + "VALUES (?, ?, ?, ?::\"BSPL区分\", ?::\"貸借区分\", '資産'::\"取引要素区分\", "
                        + "'計上'::\"集計区分\") ON CONFLICT DO NOTHING",
                accountCode, accountName, accountName, bsplType, dcType
        );
    }

    /**
     * インテグレーションテスト用の基本マスタデータをセットアップ.
     */
    protected void setupBasicMasterData() {
        // 資産勘定
        createAccount("11110", "現金", "BS", "借方");
        createAccount("11210", "普通預金", "BS", "借方");
        createAccount("13110", "売掛金", "BS", "借方");

        // 負債勘定
        createAccount("21110", "買掛金", "BS", "貸方");
        createAccount("21210", "未払金", "BS", "貸方");

        // 収益勘定
        createAccount("41110", "売上高", "PL", "貸方");

        // 費用勘定
        createAccount("51110", "仕入高", "PL", "借方");
        createAccount("61110", "給料手当", "PL", "借方");
    }
}
