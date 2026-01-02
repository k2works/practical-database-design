-- 変更履歴データ
CREATE TABLE "変更履歴データ" (
    "ID" SERIAL PRIMARY KEY,
    "テーブル名" VARCHAR(100) NOT NULL,
    "レコードID" VARCHAR(100) NOT NULL,
    "操作種別" VARCHAR(10) NOT NULL,  -- INSERT, UPDATE, DELETE
    "変更日時" TIMESTAMP NOT NULL,
    "変更者" VARCHAR(50) NOT NULL,
    "変更前データ" JSONB,
    "変更後データ" JSONB,
    "変更理由" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- マスタ履歴データ（スナップショット方式の例）
CREATE TABLE "商品マスタ履歴" (
    "ID" SERIAL PRIMARY KEY,
    "商品コード" VARCHAR(20) NOT NULL,
    "有効開始日" DATE NOT NULL,
    "有効終了日" DATE,
    "商品名" VARCHAR(100) NOT NULL,
    "商品区分" 商品区分 NOT NULL,
    "単価" DECIMAL(15, 2),
    "税区分" 税区分,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    UNIQUE ("商品コード", "有効開始日")
);

-- インデックス
CREATE INDEX "idx_変更履歴_テーブル名" ON "変更履歴データ"("テーブル名");
CREATE INDEX "idx_変更履歴_レコードID" ON "変更履歴データ"("レコードID");
CREATE INDEX "idx_変更履歴_変更日時" ON "変更履歴データ"("変更日時");
CREATE INDEX "idx_商品マスタ履歴_商品コード" ON "商品マスタ履歴"("商品コード");
CREATE INDEX "idx_商品マスタ履歴_有効期間"
    ON "商品マスタ履歴"("有効開始日", "有効終了日");

-- テーブルコメント
COMMENT ON TABLE "変更履歴データ" IS 'データ変更の履歴をJSONBで保存するテーブル';
COMMENT ON TABLE "商品マスタ履歴" IS '商品マスタのスナップショット履歴';
