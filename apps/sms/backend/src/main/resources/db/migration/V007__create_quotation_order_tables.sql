-- 見積ステータス
CREATE TYPE 見積ステータス AS ENUM ('商談中', '受注確定', '失注', '期限切れ');

-- 受注ステータス
CREATE TYPE 受注ステータス AS ENUM ('受付済', '引当済', '出荷指示済', '出荷済', 'キャンセル');

-- 見積データ（ヘッダ）
CREATE TABLE "見積データ" (
    "ID" SERIAL PRIMARY KEY,
    "見積番号" VARCHAR(20) UNIQUE NOT NULL,
    "見積日" DATE NOT NULL,
    "見積有効期限" DATE,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "担当者コード" VARCHAR(20),
    "件名" VARCHAR(200),
    "見積金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "見積合計" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "ステータス" 見積ステータス DEFAULT '商談中' NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_見積データ_顧客"
        FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番")
);

-- 見積明細
CREATE TABLE "見積明細" (
    "ID" SERIAL PRIMARY KEY,
    "見積ID" INTEGER NOT NULL,
    "行番号" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "商品名" VARCHAR(100) NOT NULL,
    "数量" DECIMAL(15, 2) NOT NULL,
    "単位" VARCHAR(10),
    "単価" DECIMAL(15, 2) NOT NULL,
    "金額" DECIMAL(15, 2) NOT NULL,
    "税区分" 税区分 DEFAULT '外税' NOT NULL,
    "消費税率" DECIMAL(5, 2) DEFAULT 10.00 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "備考" TEXT,
    CONSTRAINT "fk_見積明細_見積"
        FOREIGN KEY ("見積ID") REFERENCES "見積データ"("ID") ON DELETE CASCADE,
    CONSTRAINT "fk_見積明細_商品"
        FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT "uq_見積明細_行番号" UNIQUE ("見積ID", "行番号")
);

-- 受注データ（ヘッダ）
CREATE TABLE "受注データ" (
    "ID" SERIAL PRIMARY KEY,
    "受注番号" VARCHAR(20) UNIQUE NOT NULL,
    "受注日" DATE NOT NULL,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "出荷先番号" VARCHAR(10),
    "担当者コード" VARCHAR(20),
    "希望納期" DATE,
    "出荷予定日" DATE,
    "受注金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "受注合計" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "ステータス" 受注ステータス DEFAULT '受付済' NOT NULL,
    "見積ID" INTEGER,
    "顧客注文番号" VARCHAR(50),
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_受注データ_顧客"
        FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番"),
    CONSTRAINT "fk_受注データ_出荷先"
        FOREIGN KEY ("顧客コード", "顧客枝番", "出荷先番号") REFERENCES "出荷先マスタ"("取引先コード", "顧客枝番", "出荷先番号"),
    CONSTRAINT "fk_受注データ_見積"
        FOREIGN KEY ("見積ID") REFERENCES "見積データ"("ID")
);

-- 受注明細
CREATE TABLE "受注明細" (
    "ID" SERIAL PRIMARY KEY,
    "受注ID" INTEGER NOT NULL,
    "行番号" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "商品名" VARCHAR(100) NOT NULL,
    "受注数量" DECIMAL(15, 2) NOT NULL,
    "引当数量" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "出荷数量" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "残数量" DECIMAL(15, 2) NOT NULL,
    "単位" VARCHAR(10),
    "単価" DECIMAL(15, 2) NOT NULL,
    "金額" DECIMAL(15, 2) NOT NULL,
    "税区分" 税区分 DEFAULT '外税' NOT NULL,
    "消費税率" DECIMAL(5, 2) DEFAULT 10.00 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "倉庫コード" VARCHAR(20),
    "希望納期" DATE,
    "備考" TEXT,
    CONSTRAINT "fk_受注明細_受注"
        FOREIGN KEY ("受注ID") REFERENCES "受注データ"("ID") ON DELETE CASCADE,
    CONSTRAINT "fk_受注明細_商品"
        FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT "uq_受注明細_行番号" UNIQUE ("受注ID", "行番号")
);

-- インデックス
CREATE INDEX "idx_見積データ_顧客コード" ON "見積データ"("顧客コード");
CREATE INDEX "idx_見積データ_見積日" ON "見積データ"("見積日");
CREATE INDEX "idx_見積データ_ステータス" ON "見積データ"("ステータス");
CREATE INDEX "idx_受注データ_顧客コード" ON "受注データ"("顧客コード");
CREATE INDEX "idx_受注データ_受注日" ON "受注データ"("受注日");
CREATE INDEX "idx_受注データ_ステータス" ON "受注データ"("ステータス");
CREATE INDEX "idx_受注データ_希望納期" ON "受注データ"("希望納期");
