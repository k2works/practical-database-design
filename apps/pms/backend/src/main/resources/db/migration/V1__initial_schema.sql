-- ==================================================
-- 生産管理システム（PMS）初期スキーマ
-- V1: 基本マスタテーブル
-- ==================================================

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
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者" VARCHAR(50),
    "バージョン" INTEGER DEFAULT 1
);

COMMENT ON TABLE "部門マスタ" IS '組織の部門情報を管理するマスタテーブル';
COMMENT ON COLUMN "部門マスタ"."部門コード" IS '部門を一意に識別するコード';
COMMENT ON COLUMN "部門マスタ"."部門名" IS '部門の名称';
COMMENT ON COLUMN "部門マスタ"."部門パス" IS '階層構造を表すパス（例：本社/製造部/第一課）';
COMMENT ON COLUMN "部門マスタ"."最下層区分" IS '最下層の部門かどうかを示すフラグ';

-- --------------------------------------------------
-- 担当者マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "担当者マスタ" (
    "担当者コード" VARCHAR(10) PRIMARY KEY,
    "担当者名" VARCHAR(100) NOT NULL,
    "部門コード" VARCHAR(10),
    "メールアドレス" VARCHAR(255),
    "電話番号" VARCHAR(20),
    "有効開始日" DATE NOT NULL,
    "有効終了日" DATE,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者" VARCHAR(50),
    "バージョン" INTEGER DEFAULT 1,
    FOREIGN KEY ("部門コード") REFERENCES "部門マスタ"("部門コード")
);

COMMENT ON TABLE "担当者マスタ" IS '担当者情報を管理するマスタテーブル';

-- --------------------------------------------------
-- 場所マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "場所マスタ" (
    "場所コード" VARCHAR(10) PRIMARY KEY,
    "場所名" VARCHAR(100) NOT NULL,
    "場所種類" VARCHAR(20) NOT NULL,
    "部門コード" VARCHAR(10),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者" VARCHAR(50),
    "バージョン" INTEGER DEFAULT 1,
    FOREIGN KEY ("部門コード") REFERENCES "部門マスタ"("部門コード")
);

COMMENT ON TABLE "場所マスタ" IS '倉庫・工場・ラインなどの場所情報を管理するマスタテーブル';
COMMENT ON COLUMN "場所マスタ"."場所種類" IS '場所の種類（倉庫、工場、ライン等）';

-- --------------------------------------------------
-- 単位マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "単位マスタ" (
    "単位コード" VARCHAR(10) PRIMARY KEY,
    "単位名" VARCHAR(50) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者" VARCHAR(50),
    "バージョン" INTEGER DEFAULT 1
);

COMMENT ON TABLE "単位マスタ" IS '数量の単位を管理するマスタテーブル';

-- --------------------------------------------------
-- 品目マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "品目マスタ" (
    "品目コード" VARCHAR(20) PRIMARY KEY,
    "品目名" VARCHAR(200) NOT NULL,
    "品目区分" VARCHAR(20) NOT NULL,
    "品目グループコード" VARCHAR(10),
    "単位コード" VARCHAR(10),
    "場所コード" VARCHAR(10),
    "リードタイム" INTEGER DEFAULT 0,
    "安全リードタイム" INTEGER DEFAULT 0,
    "安全在庫数" DECIMAL(15, 3) DEFAULT 0,
    "歩留率" DECIMAL(5, 2) DEFAULT 100.00,
    "最小ロット数" DECIMAL(15, 3) DEFAULT 1,
    "刻みロット数" DECIMAL(15, 3) DEFAULT 1,
    "最大ロット数" DECIMAL(15, 3),
    "有効開始日" DATE NOT NULL,
    "有効終了日" DATE,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者" VARCHAR(50),
    "バージョン" INTEGER DEFAULT 1,
    FOREIGN KEY ("単位コード") REFERENCES "単位マスタ"("単位コード"),
    FOREIGN KEY ("場所コード") REFERENCES "場所マスタ"("場所コード")
);

COMMENT ON TABLE "品目マスタ" IS '製品・半製品・部品・材料などの品目情報を管理するマスタテーブル';
COMMENT ON COLUMN "品目マスタ"."品目区分" IS '品目の種類（製品、半製品、中間品、部品、材料、原料、資材）';
COMMENT ON COLUMN "品目マスタ"."リードタイム" IS '製造または調達にかかる日数';
COMMENT ON COLUMN "品目マスタ"."歩留率" IS '製造時の良品率（%）';

-- --------------------------------------------------
-- 部品構成表（BOM）
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "部品構成表" (
    "親品目コード" VARCHAR(20) NOT NULL,
    "子品目コード" VARCHAR(20) NOT NULL,
    "工順" INTEGER NOT NULL DEFAULT 1,
    "基準量" DECIMAL(15, 3) NOT NULL DEFAULT 1,
    "必要量" DECIMAL(15, 6) NOT NULL,
    "不良率" DECIMAL(5, 2) DEFAULT 0,
    "有効開始日" DATE NOT NULL,
    "有効終了日" DATE,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者" VARCHAR(50),
    "バージョン" INTEGER DEFAULT 1,
    PRIMARY KEY ("親品目コード", "子品目コード", "工順"),
    FOREIGN KEY ("親品目コード") REFERENCES "品目マスタ"("品目コード"),
    FOREIGN KEY ("子品目コード") REFERENCES "品目マスタ"("品目コード")
);

COMMENT ON TABLE "部品構成表" IS '製品を構成する部品・材料の構成情報（BOM）';
COMMENT ON COLUMN "部品構成表"."基準量" IS '親品目の基準となる数量';
COMMENT ON COLUMN "部品構成表"."必要量" IS '基準量あたりに必要な子品目の数量';
COMMENT ON COLUMN "部品構成表"."不良率" IS '製造時に発生する不良の割合（%）';

-- --------------------------------------------------
-- 工程マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "工程マスタ" (
    "工程コード" VARCHAR(10) PRIMARY KEY,
    "工程名" VARCHAR(100) NOT NULL,
    "工程区分" VARCHAR(20),
    "場所コード" VARCHAR(10),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者" VARCHAR(50),
    "バージョン" INTEGER DEFAULT 1,
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
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者" VARCHAR(50),
    "バージョン" INTEGER DEFAULT 1,
    PRIMARY KEY ("品目コード", "工順"),
    FOREIGN KEY ("品目コード") REFERENCES "品目マスタ"("品目コード"),
    FOREIGN KEY ("工程コード") REFERENCES "工程マスタ"("工程コード")
);

COMMENT ON TABLE "工程表" IS '品目ごとの製造工程順序を定義するテーブル';

-- --------------------------------------------------
-- 取引先マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "取引先マスタ" (
    "取引先コード" VARCHAR(10) PRIMARY KEY,
    "取引先名" VARCHAR(200) NOT NULL,
    "取引先区分" VARCHAR(20) NOT NULL,
    "郵便番号" VARCHAR(10),
    "住所" VARCHAR(500),
    "電話番号" VARCHAR(20),
    "FAX番号" VARCHAR(20),
    "担当者名" VARCHAR(100),
    "メールアドレス" VARCHAR(255),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者" VARCHAR(50),
    "バージョン" INTEGER DEFAULT 1
);

COMMENT ON TABLE "取引先マスタ" IS '仕入先・外注先・顧客などの取引先情報を管理するマスタテーブル';
COMMENT ON COLUMN "取引先マスタ"."取引先区分" IS '取引先の種類（仕入先、外注先、顧客等）';

-- --------------------------------------------------
-- 単価マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "単価マスタ" (
    "品目コード" VARCHAR(20) NOT NULL,
    "取引先コード" VARCHAR(10) NOT NULL,
    "ロット単位数" DECIMAL(15, 3) NOT NULL DEFAULT 1,
    "単価" DECIMAL(15, 2) NOT NULL,
    "通貨コード" VARCHAR(3) DEFAULT 'JPY',
    "有効開始日" DATE NOT NULL,
    "有効終了日" DATE,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者" VARCHAR(50),
    "バージョン" INTEGER DEFAULT 1,
    PRIMARY KEY ("品目コード", "取引先コード", "ロット単位数", "有効開始日"),
    FOREIGN KEY ("品目コード") REFERENCES "品目マスタ"("品目コード"),
    FOREIGN KEY ("取引先コード") REFERENCES "取引先マスタ"("取引先コード")
);

COMMENT ON TABLE "単価マスタ" IS '品目と取引先の組み合わせによる単価情報を管理するマスタテーブル';

-- --------------------------------------------------
-- カレンダマスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "カレンダマスタ" (
    "カレンダ日付" DATE PRIMARY KEY,
    "就業日区分" VARCHAR(10) NOT NULL,
    "就業時間" DECIMAL(5, 2),
    "シフト" VARCHAR(20),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者" VARCHAR(50),
    "バージョン" INTEGER DEFAULT 1
);

COMMENT ON TABLE "カレンダマスタ" IS '就業日・休日などのカレンダー情報を管理するマスタテーブル';
COMMENT ON COLUMN "カレンダマスタ"."就業日区分" IS '就業日の区分（稼働日、休日、祝日等）';

-- --------------------------------------------------
-- 欠点マスタ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "欠点マスタ" (
    "欠点コード" VARCHAR(10) PRIMARY KEY,
    "欠点名" VARCHAR(100) NOT NULL,
    "欠点区分" VARCHAR(20),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者" VARCHAR(50),
    "バージョン" INTEGER DEFAULT 1
);

COMMENT ON TABLE "欠点マスタ" IS '品質不良の種類を管理するマスタテーブル';

-- --------------------------------------------------
-- インデックス作成
-- --------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_item_type ON "品目マスタ"("品目区分");
CREATE INDEX IF NOT EXISTS idx_item_group ON "品目マスタ"("品目グループコード");
CREATE INDEX IF NOT EXISTS idx_bom_parent ON "部品構成表"("親品目コード");
CREATE INDEX IF NOT EXISTS idx_bom_child ON "部品構成表"("子品目コード");
CREATE INDEX IF NOT EXISTS idx_routing_item ON "工程表"("品目コード");
CREATE INDEX IF NOT EXISTS idx_supplier_type ON "取引先マスタ"("取引先区分");
CREATE INDEX IF NOT EXISTS idx_price_item ON "単価マスタ"("品目コード");
CREATE INDEX IF NOT EXISTS idx_price_supplier ON "単価マスタ"("取引先コード");
