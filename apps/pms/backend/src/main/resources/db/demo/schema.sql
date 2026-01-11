-- ==================================================
-- 生産管理システム（PMS）デモ用スキーマ（H2互換）
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
