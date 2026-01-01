-- 出荷ステータス
CREATE TYPE 出荷ステータス AS ENUM ('出荷指示済', '出荷準備中', '出荷済', 'キャンセル');

-- 出荷データ（ヘッダ）
CREATE TABLE "出荷データ" (
    "ID" SERIAL PRIMARY KEY,
    "出荷番号" VARCHAR(20) UNIQUE NOT NULL,
    "出荷日" DATE NOT NULL,
    "受注ID" INTEGER NOT NULL,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "出荷先番号" VARCHAR(10),
    "出荷先名" VARCHAR(100),
    "出荷先郵便番号" VARCHAR(10),
    "出荷先住所1" VARCHAR(100),
    "出荷先住所2" VARCHAR(100),
    "担当者コード" VARCHAR(20),
    "倉庫コード" VARCHAR(20),
    "ステータス" 出荷ステータス DEFAULT '出荷指示済' NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_出荷データ_受注"
        FOREIGN KEY ("受注ID") REFERENCES "受注データ"("ID"),
    CONSTRAINT "fk_出荷データ_顧客"
        FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番"),
    CONSTRAINT "fk_出荷データ_出荷先"
        FOREIGN KEY ("顧客コード", "顧客枝番", "出荷先番号") REFERENCES "出荷先マスタ"("取引先コード", "顧客枝番", "出荷先番号")
);

-- 出荷明細
CREATE TABLE "出荷明細" (
    "ID" SERIAL PRIMARY KEY,
    "出荷ID" INTEGER NOT NULL,
    "行番号" INTEGER NOT NULL,
    "受注明細ID" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "商品名" VARCHAR(100) NOT NULL,
    "出荷数量" DECIMAL(15, 2) NOT NULL,
    "単位" VARCHAR(10),
    "単価" DECIMAL(15, 2) NOT NULL,
    "金額" DECIMAL(15, 2) NOT NULL,
    "税区分" 税区分 DEFAULT '外税' NOT NULL,
    "消費税率" DECIMAL(5, 2) DEFAULT 10.00 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "倉庫コード" VARCHAR(20),
    "備考" TEXT,
    CONSTRAINT "fk_出荷明細_出荷"
        FOREIGN KEY ("出荷ID") REFERENCES "出荷データ"("ID") ON DELETE CASCADE,
    CONSTRAINT "fk_出荷明細_受注明細"
        FOREIGN KEY ("受注明細ID") REFERENCES "受注明細"("ID"),
    CONSTRAINT "fk_出荷明細_商品"
        FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT "uq_出荷明細_行番号" UNIQUE ("出荷ID", "行番号")
);

-- インデックス
CREATE INDEX "idx_出荷データ_受注ID" ON "出荷データ"("受注ID");
CREATE INDEX "idx_出荷データ_顧客コード" ON "出荷データ"("顧客コード");
CREATE INDEX "idx_出荷データ_出荷日" ON "出荷データ"("出荷日");
CREATE INDEX "idx_出荷データ_ステータス" ON "出荷データ"("ステータス");
