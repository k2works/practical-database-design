-- ==================================================
-- 生産管理システム（PMS）工程管理スキーマ
-- V5: 工程管理関連テーブル
-- ==================================================

-- --------------------------------------------------
-- ENUM型定義（PostgreSQL）
-- --------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = '作業指示ステータス') THEN
        CREATE TYPE 作業指示ステータス AS ENUM ('未着手', '作業中', '完了', '中断');
    END IF;
END$$;

-- --------------------------------------------------
-- 作業指示データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "作業指示データ" (
    "ID" SERIAL PRIMARY KEY,
    "作業指示番号" VARCHAR(20) UNIQUE NOT NULL,
    "オーダ番号" VARCHAR(20) NOT NULL,
    "作業指示日" DATE NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "作業指示数" DECIMAL(15, 2) NOT NULL,
    "場所コード" VARCHAR(20) NOT NULL,
    "開始予定日" DATE NOT NULL,
    "完成予定日" DATE NOT NULL,
    "実績開始日" DATE,
    "実績完了日" DATE,
    "完成済数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "総良品数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "総不良品数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "ステータス" 作業指示ステータス DEFAULT '未着手' NOT NULL,
    "完了フラグ" BOOLEAN DEFAULT false NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_作業指示_オーダ"
        FOREIGN KEY ("オーダ番号") REFERENCES "オーダ情報"("オーダNO"),
    CONSTRAINT "fk_作業指示_場所"
        FOREIGN KEY ("場所コード") REFERENCES "場所マスタ"("場所コード")
);

COMMENT ON TABLE "作業指示データ" IS 'オーダから展開された作業指示を管理するテーブル';

-- --------------------------------------------------
-- 作業指示明細データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "作業指示明細データ" (
    "ID" SERIAL PRIMARY KEY,
    "作業指示番号" VARCHAR(20) NOT NULL,
    "工順" INTEGER NOT NULL,
    "工程コード" VARCHAR(10) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_作業指示明細_作業指示"
        FOREIGN KEY ("作業指示番号") REFERENCES "作業指示データ"("作業指示番号"),
    CONSTRAINT "fk_作業指示明細_工程"
        FOREIGN KEY ("工程コード") REFERENCES "工程マスタ"("工程コード"),
    UNIQUE ("作業指示番号", "工順")
);

COMMENT ON TABLE "作業指示明細データ" IS '作業指示の工程別明細を管理するテーブル';

-- --------------------------------------------------
-- 完成実績データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "完成実績データ" (
    "ID" SERIAL PRIMARY KEY,
    "完成実績番号" VARCHAR(20) UNIQUE NOT NULL,
    "作業指示番号" VARCHAR(20) NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "完成日" DATE NOT NULL,
    "完成数量" DECIMAL(15, 2) NOT NULL,
    "良品数" DECIMAL(15, 2) NOT NULL,
    "不良品数" DECIMAL(15, 2) NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_完成実績_作業指示"
        FOREIGN KEY ("作業指示番号") REFERENCES "作業指示データ"("作業指示番号")
);

COMMENT ON TABLE "完成実績データ" IS '製造現場からの完成実績を記録するテーブル';

-- --------------------------------------------------
-- 完成検査結果データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "完成検査結果データ" (
    "ID" SERIAL PRIMARY KEY,
    "完成実績番号" VARCHAR(20) NOT NULL,
    "欠点コード" VARCHAR(10) NOT NULL,
    "数量" DECIMAL(15, 2) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_完成検査結果_完成実績"
        FOREIGN KEY ("完成実績番号") REFERENCES "完成実績データ"("完成実績番号"),
    CONSTRAINT "fk_完成検査結果_欠点"
        FOREIGN KEY ("欠点コード") REFERENCES "欠点マスタ"("欠点コード"),
    UNIQUE ("完成実績番号", "欠点コード")
);

COMMENT ON TABLE "完成検査結果データ" IS '完成検査で発見された欠点を記録するテーブル';

-- --------------------------------------------------
-- 工数実績データ
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "工数実績データ" (
    "ID" SERIAL PRIMARY KEY,
    "工数実績番号" VARCHAR(20) UNIQUE NOT NULL,
    "作業指示番号" VARCHAR(20) NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "工順" INTEGER NOT NULL,
    "工程コード" VARCHAR(10) NOT NULL,
    "部門コード" VARCHAR(10) NOT NULL,
    "担当者コード" VARCHAR(10) NOT NULL,
    "作業日" DATE NOT NULL,
    "工数" DECIMAL(10, 2) NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_工数実績_作業指示"
        FOREIGN KEY ("作業指示番号") REFERENCES "作業指示データ"("作業指示番号"),
    CONSTRAINT "fk_工数実績_工程"
        FOREIGN KEY ("工程コード") REFERENCES "工程マスタ"("工程コード"),
    CONSTRAINT "fk_工数実績_部門"
        FOREIGN KEY ("部門コード") REFERENCES "部門マスタ"("部門コード")
);

COMMENT ON TABLE "工数実績データ" IS '工順別の作業工数を記録するテーブル';

-- --------------------------------------------------
-- インデックス作成
-- --------------------------------------------------
CREATE INDEX IF NOT EXISTS "idx_作業指示_オーダ番号" ON "作業指示データ"("オーダ番号");
CREATE INDEX IF NOT EXISTS "idx_作業指示_品目コード" ON "作業指示データ"("品目コード");
CREATE INDEX IF NOT EXISTS "idx_作業指示_ステータス" ON "作業指示データ"("ステータス");
CREATE INDEX IF NOT EXISTS "idx_作業指示_作業指示日" ON "作業指示データ"("作業指示日");
CREATE INDEX IF NOT EXISTS "idx_作業指示明細_作業指示番号" ON "作業指示明細データ"("作業指示番号");
CREATE INDEX IF NOT EXISTS "idx_作業指示明細_工程コード" ON "作業指示明細データ"("工程コード");
CREATE INDEX IF NOT EXISTS "idx_完成実績_作業指示番号" ON "完成実績データ"("作業指示番号");
CREATE INDEX IF NOT EXISTS "idx_完成実績_品目コード" ON "完成実績データ"("品目コード");
CREATE INDEX IF NOT EXISTS "idx_完成実績_完成日" ON "完成実績データ"("完成日");
CREATE INDEX IF NOT EXISTS "idx_完成検査結果_完成実績番号" ON "完成検査結果データ"("完成実績番号");
CREATE INDEX IF NOT EXISTS "idx_工数実績_作業指示番号" ON "工数実績データ"("作業指示番号");
CREATE INDEX IF NOT EXISTS "idx_工数実績_工程コード" ON "工数実績データ"("工程コード");
CREATE INDEX IF NOT EXISTS "idx_工数実績_担当者コード" ON "工数実績データ"("担当者コード");
CREATE INDEX IF NOT EXISTS "idx_工数実績_作業日" ON "工数実績データ"("作業日");
