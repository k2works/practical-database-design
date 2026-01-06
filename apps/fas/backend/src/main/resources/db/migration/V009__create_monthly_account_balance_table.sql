-- 月次勘定科目残高テーブル
-- 日次残高を月単位で集計した残高テーブル

CREATE TABLE "月次勘定科目残高" (
    "決算期"               INTEGER NOT NULL,
    "月度"                 SMALLINT NOT NULL,
    "勘定科目コード"        VARCHAR(5) NOT NULL,
    "補助科目コード"        VARCHAR(10) NOT NULL DEFAULT '',
    "部門コード"            VARCHAR(5) NOT NULL DEFAULT '00000',
    "プロジェクトコード"    VARCHAR(10) NOT NULL DEFAULT '',
    "決算仕訳フラグ"        SMALLINT NOT NULL DEFAULT 0,
    "月初残高"             DECIMAL(15, 0) NOT NULL DEFAULT 0,
    "借方金額"             DECIMAL(15, 0) NOT NULL DEFAULT 0,
    "貸方金額"             DECIMAL(15, 0) NOT NULL DEFAULT 0,
    "月末残高"             DECIMAL(15, 0) NOT NULL DEFAULT 0,
    "バージョン"           INTEGER NOT NULL DEFAULT 1,
    "作成日時"             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "更新日時"             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (
        "決算期", "月度", "勘定科目コード", "補助科目コード",
        "部門コード", "プロジェクトコード", "決算仕訳フラグ"
    ),
    CONSTRAINT "fk_月次残高_勘定科目"
        FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード"),
    CONSTRAINT "chk_月度" CHECK ("月度" BETWEEN 1 AND 12)
);

-- インデックス
CREATE INDEX "idx_月次残高_決算期月度" ON "月次勘定科目残高"("決算期", "月度");
CREATE INDEX "idx_月次残高_勘定科目" ON "月次勘定科目残高"("勘定科目コード");

-- コメント
COMMENT ON TABLE "月次勘定科目残高" IS '日次残高を月単位で集計した残高テーブル';
COMMENT ON COLUMN "月次勘定科目残高"."バージョン" IS '楽観ロック用バージョン番号';
