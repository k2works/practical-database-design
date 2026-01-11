-- ==================================================
-- 生産管理システム（PMS）生産計画スキーマ
-- V2: 生産計画関連テーブル
-- ==================================================

-- --------------------------------------------------
-- ENUM型定義（PostgreSQL）
-- --------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = '計画ステータス') THEN
        CREATE TYPE 計画ステータス AS ENUM ('草案', '確定', '展開済', '取消');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'オーダ種別') THEN
        CREATE TYPE オーダ種別 AS ENUM ('購買', '製造');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = '引当区分') THEN
        CREATE TYPE 引当区分 AS ENUM ('在庫', '発注残', '製造残');
    END IF;
END$$;

-- --------------------------------------------------
-- 基準生産計画
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "基準生産計画" (
    "ID" SERIAL PRIMARY KEY,
    "MPS番号" VARCHAR(20) UNIQUE NOT NULL,
    "計画日" DATE NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "計画数量" DECIMAL(15, 2) NOT NULL,
    "納期" DATE NOT NULL,
    "ステータス" 計画ステータス DEFAULT '草案' NOT NULL,
    "場所コード" VARCHAR(20),
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50)
);

COMMENT ON TABLE "基準生産計画" IS '基準生産計画（MPS）を管理するテーブル';

-- --------------------------------------------------
-- オーダ情報（購買・製造共通）
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "オーダ情報" (
    "ID" SERIAL PRIMARY KEY,
    "オーダNO" VARCHAR(20) UNIQUE NOT NULL,
    "オーダ種別" オーダ種別 NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "着手予定日" DATE NOT NULL,
    "納期" DATE NOT NULL,
    "有効期限" DATE,
    "計画数量" DECIMAL(15, 2) NOT NULL,
    "場所コード" VARCHAR(20) NOT NULL,
    "ステータス" 計画ステータス DEFAULT '草案' NOT NULL,
    "MPS_ID" INTEGER,
    "親オーダID" INTEGER,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_オーダ情報_MPS"
        FOREIGN KEY ("MPS_ID") REFERENCES "基準生産計画"("ID"),
    CONSTRAINT "fk_オーダ情報_親オーダ"
        FOREIGN KEY ("親オーダID") REFERENCES "オーダ情報"("ID")
);

COMMENT ON TABLE "オーダ情報" IS '購買オーダ・製造オーダを管理するテーブル';

-- --------------------------------------------------
-- 所要情報
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "所要情報" (
    "ID" SERIAL PRIMARY KEY,
    "所要NO" VARCHAR(20) UNIQUE NOT NULL,
    "オーダID" INTEGER NOT NULL,
    "品目コード" VARCHAR(20) NOT NULL,
    "納期" DATE NOT NULL,
    "必要数量" DECIMAL(15, 2) NOT NULL,
    "引当済数量" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "不足数量" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "場所コード" VARCHAR(20) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_所要情報_オーダ"
        FOREIGN KEY ("オーダID") REFERENCES "オーダ情報"("ID")
);

COMMENT ON TABLE "所要情報" IS '所要量情報を管理するテーブル';

-- --------------------------------------------------
-- 引当情報
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS "引当情報" (
    "ID" SERIAL PRIMARY KEY,
    "所要ID" INTEGER NOT NULL,
    "引当区分" 引当区分 NOT NULL,
    "オーダID" INTEGER,
    "引当日" DATE NOT NULL,
    "引当数量" DECIMAL(15, 2) NOT NULL,
    "場所コード" VARCHAR(20) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_引当情報_所要"
        FOREIGN KEY ("所要ID") REFERENCES "所要情報"("ID"),
    CONSTRAINT "fk_引当情報_オーダ"
        FOREIGN KEY ("オーダID") REFERENCES "オーダ情報"("ID")
);

COMMENT ON TABLE "引当情報" IS '引当情報を管理するテーブル';

-- --------------------------------------------------
-- インデックス作成
-- --------------------------------------------------
CREATE INDEX IF NOT EXISTS "idx_基準生産計画_品目コード" ON "基準生産計画"("品目コード");
CREATE INDEX IF NOT EXISTS "idx_基準生産計画_納期" ON "基準生産計画"("納期");
CREATE INDEX IF NOT EXISTS "idx_基準生産計画_ステータス" ON "基準生産計画"("ステータス");
CREATE INDEX IF NOT EXISTS "idx_オーダ情報_品目コード" ON "オーダ情報"("品目コード");
CREATE INDEX IF NOT EXISTS "idx_オーダ情報_納期" ON "オーダ情報"("納期");
CREATE INDEX IF NOT EXISTS "idx_オーダ情報_MPS_ID" ON "オーダ情報"("MPS_ID");
CREATE INDEX IF NOT EXISTS "idx_オーダ情報_オーダ種別" ON "オーダ情報"("オーダ種別");
CREATE INDEX IF NOT EXISTS "idx_所要情報_オーダID" ON "所要情報"("オーダID");
CREATE INDEX IF NOT EXISTS "idx_所要情報_品目コード" ON "所要情報"("品目コード");
CREATE INDEX IF NOT EXISTS "idx_引当情報_所要ID" ON "引当情報"("所要ID");
