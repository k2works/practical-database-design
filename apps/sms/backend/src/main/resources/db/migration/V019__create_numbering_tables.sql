-- 採番マスタ
CREATE TABLE "採番マスタ" (
    "採番コード" VARCHAR(20) PRIMARY KEY,
    "採番名" VARCHAR(100) NOT NULL,
    "プレフィックス" VARCHAR(10) NOT NULL,
    "採番形式" VARCHAR(20) NOT NULL,  -- YEARLY, MONTHLY, DAILY, SEQUENTIAL
    "桁数" INTEGER NOT NULL,
    "現在値" BIGINT DEFAULT 0 NOT NULL,
    "最終採番日" DATE,
    "リセット対象" BOOLEAN DEFAULT false NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 採番履歴データ
CREATE TABLE "採番履歴データ" (
    "ID" SERIAL PRIMARY KEY,
    "採番コード" VARCHAR(20) NOT NULL,
    "採番年月" VARCHAR(8) NOT NULL,
    "最終番号" BIGINT NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_採番履歴_採番マスタ"
        FOREIGN KEY ("採番コード") REFERENCES "採番マスタ"("採番コード"),
    UNIQUE ("採番コード", "採番年月")
);

-- 初期データ
INSERT INTO "採番マスタ" ("採番コード", "採番名", "プレフィックス", "採番形式", "桁数") VALUES
('SALES', '売上番号', 'SL', 'MONTHLY', 4),
('ORDER', '受注番号', 'ORD', 'YEARLY', 5),
('SHIPMENT', '出荷番号', 'SHP', 'MONTHLY', 4),
('INVOICE', '請求番号', 'INV', 'MONTHLY', 4),
('RECEIPT', '入金番号', 'RC', 'MONTHLY', 4),
('PURCHASE_ORDER', '発注番号', 'PO', 'MONTHLY', 4),
('PURCHASE', '仕入番号', 'PU', 'MONTHLY', 4),
('PAYMENT', '支払番号', 'PAY', 'MONTHLY', 4),
('STOCKTAKING', '棚卸番号', 'ST', 'MONTHLY', 4);

-- インデックス
CREATE INDEX "idx_採番履歴_採番年月" ON "採番履歴データ"("採番年月");

-- テーブルコメント
COMMENT ON TABLE "採番マスタ" IS '伝票番号の採番ルールを管理するマスタテーブル';
COMMENT ON TABLE "採番履歴データ" IS '年月別の採番状況を記録するテーブル';
