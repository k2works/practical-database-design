-- V6__inventory_management_tables.sql
-- 在庫管理テーブル

-- --------------------------------------------------
-- 倉庫マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "倉庫マスタ" (
    "倉庫コード" VARCHAR(20) PRIMARY KEY,
    "倉庫区分" VARCHAR(20) NOT NULL,
    "倉庫名" VARCHAR(100) NOT NULL,
    "部門コード" VARCHAR(20),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_倉庫_部門"
        FOREIGN KEY ("部門コード") REFERENCES "部門マスタ"("部門コード")
);

COMMENT ON TABLE "倉庫マスタ" IS '倉庫マスタ';
COMMENT ON COLUMN "倉庫マスタ"."倉庫コード" IS '倉庫コード';
COMMENT ON COLUMN "倉庫マスタ"."倉庫区分" IS '倉庫区分';
COMMENT ON COLUMN "倉庫マスタ"."倉庫名" IS '倉庫名';

-- --------------------------------------------------
-- 在庫情報
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "在庫情報" (
    "ID" SERIAL PRIMARY KEY,
    "場所コード" VARCHAR(20) NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "在庫数量" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "合格数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "不良数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "未検査数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "uk_在庫_場所_品目" UNIQUE ("場所コード", "品目コード"),
    CONSTRAINT "fk_在庫_場所"
        FOREIGN KEY ("場所コード") REFERENCES "場所マスタ"("場所コード")
    -- 品目コードへの外部キーは、品目マスタが複合キー(品目コード, 適用開始日)のため設定しない
);

COMMENT ON TABLE "在庫情報" IS '在庫情報';
COMMENT ON COLUMN "在庫情報"."ID" IS 'サロゲートキー';
COMMENT ON COLUMN "在庫情報"."場所コード" IS '場所コード';
COMMENT ON COLUMN "在庫情報"."品目コード" IS '品目コード';
COMMENT ON COLUMN "在庫情報"."在庫数量" IS '総在庫数量';
COMMENT ON COLUMN "在庫情報"."合格数" IS '合格品数量';
COMMENT ON COLUMN "在庫情報"."不良数" IS '不良品数量';
COMMENT ON COLUMN "在庫情報"."未検査数" IS '未検査品数量';

CREATE INDEX "idx_在庫_場所" ON "在庫情報" ("場所コード");
CREATE INDEX "idx_在庫_品目" ON "在庫情報" ("品目コード");

-- --------------------------------------------------
-- 払出指示データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "払出指示データ" (
    "ID" SERIAL PRIMARY KEY,
    "払出指示番号" VARCHAR(20) UNIQUE NOT NULL,
    "オーダ番号" VARCHAR(20) NOT NULL,
    "払出指示日" DATE NOT NULL,
    "場所コード" VARCHAR(20) NOT NULL,
    "備考" VARCHAR(500),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_払出指示_オーダ"
        FOREIGN KEY ("オーダ番号") REFERENCES "オーダ情報"("オーダNO"),
    CONSTRAINT "fk_払出指示_場所"
        FOREIGN KEY ("場所コード") REFERENCES "場所マスタ"("場所コード")
);

COMMENT ON TABLE "払出指示データ" IS '払出指示データ';

CREATE INDEX "idx_払出指示_オーダ" ON "払出指示データ" ("オーダ番号");
CREATE INDEX "idx_払出指示_場所" ON "払出指示データ" ("場所コード");

-- --------------------------------------------------
-- 払出指示明細データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "払出指示明細データ" (
    "ID" SERIAL PRIMARY KEY,
    "払出指示番号" VARCHAR(20) NOT NULL,
    "払出行番号" INT NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "工順" INT NOT NULL,
    "払出数量" DECIMAL(15, 2) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "uk_払出指示明細" UNIQUE ("払出指示番号", "払出行番号"),
    CONSTRAINT "fk_払出指示明細_払出指示"
        FOREIGN KEY ("払出指示番号") REFERENCES "払出指示データ"("払出指示番号")
    -- 品目コードへの外部キーは、品目マスタが複合キー(品目コード, 適用開始日)のため設定しない
);

COMMENT ON TABLE "払出指示明細データ" IS '払出指示明細データ';

-- --------------------------------------------------
-- 払出データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "払出データ" (
    "ID" SERIAL PRIMARY KEY,
    "払出番号" VARCHAR(20) UNIQUE NOT NULL,
    "作業指示番号" VARCHAR(20) NOT NULL,
    "工順" INT NOT NULL,
    "場所コード" VARCHAR(20) NOT NULL,
    "払出日" DATE NOT NULL,
    "払出担当者コード" VARCHAR(20) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_払出_作業指示"
        FOREIGN KEY ("作業指示番号") REFERENCES "作業指示データ"("作業指示番号"),
    CONSTRAINT "fk_払出_場所"
        FOREIGN KEY ("場所コード") REFERENCES "場所マスタ"("場所コード")
);

COMMENT ON TABLE "払出データ" IS '払出データ';

CREATE INDEX "idx_払出_作業指示" ON "払出データ" ("作業指示番号");

-- --------------------------------------------------
-- 払出明細データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "払出明細データ" (
    "ID" SERIAL PRIMARY KEY,
    "払出番号" VARCHAR(20) NOT NULL,
    "払出行番号" INT NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "払出数" DECIMAL(15, 2) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "uk_払出明細" UNIQUE ("払出番号", "払出行番号"),
    CONSTRAINT "fk_払出明細_払出"
        FOREIGN KEY ("払出番号") REFERENCES "払出データ"("払出番号")
    -- 品目コードへの外部キーは、品目マスタが複合キー(品目コード, 適用開始日)のため設定しない
);

COMMENT ON TABLE "払出明細データ" IS '払出明細データ';

-- --------------------------------------------------
-- 棚卸ステータス ENUM
-- --------------------------------------------------
CREATE TYPE 棚卸ステータス AS ENUM ('発行済', '入力済', '確定');

-- --------------------------------------------------
-- 棚卸データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "棚卸データ" (
    "ID" SERIAL PRIMARY KEY,
    "棚卸番号" VARCHAR(20) UNIQUE NOT NULL,
    "場所コード" VARCHAR(20) NOT NULL,
    "棚卸日" DATE NOT NULL,
    "ステータス" 棚卸ステータス DEFAULT '発行済' NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_棚卸_場所"
        FOREIGN KEY ("場所コード") REFERENCES "場所マスタ"("場所コード")
);

COMMENT ON TABLE "棚卸データ" IS '棚卸データ';

CREATE INDEX "idx_棚卸_場所" ON "棚卸データ" ("場所コード");
CREATE INDEX "idx_棚卸_日付" ON "棚卸データ" ("棚卸日");

-- --------------------------------------------------
-- 棚卸明細データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "棚卸明細データ" (
    "ID" SERIAL PRIMARY KEY,
    "棚卸番号" VARCHAR(20) NOT NULL,
    "棚卸行番号" INT NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "帳簿数量" DECIMAL(15, 2) NOT NULL,
    "実棚数量" DECIMAL(15, 2),
    "差異数量" DECIMAL(15, 2),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "uk_棚卸明細" UNIQUE ("棚卸番号", "棚卸行番号"),
    CONSTRAINT "fk_棚卸明細_棚卸"
        FOREIGN KEY ("棚卸番号") REFERENCES "棚卸データ"("棚卸番号")
    -- 品目コードへの外部キーは、品目マスタが複合キー(品目コード, 適用開始日)のため設定しない
);

COMMENT ON TABLE "棚卸明細データ" IS '棚卸明細データ';

-- --------------------------------------------------
-- 在庫調整データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "在庫調整データ" (
    "ID" SERIAL PRIMARY KEY,
    "在庫調整番号" VARCHAR(30) UNIQUE NOT NULL,
    "棚卸番号" VARCHAR(20),
    "品目コード" VARCHAR(20) NOT NULL,
    "場所コード" VARCHAR(20) NOT NULL,
    "調整日" DATE NOT NULL,
    "調整担当者コード" VARCHAR(20) NOT NULL,
    "調整数" DECIMAL(15, 2) NOT NULL,
    "理由コード" VARCHAR(20) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_在庫調整_棚卸"
        FOREIGN KEY ("棚卸番号") REFERENCES "棚卸データ"("棚卸番号"),
    CONSTRAINT "fk_在庫調整_場所"
        FOREIGN KEY ("場所コード") REFERENCES "場所マスタ"("場所コード")
    -- 品目コードへの外部キーは、品目マスタが複合キー(品目コード, 適用開始日)のため設定しない
);

COMMENT ON TABLE "在庫調整データ" IS '在庫調整データ';
COMMENT ON COLUMN "在庫調整データ"."調整数" IS '調整数（+/-）';

CREATE INDEX "idx_在庫調整_棚卸" ON "在庫調整データ" ("棚卸番号");
