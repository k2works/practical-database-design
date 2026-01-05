-- 売上ステータス
CREATE TYPE 売上ステータス AS ENUM ('計上済', '請求済', '入金済', 'キャンセル');

-- 売上データ（ヘッダ）
CREATE TABLE "売上データ" (
    "ID" SERIAL PRIMARY KEY,
    "売上番号" VARCHAR(20) UNIQUE NOT NULL,
    "売上日" DATE NOT NULL,
    "受注ID" INTEGER NOT NULL,
    "出荷ID" INTEGER,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "担当者コード" VARCHAR(20),
    "売上金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "売上合計" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "ステータス" 売上ステータス DEFAULT '計上済' NOT NULL,
    "請求ID" INTEGER,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_売上データ_受注"
        FOREIGN KEY ("受注ID") REFERENCES "受注データ"("ID"),
    CONSTRAINT "fk_売上データ_出荷"
        FOREIGN KEY ("出荷ID") REFERENCES "出荷データ"("ID"),
    CONSTRAINT "fk_売上データ_顧客"
        FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番")
);

-- 売上明細
CREATE TABLE "売上明細" (
    "ID" SERIAL PRIMARY KEY,
    "売上ID" INTEGER NOT NULL,
    "行番号" INTEGER NOT NULL,
    "受注明細ID" INTEGER NOT NULL,
    "出荷明細ID" INTEGER,
    "商品コード" VARCHAR(20) NOT NULL,
    "商品名" VARCHAR(100) NOT NULL,
    "売上数量" DECIMAL(15, 2) NOT NULL,
    "単位" VARCHAR(10),
    "単価" DECIMAL(15, 2) NOT NULL,
    "金額" DECIMAL(15, 2) NOT NULL,
    "税区分" 税区分 DEFAULT '外税' NOT NULL,
    "消費税率" DECIMAL(5, 2) DEFAULT 10.00 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "備考" TEXT,
    CONSTRAINT "fk_売上明細_売上"
        FOREIGN KEY ("売上ID") REFERENCES "売上データ"("ID") ON DELETE CASCADE,
    CONSTRAINT "fk_売上明細_受注明細"
        FOREIGN KEY ("受注明細ID") REFERENCES "受注明細"("ID"),
    CONSTRAINT "fk_売上明細_出荷明細"
        FOREIGN KEY ("出荷明細ID") REFERENCES "出荷明細"("ID"),
    CONSTRAINT "fk_売上明細_商品"
        FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT "uq_売上明細_行番号" UNIQUE ("売上ID", "行番号")
);

-- インデックス
CREATE INDEX "idx_売上データ_受注ID" ON "売上データ"("受注ID");
CREATE INDEX "idx_売上データ_出荷ID" ON "売上データ"("出荷ID");
CREATE INDEX "idx_売上データ_顧客コード" ON "売上データ"("顧客コード");
CREATE INDEX "idx_売上データ_売上日" ON "売上データ"("売上日");
CREATE INDEX "idx_売上データ_ステータス" ON "売上データ"("ステータス");
