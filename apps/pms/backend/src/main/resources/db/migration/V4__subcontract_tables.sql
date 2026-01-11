-- ==================================================
-- 生産管理システム（PMS）外注委託関連テーブル
-- V4: 支給・消費テーブル
-- ==================================================

-- --------------------------------------------------
-- ENUM型定義（PostgreSQL）
-- --------------------------------------------------
CREATE TYPE 支給区分 AS ENUM ('有償支給', '無償支給');

-- --------------------------------------------------
-- 支給データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "支給データ" (
    "ID" SERIAL PRIMARY KEY,
    "支給番号" VARCHAR(20) UNIQUE NOT NULL,
    "発注番号" VARCHAR(20) NOT NULL,
    "発注行番号" INTEGER NOT NULL,
    "取引先コード" VARCHAR(20) NOT NULL,
    "支給日" DATE NOT NULL,
    "支給担当者コード" VARCHAR(20) NOT NULL,
    "支給区分" 支給区分 DEFAULT '無償支給' NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_支給データ_発注明細"
        FOREIGN KEY ("発注番号", "発注行番号") REFERENCES "発注明細データ"("発注番号", "発注行番号")
);

COMMENT ON TABLE "支給データ" IS '外注先への支給情報を管理するテーブル';

-- --------------------------------------------------
-- 支給明細データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "支給明細データ" (
    "ID" SERIAL PRIMARY KEY,
    "支給番号" VARCHAR(20) NOT NULL,
    "支給行番号" INTEGER NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "支給数" DECIMAL(15, 2) NOT NULL,
    "支給単価" DECIMAL(15, 2) NOT NULL,
    "支給金額" DECIMAL(15, 2) NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_支給明細_支給"
        FOREIGN KEY ("支給番号") REFERENCES "支給データ"("支給番号"),
    UNIQUE ("支給番号", "支給行番号")
);

COMMENT ON TABLE "支給明細データ" IS '支給の明細情報を管理するテーブル';

-- --------------------------------------------------
-- 消費データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "消費データ" (
    "ID" SERIAL PRIMARY KEY,
    "消費番号" VARCHAR(20) UNIQUE NOT NULL,
    "入荷番号" VARCHAR(20) NOT NULL,
    "消費日" DATE NOT NULL,
    "取引先コード" VARCHAR(20) NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_消費データ_入荷"
        FOREIGN KEY ("入荷番号") REFERENCES "入荷受入データ"("入荷番号")
);

COMMENT ON TABLE "消費データ" IS '支給品の消費情報を管理するテーブル';

-- --------------------------------------------------
-- 消費明細データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "消費明細データ" (
    "ID" SERIAL PRIMARY KEY,
    "消費番号" VARCHAR(20) NOT NULL,
    "消費行番号" INTEGER NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "消費数量" DECIMAL(15, 2) NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_消費明細_消費"
        FOREIGN KEY ("消費番号") REFERENCES "消費データ"("消費番号"),
    UNIQUE ("消費番号", "消費行番号")
);

COMMENT ON TABLE "消費明細データ" IS '消費の明細情報を管理するテーブル';

-- --------------------------------------------------
-- インデックス作成
-- --------------------------------------------------
CREATE INDEX "idx_支給データ_発注番号" ON "支給データ"("発注番号", "発注行番号");
CREATE INDEX "idx_支給データ_取引先コード" ON "支給データ"("取引先コード");
CREATE INDEX "idx_支給データ_支給日" ON "支給データ"("支給日");
CREATE INDEX "idx_支給明細_支給番号" ON "支給明細データ"("支給番号");
CREATE INDEX "idx_支給明細_品目コード" ON "支給明細データ"("品目コード");
CREATE INDEX "idx_消費データ_入荷番号" ON "消費データ"("入荷番号");
CREATE INDEX "idx_消費データ_取引先コード" ON "消費データ"("取引先コード");
CREATE INDEX "idx_消費データ_消費日" ON "消費データ"("消費日");
CREATE INDEX "idx_消費明細_消費番号" ON "消費明細データ"("消費番号");
CREATE INDEX "idx_消費明細_品目コード" ON "消費明細データ"("品目コード");
