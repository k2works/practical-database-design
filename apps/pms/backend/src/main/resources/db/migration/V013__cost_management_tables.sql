-- V013__cost_management_tables.sql
-- 製造原価管理テーブル（chapter30.md 準拠）
-- 注意: 品目マスタは複合キー（品目コード, 適用開始日）のためFK参照不可

-- --------------------------------------------------
-- 材料消費データ
-- --------------------------------------------------
CREATE TABLE "材料消費データ" (
    "ID" SERIAL PRIMARY KEY,
    "作業指示番号" VARCHAR(20) NOT NULL,
    "材料コード" VARCHAR(20) NOT NULL,
    "消費日" DATE NOT NULL,
    "消費数量" DECIMAL(15, 2) NOT NULL,
    "単価" DECIMAL(15, 4) NOT NULL,
    "消費金額" DECIMAL(15, 2) NOT NULL,
    "直接材料フラグ" BOOLEAN NOT NULL DEFAULT true,
    "備考" VARCHAR(500),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT "FK_材料消費_作業指示" FOREIGN KEY ("作業指示番号")
        REFERENCES "作業指示データ"("作業指示番号")
);

COMMENT ON TABLE "材料消費データ" IS '材料消費データ';
COMMENT ON COLUMN "材料消費データ"."直接材料フラグ" IS '直接材料=true, 間接材料=false';
COMMENT ON COLUMN "材料消費データ"."バージョン" IS '楽観ロック用バージョン番号';

CREATE INDEX "IDX_材料消費_作業指示" ON "材料消費データ" ("作業指示番号");
CREATE INDEX "IDX_材料消費_消費日" ON "材料消費データ" ("消費日");

-- --------------------------------------------------
-- 賃率マスタ
-- --------------------------------------------------
CREATE TABLE "賃率マスタ" (
    "ID" SERIAL PRIMARY KEY,
    "作業者区分コード" VARCHAR(20) NOT NULL,
    "作業者区分名" VARCHAR(100) NOT NULL,
    "適用開始日" DATE NOT NULL,
    "適用終了日" DATE,
    "時間単価" DECIMAL(15, 2) NOT NULL,
    "直接労務フラグ" BOOLEAN NOT NULL DEFAULT true,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT "UK_賃率_区分_適用開始日" UNIQUE ("作業者区分コード", "適用開始日")
);

COMMENT ON TABLE "賃率マスタ" IS '賃率マスタ';
COMMENT ON COLUMN "賃率マスタ"."時間単価" IS '1時間あたりの賃率';
COMMENT ON COLUMN "賃率マスタ"."直接労務フラグ" IS '直接労務=true, 間接労務=false';
COMMENT ON COLUMN "賃率マスタ"."バージョン" IS '楽観ロック用バージョン番号';

-- --------------------------------------------------
-- 労務費データ（工数実績に労務費計算カラムを追加）
-- --------------------------------------------------
ALTER TABLE "工数実績データ" ADD COLUMN IF NOT EXISTS "作業者区分コード" VARCHAR(20);
ALTER TABLE "工数実績データ" ADD COLUMN IF NOT EXISTS "時間単価" DECIMAL(15, 2);
ALTER TABLE "工数実績データ" ADD COLUMN IF NOT EXISTS "労務費" DECIMAL(15, 2);
ALTER TABLE "工数実績データ" ADD COLUMN IF NOT EXISTS "直接労務フラグ" BOOLEAN DEFAULT true;

COMMENT ON COLUMN "工数実績データ"."作業者区分コード" IS '作業者区分（賃率マスタ参照）';
COMMENT ON COLUMN "工数実績データ"."時間単価" IS '1時間あたりの賃率';
COMMENT ON COLUMN "工数実績データ"."労務費" IS '工数 × 時間単価';
COMMENT ON COLUMN "工数実績データ"."直接労務フラグ" IS '直接労務=true, 間接労務=false';

-- --------------------------------------------------
-- 製造間接費マスタ
-- --------------------------------------------------
CREATE TABLE "製造間接費マスタ" (
    "ID" SERIAL PRIMARY KEY,
    "会計期間" VARCHAR(7) NOT NULL,
    "費用区分" VARCHAR(20) NOT NULL,
    "費用区分名" VARCHAR(100) NOT NULL,
    "金額" DECIMAL(15, 2) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT "UK_製造間接費_期間_区分" UNIQUE ("会計期間", "費用区分")
);

COMMENT ON TABLE "製造間接費マスタ" IS '製造間接費マスタ';
COMMENT ON COLUMN "製造間接費マスタ"."会計期間" IS '会計期間（YYYY-MM形式）';
COMMENT ON COLUMN "製造間接費マスタ"."費用区分" IS '費用区分（間接材料/間接労務/減価償却/水道光熱など）';
COMMENT ON COLUMN "製造間接費マスタ"."バージョン" IS '楽観ロック用バージョン番号';

CREATE INDEX "IDX_製造間接費_期間" ON "製造間接費マスタ" ("会計期間");

-- --------------------------------------------------
-- 製造間接費配賦データ
-- --------------------------------------------------
CREATE TABLE "製造間接費配賦データ" (
    "ID" SERIAL PRIMARY KEY,
    "作業指示番号" VARCHAR(20) NOT NULL,
    "会計期間" VARCHAR(7) NOT NULL,
    "配賦基準" VARCHAR(50) NOT NULL,
    "基準金額" DECIMAL(15, 2) NOT NULL,
    "配賦率" DECIMAL(10, 6) NOT NULL,
    "配賦金額" DECIMAL(15, 2) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "UK_間接費配賦_作業指示_期間" UNIQUE ("作業指示番号", "会計期間"),
    CONSTRAINT "FK_間接費配賦_作業指示" FOREIGN KEY ("作業指示番号")
        REFERENCES "作業指示データ"("作業指示番号")
);

COMMENT ON TABLE "製造間接費配賦データ" IS '製造間接費配賦データ';
COMMENT ON COLUMN "製造間接費配賦データ"."配賦基準" IS '配賦基準（直接作業時間/機械稼働時間/直接材料費/直接労務費）';
COMMENT ON COLUMN "製造間接費配賦データ"."配賦率" IS '製造間接費 ÷ 配賦基準総額';

CREATE INDEX "IDX_間接費配賦_作業指示" ON "製造間接費配賦データ" ("作業指示番号");

-- --------------------------------------------------
-- 標準原価マスタ
-- --------------------------------------------------
CREATE TABLE "標準原価マスタ" (
    "ID" SERIAL PRIMARY KEY,
    "品目コード" VARCHAR(20) NOT NULL,
    "適用開始日" DATE NOT NULL,
    "適用終了日" DATE,
    "標準材料費" DECIMAL(15, 2) NOT NULL,
    "標準労務費" DECIMAL(15, 2) NOT NULL,
    "標準経費" DECIMAL(15, 2) NOT NULL,
    "標準製造原価" DECIMAL(15, 2) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT "UK_標準原価_品目_適用開始日" UNIQUE ("品目コード", "適用開始日")
);

COMMENT ON TABLE "標準原価マスタ" IS '標準原価マスタ';
COMMENT ON COLUMN "標準原価マスタ"."標準材料費" IS '標準材料費（単位あたり）';
COMMENT ON COLUMN "標準原価マスタ"."標準労務費" IS '標準労務費（単位あたり）';
COMMENT ON COLUMN "標準原価マスタ"."標準経費" IS '標準経費（単位あたり）';
COMMENT ON COLUMN "標準原価マスタ"."標準製造原価" IS '標準製造原価（単位あたり）= 材料費 + 労務費 + 経費';
COMMENT ON COLUMN "標準原価マスタ"."バージョン" IS '楽観ロック用バージョン番号';

CREATE INDEX "IDX_標準原価_品目" ON "標準原価マスタ" ("品目コード");

-- --------------------------------------------------
-- 実際原価データ
-- --------------------------------------------------
CREATE TABLE "実際原価データ" (
    "ID" SERIAL PRIMARY KEY,
    "作業指示番号" VARCHAR(20) NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "完成数量" DECIMAL(15, 2) NOT NULL,
    "実際材料費" DECIMAL(15, 2) NOT NULL,
    "実際労務費" DECIMAL(15, 2) NOT NULL,
    "実際経費" DECIMAL(15, 2) NOT NULL,
    "実際製造原価" DECIMAL(15, 2) NOT NULL,
    "単位原価" DECIMAL(15, 4) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT "UK_実際原価_作業指示" UNIQUE ("作業指示番号"),
    CONSTRAINT "FK_実際原価_作業指示" FOREIGN KEY ("作業指示番号")
        REFERENCES "作業指示データ"("作業指示番号")
);

COMMENT ON TABLE "実際原価データ" IS '実際原価データ';
COMMENT ON COLUMN "実際原価データ"."単位原価" IS '実際製造原価 ÷ 完成数量';
COMMENT ON COLUMN "実際原価データ"."バージョン" IS '楽観ロック用バージョン番号';

CREATE INDEX "IDX_実際原価_作業指示" ON "実際原価データ" ("作業指示番号");

-- --------------------------------------------------
-- 原価差異データ
-- --------------------------------------------------
CREATE TABLE "原価差異データ" (
    "ID" SERIAL PRIMARY KEY,
    "作業指示番号" VARCHAR(20) NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "材料費差異" DECIMAL(15, 2) NOT NULL,
    "労務費差異" DECIMAL(15, 2) NOT NULL,
    "経費差異" DECIMAL(15, 2) NOT NULL,
    "総差異" DECIMAL(15, 2) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "UK_原価差異_作業指示" UNIQUE ("作業指示番号"),
    CONSTRAINT "FK_原価差異_作業指示" FOREIGN KEY ("作業指示番号")
        REFERENCES "作業指示データ"("作業指示番号")
);

COMMENT ON TABLE "原価差異データ" IS '原価差異データ';
COMMENT ON COLUMN "原価差異データ"."材料費差異" IS '実際材料費 - 標準材料費 × 完成数量';
COMMENT ON COLUMN "原価差異データ"."労務費差異" IS '実際労務費 - 標準労務費 × 完成数量';
COMMENT ON COLUMN "原価差異データ"."経費差異" IS '実際経費 - 標準経費 × 完成数量';
COMMENT ON COLUMN "原価差異データ"."総差異" IS '材料費差異 + 労務費差異 + 経費差異';

CREATE INDEX "IDX_原価差異_作業指示" ON "原価差異データ" ("作業指示番号");
