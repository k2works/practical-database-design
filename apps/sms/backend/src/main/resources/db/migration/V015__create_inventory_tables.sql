-- 倉庫区分
CREATE TYPE 倉庫区分 AS ENUM ('自社', '外部', '仮想');

-- 倉庫マスタに倉庫区分カラムを追加
ALTER TABLE "倉庫マスタ" ADD COLUMN "倉庫区分" 倉庫区分 DEFAULT '自社' NOT NULL;

-- ロケーションマスタ
CREATE TABLE "ロケーションマスタ" (
    "ロケーションコード" VARCHAR(20) PRIMARY KEY,
    "倉庫コード" VARCHAR(20) NOT NULL,
    "棚番" VARCHAR(20) NOT NULL,
    "ゾーン" VARCHAR(10),
    "通路" VARCHAR(10),
    "ラック" VARCHAR(10),
    "段" VARCHAR(10),
    "間口" VARCHAR(10),
    "有効フラグ" BOOLEAN DEFAULT TRUE NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_ロケーション_倉庫"
        FOREIGN KEY ("倉庫コード") REFERENCES "倉庫マスタ"("倉庫コード"),
    UNIQUE ("倉庫コード", "棚番")
);

-- 在庫データ（倉庫 × 商品）
CREATE TABLE "在庫データ" (
    "ID" SERIAL PRIMARY KEY,
    "倉庫コード" VARCHAR(20) NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "ロケーションコード" VARCHAR(20),
    "現在庫数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "引当数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "発注残数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "最終入庫日" DATE,
    "最終出庫日" DATE,
    "ロット番号" VARCHAR(50),
    "シリアル番号" VARCHAR(50),
    "有効期限" DATE,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_在庫_倉庫"
        FOREIGN KEY ("倉庫コード") REFERENCES "倉庫マスタ"("倉庫コード"),
    CONSTRAINT "fk_在庫_商品"
        FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT "fk_在庫_ロケーション"
        FOREIGN KEY ("ロケーションコード") REFERENCES "ロケーションマスタ"("ロケーションコード"),
    UNIQUE ("倉庫コード", "商品コード", "ロケーションコード", "ロット番号")
);

-- 有効在庫数を計算するビュー
CREATE VIEW "有効在庫ビュー" AS
SELECT
    "倉庫コード",
    "商品コード",
    "ロケーションコード",
    "現在庫数",
    "引当数",
    "現在庫数" - "引当数" AS "有効在庫数",
    "発注残数",
    "現在庫数" - "引当数" + "発注残数" AS "予定在庫数"
FROM "在庫データ";

-- インデックス
CREATE INDEX "idx_ロケーション_倉庫" ON "ロケーションマスタ"("倉庫コード");
CREATE INDEX "idx_在庫_倉庫コード" ON "在庫データ"("倉庫コード");
CREATE INDEX "idx_在庫_商品コード" ON "在庫データ"("商品コード");
CREATE INDEX "idx_在庫_ロケーション" ON "在庫データ"("ロケーションコード");
CREATE INDEX "idx_在庫_ロット番号" ON "在庫データ"("ロット番号");
CREATE INDEX "idx_在庫_有効期限" ON "在庫データ"("有効期限");

-- テーブルコメント
COMMENT ON TABLE "ロケーションマスタ" IS '倉庫内の棚・ロケーション情報を管理するテーブル';
COMMENT ON TABLE "在庫データ" IS '倉庫×商品の在庫情報を管理するテーブル';
COMMENT ON COLUMN "在庫データ"."バージョン" IS '楽観ロック用バージョン番号';
