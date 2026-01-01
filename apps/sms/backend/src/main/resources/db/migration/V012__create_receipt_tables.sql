-- 入金ステータス
CREATE TYPE 入金ステータス AS ENUM ('入金済', '一部消込', '消込済', '過入金');

-- 入金方法（取引先の支払方法とは別）
CREATE TYPE 入金方法 AS ENUM ('現金', '銀行振込', 'クレジットカード', '手形', '電子記録債権');

-- 入金データ
CREATE TABLE "入金データ" (
    "ID" SERIAL PRIMARY KEY,
    "入金番号" VARCHAR(20) UNIQUE NOT NULL,
    "入金日" DATE NOT NULL,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "入金方法" 入金方法 NOT NULL,
    "入金金額" DECIMAL(15, 2) NOT NULL,
    "消込済金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "未消込金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "手数料" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "振込名義" VARCHAR(100),
    "銀行名" VARCHAR(50),
    "口座番号" VARCHAR(20),
    "ステータス" 入金ステータス DEFAULT '入金済' NOT NULL,
    "備考" TEXT,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_入金データ_顧客"
        FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番")
);

-- 入金消込明細
CREATE TABLE "入金消込明細" (
    "ID" SERIAL PRIMARY KEY,
    "入金ID" INTEGER NOT NULL,
    "行番号" INTEGER NOT NULL,
    "請求ID" INTEGER,
    "消込日" DATE NOT NULL,
    "消込金額" DECIMAL(15, 2) NOT NULL,
    "備考" TEXT,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_入金消込明細_入金"
        FOREIGN KEY ("入金ID") REFERENCES "入金データ"("ID") ON DELETE CASCADE,
    CONSTRAINT "fk_入金消込明細_請求"
        FOREIGN KEY ("請求ID") REFERENCES "請求データ"("ID"),
    CONSTRAINT "uk_入金消込明細_入金_行" UNIQUE ("入金ID", "行番号")
);

-- 前受金データ
CREATE TABLE "前受金データ" (
    "ID" SERIAL PRIMARY KEY,
    "前受金番号" VARCHAR(20) UNIQUE NOT NULL,
    "発生日" DATE NOT NULL,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "入金ID" INTEGER,
    "前受金額" DECIMAL(15, 2) NOT NULL,
    "使用済金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "残高" DECIMAL(15, 2) NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_前受金データ_顧客"
        FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番"),
    CONSTRAINT "fk_前受金データ_入金"
        FOREIGN KEY ("入金ID") REFERENCES "入金データ"("ID")
);

-- インデックス
CREATE INDEX "idx_入金データ_顧客コード" ON "入金データ"("顧客コード");
CREATE INDEX "idx_入金データ_入金日" ON "入金データ"("入金日");
CREATE INDEX "idx_入金データ_ステータス" ON "入金データ"("ステータス");
CREATE INDEX "idx_入金消込明細_入金ID" ON "入金消込明細"("入金ID");
CREATE INDEX "idx_入金消込明細_請求ID" ON "入金消込明細"("請求ID");
CREATE INDEX "idx_前受金データ_顧客コード" ON "前受金データ"("顧客コード");

-- テーブルコメント
COMMENT ON TABLE "入金データ" IS '入金情報を管理するテーブル';
COMMENT ON TABLE "入金消込明細" IS '入金と請求の消込明細を管理するテーブル';
COMMENT ON TABLE "前受金データ" IS '過入金による前受金を管理するテーブル';

-- カラムコメント
COMMENT ON COLUMN "入金データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "入金消込明細"."バージョン" IS '楽観ロック用バージョン番号';
