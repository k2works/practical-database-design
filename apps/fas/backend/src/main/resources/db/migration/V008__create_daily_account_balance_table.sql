-- 日次勘定科目残高テーブル
-- 仕訳データを起票日単位で集計した残高テーブル

CREATE TABLE "日次勘定科目残高" (
    "起票日"               DATE NOT NULL,
    "勘定科目コード"        VARCHAR(5) NOT NULL,
    "補助科目コード"        VARCHAR(10) NOT NULL DEFAULT '',
    "部門コード"            VARCHAR(5) NOT NULL DEFAULT '00000',
    "プロジェクトコード"    VARCHAR(10) NOT NULL DEFAULT '',
    "決算仕訳フラグ"        SMALLINT NOT NULL DEFAULT 0,
    "借方金額"             DECIMAL(15, 0) NOT NULL DEFAULT 0,
    "貸方金額"             DECIMAL(15, 0) NOT NULL DEFAULT 0,
    "バージョン"           INTEGER NOT NULL DEFAULT 1,
    "作成日時"             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "更新日時"             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (
        "起票日", "勘定科目コード", "補助科目コード",
        "部門コード", "プロジェクトコード", "決算仕訳フラグ"
    ),
    CONSTRAINT "fk_日次残高_勘定科目"
        FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード")
);

-- インデックス
CREATE INDEX "idx_日次残高_起票日" ON "日次勘定科目残高"("起票日");
CREATE INDEX "idx_日次残高_勘定科目" ON "日次勘定科目残高"("勘定科目コード");
CREATE INDEX "idx_日次残高_部門" ON "日次勘定科目残高"("部門コード");

-- コメント
COMMENT ON TABLE "日次勘定科目残高" IS '仕訳データを起票日単位で集計した残高テーブル';
COMMENT ON COLUMN "日次勘定科目残高"."バージョン" IS '楽観ロック用バージョン番号';
