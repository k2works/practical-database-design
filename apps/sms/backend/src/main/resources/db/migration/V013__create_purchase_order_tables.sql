-- 発注ステータス
CREATE TYPE 発注ステータス AS ENUM ('作成中', '確定', '一部入荷', '入荷完了', '取消');

-- 倉庫マスタ
CREATE TABLE "倉庫マスタ" (
    "倉庫コード" VARCHAR(20) PRIMARY KEY,
    "倉庫名" VARCHAR(100) NOT NULL,
    "倉庫名カナ" VARCHAR(200),
    "郵便番号" VARCHAR(10),
    "住所" VARCHAR(200),
    "電話番号" VARCHAR(20),
    "有効フラグ" BOOLEAN DEFAULT TRUE,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者名" VARCHAR(50)
);

-- 発注データ
CREATE TABLE "発注データ" (
    "ID" SERIAL PRIMARY KEY,
    "発注番号" VARCHAR(20) UNIQUE NOT NULL,
    "仕入先コード" VARCHAR(20) NOT NULL,
    "仕入先枝番" VARCHAR(10) DEFAULT '00',
    "発注日" DATE NOT NULL,
    "希望納期" DATE,
    "発注ステータス" 発注ステータス DEFAULT '作成中' NOT NULL,
    "発注担当者コード" VARCHAR(20),
    "発注合計金額" DECIMAL(15, 2) DEFAULT 0,
    "税額" DECIMAL(15, 2) DEFAULT 0,
    "備考" TEXT,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_発注_仕入先"
        FOREIGN KEY ("仕入先コード", "仕入先枝番") REFERENCES "仕入先マスタ"("仕入先コード", "仕入先枝番")
);

-- 発注明細データ
CREATE TABLE "発注明細データ" (
    "ID" SERIAL PRIMARY KEY,
    "発注ID" INTEGER NOT NULL,
    "発注行番号" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "発注数量" DECIMAL(15, 2) NOT NULL,
    "発注単価" DECIMAL(15, 2) NOT NULL,
    "発注金額" DECIMAL(15, 2) NOT NULL,
    "入荷予定日" DATE,
    "入荷済数量" DECIMAL(15, 2) DEFAULT 0,
    "残数量" DECIMAL(15, 2),
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_発注明細_発注"
        FOREIGN KEY ("発注ID") REFERENCES "発注データ"("ID") ON DELETE CASCADE,
    CONSTRAINT "fk_発注明細_商品"
        FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT "uk_発注明細_発注_行" UNIQUE ("発注ID", "発注行番号")
);

-- 残数量の自動計算トリガー
CREATE OR REPLACE FUNCTION update_purchase_order_remaining()
RETURNS TRIGGER AS $$
BEGIN
    NEW."残数量" := NEW."発注数量" - COALESCE(NEW."入荷済数量", 0);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_発注明細_残数量更新
    BEFORE INSERT OR UPDATE ON "発注明細データ"
    FOR EACH ROW
    EXECUTE FUNCTION update_purchase_order_remaining();

-- インデックス
CREATE INDEX "idx_発注データ_仕入先コード" ON "発注データ"("仕入先コード");
CREATE INDEX "idx_発注データ_発注日" ON "発注データ"("発注日");
CREATE INDEX "idx_発注データ_ステータス" ON "発注データ"("発注ステータス");
CREATE INDEX "idx_発注明細_商品コード" ON "発注明細データ"("商品コード");

-- テーブルコメント
COMMENT ON TABLE "倉庫マスタ" IS '倉庫情報を管理するテーブル';
COMMENT ON TABLE "発注データ" IS '発注ヘッダ情報を管理するテーブル';
COMMENT ON TABLE "発注明細データ" IS '発注明細情報を管理するテーブル';
COMMENT ON COLUMN "発注データ"."バージョン" IS '楽観ロック用バージョン番号';
