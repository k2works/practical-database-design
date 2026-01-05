-- 支払ステータス
CREATE TYPE 支払ステータス AS ENUM ('作成中', '承認待ち', '承認済', '支払済', '取消');

-- 支払方法に不足している値を追加（既存のENUMはV001で定義済み）
ALTER TYPE 支払方法 ADD VALUE IF NOT EXISTS '相殺';
ALTER TYPE 支払方法 ADD VALUE IF NOT EXISTS '電子記録債権';

-- 支払データ（ヘッダ）
CREATE TABLE "支払データ" (
    "ID" SERIAL PRIMARY KEY,
    "支払番号" VARCHAR(20) UNIQUE NOT NULL,
    "仕入先コード" VARCHAR(20) NOT NULL,
    "支払締日" DATE NOT NULL,
    "支払予定日" DATE NOT NULL,
    "支払方法" 支払方法 NOT NULL,
    "支払金額" DECIMAL(15, 2) NOT NULL,
    "消費税額" DECIMAL(15, 2) NOT NULL,
    "源泉徴収額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "差引支払額" DECIMAL(15, 2) NOT NULL,
    "支払実行日" DATE,
    "ステータス" 支払ステータス DEFAULT '作成中' NOT NULL,
    "振込先銀行コード" VARCHAR(10),
    "振込先支店コード" VARCHAR(10),
    "振込先口座種別" VARCHAR(10),
    "振込先口座番号" VARCHAR(20),
    "振込先口座名義" VARCHAR(100),
    "備考" TEXT,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_支払_仕入先"
        FOREIGN KEY ("仕入先コード") REFERENCES "取引先マスタ"("取引先コード")
);

-- 支払明細データ
CREATE TABLE "支払明細データ" (
    "ID" SERIAL PRIMARY KEY,
    "支払ID" INTEGER NOT NULL,
    "支払行番号" INTEGER NOT NULL,
    "仕入番号" VARCHAR(20) NOT NULL,
    "仕入日" DATE NOT NULL,
    "仕入金額" DECIMAL(15, 2) NOT NULL,
    "消費税額" DECIMAL(15, 2) NOT NULL,
    "支払対象金額" DECIMAL(15, 2) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_支払明細_支払"
        FOREIGN KEY ("支払ID") REFERENCES "支払データ"("ID") ON DELETE CASCADE,
    CONSTRAINT "fk_支払明細_仕入"
        FOREIGN KEY ("仕入番号") REFERENCES "仕入データ"("仕入番号"),
    CONSTRAINT "uk_支払明細_支払_行" UNIQUE ("支払ID", "支払行番号")
);

-- 買掛金残高データ
CREATE TABLE "買掛金残高データ" (
    "ID" SERIAL PRIMARY KEY,
    "仕入先コード" VARCHAR(20) NOT NULL,
    "年月" DATE NOT NULL,
    "前月残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "当月仕入高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "当月支払高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "当月残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_買掛金残高_仕入先"
        FOREIGN KEY ("仕入先コード") REFERENCES "取引先マスタ"("取引先コード"),
    UNIQUE ("仕入先コード", "年月")
);

-- 支払予定データ
CREATE TABLE "支払予定データ" (
    "ID" SERIAL PRIMARY KEY,
    "仕入先コード" VARCHAR(20) NOT NULL,
    "支払予定日" DATE NOT NULL,
    "支払予定額" DECIMAL(15, 2) NOT NULL,
    "支払方法" 支払方法 NOT NULL,
    "支払済フラグ" BOOLEAN DEFAULT FALSE NOT NULL,
    "支払ID" INTEGER,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_支払予定_仕入先"
        FOREIGN KEY ("仕入先コード") REFERENCES "取引先マスタ"("取引先コード"),
    CONSTRAINT "fk_支払予定_支払"
        FOREIGN KEY ("支払ID") REFERENCES "支払データ"("ID")
);

-- 当月残高を自動計算するトリガー
CREATE OR REPLACE FUNCTION calculate_payable_balance()
RETURNS TRIGGER AS $$
BEGIN
    NEW."当月残高" := NEW."前月残高" + NEW."当月仕入高" - NEW."当月支払高";
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_買掛金残高_残高計算
    BEFORE INSERT OR UPDATE ON "買掛金残高データ"
    FOR EACH ROW
    EXECUTE FUNCTION calculate_payable_balance();

-- インデックス
CREATE INDEX "idx_支払_仕入先コード" ON "支払データ"("仕入先コード");
CREATE INDEX "idx_支払_支払締日" ON "支払データ"("支払締日");
CREATE INDEX "idx_支払_支払予定日" ON "支払データ"("支払予定日");
CREATE INDEX "idx_支払_ステータス" ON "支払データ"("ステータス");
CREATE INDEX "idx_支払明細_仕入番号" ON "支払明細データ"("仕入番号");
CREATE INDEX "idx_買掛金残高_年月" ON "買掛金残高データ"("年月");
CREATE INDEX "idx_支払予定_支払予定日" ON "支払予定データ"("支払予定日");
CREATE INDEX "idx_支払予定_支払済" ON "支払予定データ"("支払済フラグ");

-- テーブルコメント
COMMENT ON TABLE "支払データ" IS '支払締処理で作成される支払ヘッダ情報';
COMMENT ON TABLE "支払明細データ" IS '支払データと仕入データの紐付け';
COMMENT ON TABLE "買掛金残高データ" IS '仕入先別・月次の買掛金残高';
COMMENT ON TABLE "支払予定データ" IS '支払スケジュールの管理';
COMMENT ON COLUMN "支払データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "買掛金残高データ"."バージョン" IS '楽観ロック用バージョン番号';
