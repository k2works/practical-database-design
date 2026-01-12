-- V012__quality_management_tables.sql
-- 品質管理テーブル（chapter29.md 準拠）

-- ==================================================
-- 1. 欠点マスタの修正（欠点区分 → 欠点分類）
-- ==================================================
ALTER TABLE "欠点マスタ" RENAME COLUMN "欠点区分" TO "欠点分類";

-- ==================================================
-- 2. 既存の受入検査データテーブルに品質管理用カラムを追加
-- ==================================================
-- 仕入先コード
ALTER TABLE "受入検査データ" ADD COLUMN "仕入先コード" VARCHAR(20);
-- 検査数量
ALTER TABLE "受入検査データ" ADD COLUMN "検査数量" DECIMAL(15, 2);
-- 判定（VARCHAR型、Enumは PostgreSQL 固有機能のため）
ALTER TABLE "受入検査データ" ADD COLUMN "判定" VARCHAR(20);
-- 受入検査日 → 検査日 へのリネーム（互換性のため新カラム追加）
ALTER TABLE "受入検査データ" ADD COLUMN "検査日" DATE;
-- 検査担当者コードのリネーム（互換性のため新カラム追加）
ALTER TABLE "受入検査データ" ADD COLUMN "検査担当者コード" VARCHAR(20);
-- 合格数カラム（良品数と別名）
ALTER TABLE "受入検査データ" ADD COLUMN "合格数" DECIMAL(15, 2);
-- 不合格数カラム（不良品数と別名）
ALTER TABLE "受入検査データ" ADD COLUMN "不合格数" DECIMAL(15, 2);
-- 備考カラムの追加
ALTER TABLE "受入検査データ" ADD COLUMN "備考" VARCHAR(500);

-- データ同期（既存データがある場合）
UPDATE "受入検査データ"
SET "検査日" = "受入検査日",
    "検査担当者コード" = "受入検査担当者コード",
    "合格数" = "良品数",
    "不合格数" = "不良品数",
    "備考" = "受入検査備考"
WHERE "検査日" IS NULL;

-- ==================================================
-- 3. 受入検査結果データテーブルの作成
-- ==================================================
CREATE TABLE "受入検査結果データ" (
    "ID" SERIAL PRIMARY KEY,
    "受入検査番号" VARCHAR(20) NOT NULL,
    "欠点コード" VARCHAR(10) NOT NULL,
    "数量" DECIMAL(15, 2) NOT NULL,
    "備考" VARCHAR(500),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_受入検査結果 UNIQUE ("受入検査番号", "欠点コード"),
    CONSTRAINT fk_受入検査結果_受入検査 FOREIGN KEY ("受入検査番号")
        REFERENCES "受入検査データ"("受入検査番号"),
    CONSTRAINT fk_受入検査結果_欠点 FOREIGN KEY ("欠点コード")
        REFERENCES "欠点マスタ"("欠点コード")
);

CREATE INDEX idx_受入検査結果_受入検査番号 ON "受入検査結果データ"("受入検査番号");

-- ==================================================
-- 4. 工程検査データテーブルの作成
-- ==================================================
CREATE TABLE "工程検査データ" (
    "ID" SERIAL PRIMARY KEY,
    "工程検査番号" VARCHAR(20) UNIQUE NOT NULL,
    "作業指示番号" VARCHAR(20) NOT NULL,
    "工程コード" VARCHAR(10),
    "品目コード" VARCHAR(20) NOT NULL,
    "検査日" DATE NOT NULL,
    "検査担当者コード" VARCHAR(20) NOT NULL,
    "検査数量" DECIMAL(15, 2) NOT NULL,
    "合格数" DECIMAL(15, 2) NOT NULL,
    "不合格数" DECIMAL(15, 2) NOT NULL,
    "判定" VARCHAR(20) NOT NULL,
    "備考" VARCHAR(500),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT fk_工程検査_作業指示 FOREIGN KEY ("作業指示番号")
        REFERENCES "作業指示データ"("作業指示番号"),
    CONSTRAINT fk_工程検査_工程 FOREIGN KEY ("工程コード")
        REFERENCES "工程マスタ"("工程コード")
);

CREATE INDEX idx_工程検査_作業指示番号 ON "工程検査データ"("作業指示番号");
CREATE INDEX idx_工程検査_検査日 ON "工程検査データ"("検査日");
CREATE INDEX idx_工程検査_工程コード ON "工程検査データ"("工程コード");

-- ==================================================
-- 5. 工程検査結果データテーブルの作成
-- ==================================================
CREATE TABLE "工程検査結果データ" (
    "ID" SERIAL PRIMARY KEY,
    "工程検査番号" VARCHAR(20) NOT NULL,
    "欠点コード" VARCHAR(10) NOT NULL,
    "数量" DECIMAL(15, 2) NOT NULL,
    "備考" VARCHAR(500),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_工程検査結果 UNIQUE ("工程検査番号", "欠点コード"),
    CONSTRAINT fk_工程検査結果_工程検査 FOREIGN KEY ("工程検査番号")
        REFERENCES "工程検査データ"("工程検査番号"),
    CONSTRAINT fk_工程検査結果_欠点 FOREIGN KEY ("欠点コード")
        REFERENCES "欠点マスタ"("欠点コード")
);

CREATE INDEX idx_工程検査結果_工程検査番号 ON "工程検査結果データ"("工程検査番号");

-- ==================================================
-- 6. 出荷検査データテーブルの作成
-- ==================================================
CREATE TABLE "出荷検査データ" (
    "ID" SERIAL PRIMARY KEY,
    "出荷検査番号" VARCHAR(20) UNIQUE NOT NULL,
    "出荷番号" VARCHAR(20) NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "検査日" DATE NOT NULL,
    "検査担当者コード" VARCHAR(20) NOT NULL,
    "検査数量" DECIMAL(15, 2) NOT NULL,
    "合格数" DECIMAL(15, 2) NOT NULL,
    "不合格数" DECIMAL(15, 2) NOT NULL,
    "判定" VARCHAR(20) NOT NULL,
    "備考" VARCHAR(500),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "バージョン" INTEGER DEFAULT 1 NOT NULL
);

CREATE INDEX idx_出荷検査_出荷番号 ON "出荷検査データ"("出荷番号");
CREATE INDEX idx_出荷検査_検査日 ON "出荷検査データ"("検査日");

-- ==================================================
-- 7. 出荷検査結果データテーブルの作成
-- ==================================================
CREATE TABLE "出荷検査結果データ" (
    "ID" SERIAL PRIMARY KEY,
    "出荷検査番号" VARCHAR(20) NOT NULL,
    "欠点コード" VARCHAR(10) NOT NULL,
    "数量" DECIMAL(15, 2) NOT NULL,
    "備考" VARCHAR(500),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_出荷検査結果 UNIQUE ("出荷検査番号", "欠点コード"),
    CONSTRAINT fk_出荷検査結果_出荷検査 FOREIGN KEY ("出荷検査番号")
        REFERENCES "出荷検査データ"("出荷検査番号"),
    CONSTRAINT fk_出荷検査結果_欠点 FOREIGN KEY ("欠点コード")
        REFERENCES "欠点マスタ"("欠点コード")
);

CREATE INDEX idx_出荷検査結果_出荷検査番号 ON "出荷検査結果データ"("出荷検査番号");

-- ==================================================
-- 8. ロットマスタテーブルの作成
-- ==================================================
CREATE TABLE "ロットマスタ" (
    "ID" SERIAL PRIMARY KEY,
    "ロット番号" VARCHAR(30) UNIQUE NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "ロット種別" VARCHAR(20) NOT NULL,
    "製造日" DATE,
    "有効期限" DATE,
    "数量" DECIMAL(15, 2) NOT NULL,
    "倉庫コード" VARCHAR(20),
    "備考" VARCHAR(500),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "バージョン" INTEGER DEFAULT 1 NOT NULL
);

CREATE INDEX idx_ロット_品目コード ON "ロットマスタ"("品目コード");
CREATE INDEX idx_ロット_製造日 ON "ロットマスタ"("製造日");
CREATE INDEX idx_ロット_有効期限 ON "ロットマスタ"("有効期限");

-- ==================================================
-- 9. ロット構成テーブルの作成（トレーサビリティ用）
-- ==================================================
CREATE TABLE "ロット構成" (
    "ID" SERIAL PRIMARY KEY,
    "親ロット番号" VARCHAR(30) NOT NULL,
    "子ロット番号" VARCHAR(30) NOT NULL,
    "使用数量" DECIMAL(15, 2) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_ロット構成 UNIQUE ("親ロット番号", "子ロット番号"),
    CONSTRAINT fk_ロット構成_親 FOREIGN KEY ("親ロット番号")
        REFERENCES "ロットマスタ"("ロット番号"),
    CONSTRAINT fk_ロット構成_子 FOREIGN KEY ("子ロット番号")
        REFERENCES "ロットマスタ"("ロット番号")
);

CREATE INDEX idx_ロット構成_親ロット番号 ON "ロット構成"("親ロット番号");
CREATE INDEX idx_ロット構成_子ロット番号 ON "ロット構成"("子ロット番号");
