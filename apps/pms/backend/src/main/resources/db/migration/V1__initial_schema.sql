-- ==================================================
-- 生産管理システム（PMS）初期スキーマ
-- V1: 基本マスタテーブル
-- ==================================================

-- --------------------------------------------------
-- ENUM型定義（PostgreSQL）
-- --------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = '品目区分') THEN
        CREATE TYPE 品目区分 AS ENUM ('製品', '半製品', '中間品', '部品', '材料', '原料', '資材');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = '日付区分') THEN
        CREATE TYPE 日付区分 AS ENUM ('稼働日', '休日', '半日稼働');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = '場所区分') THEN
        CREATE TYPE 場所区分 AS ENUM ('倉庫', '製造', '検査', '出荷', '外注');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = '取引先区分') THEN
        CREATE TYPE 取引先区分 AS ENUM ('仕入先', '外注先', '得意先', '仕入先兼外注先');
    END IF;
END$$;

-- --------------------------------------------------
-- 単位マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "単位マスタ" (
    "単位コード" VARCHAR(10) PRIMARY KEY,
    "単位記号" VARCHAR(10) NOT NULL,
    "単位名" VARCHAR(50) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE "単位マスタ" IS '数量の単位を管理するマスタテーブル';

-- --------------------------------------------------
-- 部門マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "部門マスタ" (
    "部門コード" VARCHAR(10) PRIMARY KEY,
    "部門名" VARCHAR(100) NOT NULL,
    "部門パス" VARCHAR(500),
    "最下層区分" BOOLEAN DEFAULT FALSE,
    "有効開始日" DATE NOT NULL,
    "有効終了日" DATE,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE "部門マスタ" IS '組織の部門情報を管理するマスタテーブル';

-- --------------------------------------------------
-- 担当者マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "担当者マスタ" (
    "担当者コード" VARCHAR(10) NOT NULL,
    "適用開始日" DATE NOT NULL,
    "適用停止日" DATE,
    "担当者名" VARCHAR(100) NOT NULL,
    "部門コード" VARCHAR(10),
    "メールアドレス" VARCHAR(255),
    "電話番号" VARCHAR(20),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("担当者コード", "適用開始日"),
    FOREIGN KEY ("部門コード") REFERENCES "部門マスタ"("部門コード")
);

COMMENT ON TABLE "担当者マスタ" IS '担当者情報を管理するマスタテーブル';

-- --------------------------------------------------
-- 場所マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "場所マスタ" (
    "場所コード" VARCHAR(20) PRIMARY KEY,
    "場所名" VARCHAR(100) NOT NULL,
    "場所区分" 場所区分 NOT NULL,
    "親場所コード" VARCHAR(20),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("親場所コード") REFERENCES "場所マスタ"("場所コード")
);

COMMENT ON TABLE "場所マスタ" IS '倉庫・工場・ラインなどの場所情報を管理するマスタテーブル';

-- --------------------------------------------------
-- 品目マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "品目マスタ" (
    "ID" SERIAL PRIMARY KEY,
    "品目コード" VARCHAR(20) NOT NULL,
    "適用開始日" DATE NOT NULL,
    "適用停止日" DATE,
    "品名" VARCHAR(200) NOT NULL,
    "品目区分" 品目区分 NOT NULL,
    "単位コード" VARCHAR(10),
    "リードタイム" INTEGER DEFAULT 0,
    "安全リードタイム" INTEGER DEFAULT 0,
    "安全在庫数" DECIMAL(15, 3) DEFAULT 0,
    "歩留率" DECIMAL(5, 2) DEFAULT 100.00,
    "最小ロット数" DECIMAL(15, 3) DEFAULT 1,
    "刻みロット数" DECIMAL(15, 3) DEFAULT 1,
    "最大ロット数" DECIMAL(15, 3),
    "有効期間" INTEGER,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE("品目コード", "適用開始日"),
    FOREIGN KEY ("単位コード") REFERENCES "単位マスタ"("単位コード")
);

COMMENT ON TABLE "品目マスタ" IS '製品・半製品・部品・材料などの品目情報を管理するマスタテーブル';

-- --------------------------------------------------
-- 部品構成表（BOM）
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "部品構成表" (
    "親品目コード" VARCHAR(20) NOT NULL,
    "子品目コード" VARCHAR(20) NOT NULL,
    "適用開始日" DATE NOT NULL,
    "適用停止日" DATE,
    "基準数量" DECIMAL(15, 3) NOT NULL DEFAULT 1,
    "必要数量" DECIMAL(15, 6) NOT NULL,
    "不良率" DECIMAL(5, 2) DEFAULT 0,
    "工順" INTEGER DEFAULT 1,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("親品目コード", "子品目コード", "適用開始日")
);

COMMENT ON TABLE "部品構成表" IS '製品を構成する部品・材料の構成情報（BOM）';

-- --------------------------------------------------
-- カレンダマスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "カレンダマスタ" (
    "カレンダコード" VARCHAR(20) NOT NULL,
    "日付" DATE NOT NULL,
    "日付区分" 日付区分 NOT NULL DEFAULT '稼働日',
    "稼働時間" DECIMAL(5, 2),
    "備考" VARCHAR(200),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("カレンダコード", "日付")
);

COMMENT ON TABLE "カレンダマスタ" IS '就業日・休日などのカレンダー情報を管理するマスタテーブル';

-- --------------------------------------------------
-- 取引先マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "取引先マスタ" (
    "取引先コード" VARCHAR(20) NOT NULL,
    "適用開始日" DATE NOT NULL,
    "適用停止日" DATE,
    "取引先名" VARCHAR(100) NOT NULL,
    "取引先カナ" VARCHAR(100),
    "取引先区分" 取引先区分 NOT NULL,
    "郵便番号" VARCHAR(10),
    "住所" VARCHAR(200),
    "電話番号" VARCHAR(20),
    "FAX番号" VARCHAR(20),
    "担当者名" VARCHAR(50),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("取引先コード", "適用開始日")
);

COMMENT ON TABLE "取引先マスタ" IS '仕入先・外注先・顧客などの取引先情報を管理するマスタテーブル';

-- --------------------------------------------------
-- 工程マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "工程マスタ" (
    "工程コード" VARCHAR(10) PRIMARY KEY,
    "工程名" VARCHAR(100) NOT NULL,
    "工程区分" VARCHAR(20),
    "場所コード" VARCHAR(20),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("場所コード") REFERENCES "場所マスタ"("場所コード")
);

COMMENT ON TABLE "工程マスタ" IS '製造工程情報を管理するマスタテーブル';

-- --------------------------------------------------
-- 工程表
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "工程表" (
    "品目コード" VARCHAR(20) NOT NULL,
    "工順" INTEGER NOT NULL,
    "工程コード" VARCHAR(10) NOT NULL,
    "標準作業時間" DECIMAL(10, 2),
    "段取時間" DECIMAL(10, 2),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("品目コード", "工順"),
    FOREIGN KEY ("工程コード") REFERENCES "工程マスタ"("工程コード")
);

COMMENT ON TABLE "工程表" IS '品目ごとの製造工程順序を定義するテーブル';

-- --------------------------------------------------
-- 単価マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "単価マスタ" (
    "品目コード" VARCHAR(20) NOT NULL,
    "取引先コード" VARCHAR(20) NOT NULL,
    "適用開始日" DATE NOT NULL,
    "適用停止日" DATE,
    "単価" DECIMAL(15, 2) NOT NULL,
    "通貨コード" VARCHAR(3) DEFAULT 'JPY',
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("品目コード", "取引先コード", "適用開始日")
);

COMMENT ON TABLE "単価マスタ" IS '品目と取引先の組み合わせによる単価情報を管理するマスタテーブル';

-- --------------------------------------------------
-- 欠点マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "欠点マスタ" (
    "欠点コード" VARCHAR(10) PRIMARY KEY,
    "欠点名" VARCHAR(100) NOT NULL,
    "欠点区分" VARCHAR(20),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE "欠点マスタ" IS '品質不良の種類を管理するマスタテーブル';

-- --------------------------------------------------
-- インデックス作成
-- --------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_品目マスタ_品目コード ON "品目マスタ"("品目コード");
CREATE INDEX IF NOT EXISTS idx_品目マスタ_品目区分 ON "品目マスタ"("品目区分");
CREATE INDEX IF NOT EXISTS idx_bom_親品目コード ON "部品構成表"("親品目コード");
CREATE INDEX IF NOT EXISTS idx_bom_子品目コード ON "部品構成表"("子品目コード");
CREATE INDEX IF NOT EXISTS idx_工程表_品目コード ON "工程表"("品目コード");
CREATE INDEX IF NOT EXISTS idx_取引先マスタ_取引先区分 ON "取引先マスタ"("取引先区分");
CREATE INDEX IF NOT EXISTS idx_単価マスタ_品目コード ON "単価マスタ"("品目コード");
CREATE INDEX IF NOT EXISTS idx_単価マスタ_取引先コード ON "単価マスタ"("取引先コード");
