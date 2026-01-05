-- 移動区分
CREATE TYPE 移動区分 AS ENUM (
    '入荷入庫',
    '出荷出庫',
    '倉庫間移動出',
    '倉庫間移動入',
    '棚卸調整増',
    '棚卸調整減',
    '返品入庫',
    '廃棄'
);

-- 棚卸ステータス
CREATE TYPE 棚卸ステータス AS ENUM ('作成中', '実施中', '確定', '取消');

-- 入出庫履歴データ
CREATE TABLE "入出庫履歴データ" (
    "ID" SERIAL PRIMARY KEY,
    "倉庫コード" VARCHAR(20) NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "移動日時" TIMESTAMP NOT NULL,
    "移動区分" 移動区分 NOT NULL,
    "移動数量" DECIMAL(15, 2) NOT NULL,
    "移動前在庫数" DECIMAL(15, 2) NOT NULL,
    "移動後在庫数" DECIMAL(15, 2) NOT NULL,
    "伝票番号" VARCHAR(20),
    "伝票種別" VARCHAR(20),
    "移動理由" TEXT,
    "ロケーションコード" VARCHAR(20),
    "ロット番号" VARCHAR(50),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    CONSTRAINT "fk_入出庫履歴_倉庫"
        FOREIGN KEY ("倉庫コード") REFERENCES "倉庫マスタ"("倉庫コード"),
    CONSTRAINT "fk_入出庫履歴_商品"
        FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード")
);

-- 棚卸データ（ヘッダ）
CREATE TABLE "棚卸データ" (
    "ID" SERIAL PRIMARY KEY,
    "棚卸番号" VARCHAR(20) UNIQUE NOT NULL,
    "倉庫コード" VARCHAR(20) NOT NULL,
    "棚卸日" DATE NOT NULL,
    "棚卸開始日時" TIMESTAMP,
    "棚卸終了日時" TIMESTAMP,
    "ステータス" 棚卸ステータス DEFAULT '作成中' NOT NULL,
    "備考" TEXT,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_棚卸_倉庫"
        FOREIGN KEY ("倉庫コード") REFERENCES "倉庫マスタ"("倉庫コード")
);

-- 棚卸明細データ
CREATE TABLE "棚卸明細データ" (
    "ID" SERIAL PRIMARY KEY,
    "棚卸ID" INTEGER NOT NULL,
    "棚卸行番号" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "ロケーションコード" VARCHAR(20),
    "ロット番号" VARCHAR(50),
    "帳簿在庫数" DECIMAL(15, 2) NOT NULL,
    "実棚数" DECIMAL(15, 2),
    "差異数" DECIMAL(15, 2),
    "差異理由" TEXT,
    "調整済フラグ" BOOLEAN DEFAULT FALSE NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_棚卸明細_棚卸"
        FOREIGN KEY ("棚卸ID") REFERENCES "棚卸データ"("ID") ON DELETE CASCADE,
    CONSTRAINT "fk_棚卸明細_商品"
        FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT "uk_棚卸明細_棚卸_行" UNIQUE ("棚卸ID", "棚卸行番号")
);

-- 差異数を自動計算するトリガー
CREATE OR REPLACE FUNCTION calculate_stocktaking_difference()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW."実棚数" IS NOT NULL THEN
        NEW."差異数" := NEW."実棚数" - NEW."帳簿在庫数";
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_棚卸明細_差異計算
    BEFORE INSERT OR UPDATE ON "棚卸明細データ"
    FOR EACH ROW
    EXECUTE FUNCTION calculate_stocktaking_difference();

-- インデックス
CREATE INDEX "idx_入出庫履歴_倉庫コード" ON "入出庫履歴データ"("倉庫コード");
CREATE INDEX "idx_入出庫履歴_商品コード" ON "入出庫履歴データ"("商品コード");
CREATE INDEX "idx_入出庫履歴_移動日時" ON "入出庫履歴データ"("移動日時");
CREATE INDEX "idx_入出庫履歴_伝票番号" ON "入出庫履歴データ"("伝票番号");
CREATE INDEX "idx_棚卸_倉庫コード" ON "棚卸データ"("倉庫コード");
CREATE INDEX "idx_棚卸_棚卸日" ON "棚卸データ"("棚卸日");
CREATE INDEX "idx_棚卸_ステータス" ON "棚卸データ"("ステータス");
CREATE INDEX "idx_棚卸明細_商品コード" ON "棚卸明細データ"("商品コード");

-- テーブルコメント
COMMENT ON TABLE "入出庫履歴データ" IS '在庫の入出庫履歴を記録するテーブル';
COMMENT ON TABLE "棚卸データ" IS '棚卸作業のヘッダ情報を管理するテーブル';
COMMENT ON TABLE "棚卸明細データ" IS '棚卸明細・差異情報を管理するテーブル';
COMMENT ON COLUMN "棚卸データ"."バージョン" IS '楽観ロック用バージョン番号';
