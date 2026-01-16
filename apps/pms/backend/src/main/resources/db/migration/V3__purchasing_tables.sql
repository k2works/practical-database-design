-- ==================================================
-- 生産管理システム（PMS）購買関連テーブル
-- V3: 発注・入荷・検収テーブル
-- ==================================================

-- --------------------------------------------------
-- ENUM型定義（PostgreSQL）
-- --------------------------------------------------
CREATE TYPE 発注ステータス AS ENUM ('作成中', '発注済', '一部入荷', '入荷完了', '検収完了', '取消');
CREATE TYPE 入荷受入区分 AS ENUM ('通常入荷', '分割入荷', '返品入荷');

-- --------------------------------------------------
-- 発注データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "発注データ" (
    "ID" SERIAL PRIMARY KEY,
    "発注番号" VARCHAR(20) UNIQUE NOT NULL,
    "発注日" DATE NOT NULL,
    "取引先コード" VARCHAR(20) NOT NULL,
    "発注担当者コード" VARCHAR(20),
    "発注部門コード" VARCHAR(20),
    "ステータス" 発注ステータス DEFAULT '作成中' NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50)
);

COMMENT ON TABLE "発注データ" IS '発注ヘッダ情報を管理するテーブル';

-- --------------------------------------------------
-- 発注明細データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "発注明細データ" (
    "ID" SERIAL PRIMARY KEY,
    "発注番号" VARCHAR(20) NOT NULL,
    "発注行番号" INTEGER NOT NULL,
    "オーダNO" VARCHAR(20),
    "納入場所コード" VARCHAR(20),
    "品目コード" VARCHAR(20) NOT NULL,
    "諸口品目区分" BOOLEAN DEFAULT FALSE NOT NULL,
    "受入予定日" DATE NOT NULL,
    "回答納期" DATE,
    "発注単価" DECIMAL(15, 2) NOT NULL,
    "発注数量" DECIMAL(15, 2) NOT NULL,
    "入荷済数量" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "検査済数量" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "検収済数量" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "発注金額" DECIMAL(15, 2) NOT NULL,
    "消費税金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "完了フラグ" BOOLEAN DEFAULT FALSE NOT NULL,
    "明細備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_発注明細_発注"
        FOREIGN KEY ("発注番号") REFERENCES "発注データ"("発注番号"),
    UNIQUE ("発注番号", "発注行番号")
);

COMMENT ON TABLE "発注明細データ" IS '発注明細情報を管理するテーブル';

-- --------------------------------------------------
-- 諸口品目情報
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "諸口品目情報" (
    "ID" SERIAL PRIMARY KEY,
    "発注番号" VARCHAR(20) NOT NULL,
    "発注行番号" INTEGER NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "品名" VARCHAR(100) NOT NULL,
    "規格" VARCHAR(100),
    "図番メーカー" VARCHAR(100),
    "版数" VARCHAR(20),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE ("発注番号", "発注行番号", "品目コード")
);

COMMENT ON TABLE "諸口品目情報" IS 'マスタに登録されていない臨時品目の情報';

-- --------------------------------------------------
-- 入荷受入データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "入荷受入データ" (
    "ID" SERIAL PRIMARY KEY,
    "入荷番号" VARCHAR(20) UNIQUE NOT NULL,
    "発注番号" VARCHAR(20) NOT NULL,
    "発注行番号" INTEGER NOT NULL,
    "入荷日" DATE NOT NULL,
    "入荷担当者コード" VARCHAR(20),
    "入荷受入区分" 入荷受入区分 DEFAULT '通常入荷' NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "諸口品目区分" BOOLEAN DEFAULT FALSE NOT NULL,
    "入荷数量" DECIMAL(15, 2) NOT NULL,
    "入荷備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_入荷受入_発注明細"
        FOREIGN KEY ("発注番号", "発注行番号") REFERENCES "発注明細データ"("発注番号", "発注行番号")
);

COMMENT ON TABLE "入荷受入データ" IS '入荷受入情報を管理するテーブル';

-- --------------------------------------------------
-- 受入検査データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "受入検査データ" (
    "ID" SERIAL PRIMARY KEY,
    "受入検査番号" VARCHAR(20) UNIQUE NOT NULL,
    "入荷番号" VARCHAR(20) NOT NULL,
    "発注番号" VARCHAR(20) NOT NULL,
    "発注行番号" INTEGER NOT NULL,
    "受入検査日" DATE NOT NULL,
    "受入検査担当者コード" VARCHAR(20),
    "品目コード" VARCHAR(20) NOT NULL,
    "諸口品目区分" BOOLEAN DEFAULT FALSE NOT NULL,
    "良品数" DECIMAL(15, 2) NOT NULL,
    "不良品数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "受入検査備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_受入検査_入荷"
        FOREIGN KEY ("入荷番号") REFERENCES "入荷受入データ"("入荷番号")
);

COMMENT ON TABLE "受入検査データ" IS '受入検査情報を管理するテーブル';

-- --------------------------------------------------
-- 検収データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "検収データ" (
    "ID" SERIAL PRIMARY KEY,
    "検収番号" VARCHAR(20) UNIQUE NOT NULL,
    "受入検査番号" VARCHAR(20) NOT NULL,
    "発注番号" VARCHAR(20) NOT NULL,
    "発注行番号" INTEGER NOT NULL,
    "検収日" DATE NOT NULL,
    "検収担当者コード" VARCHAR(20),
    "取引先コード" VARCHAR(20) NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "諸口品目区分" BOOLEAN DEFAULT FALSE NOT NULL,
    "検収数" DECIMAL(15, 2) NOT NULL,
    "検収単価" DECIMAL(15, 2) NOT NULL,
    "検収金額" DECIMAL(15, 2) NOT NULL,
    "検収消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "検収備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_検収_受入検査"
        FOREIGN KEY ("受入検査番号") REFERENCES "受入検査データ"("受入検査番号"),
    CONSTRAINT "fk_検収_発注明細"
        FOREIGN KEY ("発注番号", "発注行番号") REFERENCES "発注明細データ"("発注番号", "発注行番号")
);

COMMENT ON TABLE "検収データ" IS '検収情報を管理するテーブル';

-- --------------------------------------------------
-- インデックス作成
-- --------------------------------------------------
CREATE INDEX "idx_発注データ_取引先コード" ON "発注データ"("取引先コード");
CREATE INDEX "idx_発注データ_発注日" ON "発注データ"("発注日");
CREATE INDEX "idx_発注明細_発注番号" ON "発注明細データ"("発注番号");
CREATE INDEX "idx_発注明細_品目コード" ON "発注明細データ"("品目コード");
CREATE INDEX "idx_入荷受入_発注番号" ON "入荷受入データ"("発注番号", "発注行番号");
CREATE INDEX "idx_入荷受入_入荷日" ON "入荷受入データ"("入荷日");
CREATE INDEX "idx_受入検査_入荷番号" ON "受入検査データ"("入荷番号");
CREATE INDEX "idx_検収_受入検査番号" ON "検収データ"("受入検査番号");
CREATE INDEX "idx_検収_発注番号" ON "検収データ"("発注番号", "発注行番号");
