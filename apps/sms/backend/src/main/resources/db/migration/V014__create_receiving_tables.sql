-- 入荷ステータス
CREATE TYPE 入荷ステータス AS ENUM ('入荷待ち', '検品中', '検品完了', '仕入計上済', '返品');

-- 入荷データ
CREATE TABLE "入荷データ" (
    "ID" SERIAL PRIMARY KEY,
    "入荷番号" VARCHAR(20) UNIQUE NOT NULL,
    "発注ID" INTEGER NOT NULL,
    "仕入先コード" VARCHAR(20) NOT NULL,
    "仕入先枝番" VARCHAR(10) DEFAULT '00',
    "入荷日" DATE NOT NULL,
    "入荷ステータス" 入荷ステータス DEFAULT '入荷待ち' NOT NULL,
    "入荷担当者コード" VARCHAR(20),
    "倉庫コード" VARCHAR(20) NOT NULL,
    "備考" TEXT,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_入荷_発注"
        FOREIGN KEY ("発注ID") REFERENCES "発注データ"("ID"),
    CONSTRAINT "fk_入荷_仕入先"
        FOREIGN KEY ("仕入先コード", "仕入先枝番") REFERENCES "仕入先マスタ"("仕入先コード", "仕入先枝番"),
    CONSTRAINT "fk_入荷_倉庫"
        FOREIGN KEY ("倉庫コード") REFERENCES "倉庫マスタ"("倉庫コード")
);

-- 入荷明細データ
CREATE TABLE "入荷明細データ" (
    "ID" SERIAL PRIMARY KEY,
    "入荷ID" INTEGER NOT NULL,
    "入荷行番号" INTEGER NOT NULL,
    "発注明細ID" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "入荷数量" DECIMAL(15, 2) NOT NULL,
    "検品数量" DECIMAL(15, 2) DEFAULT 0,
    "合格数量" DECIMAL(15, 2) DEFAULT 0,
    "不合格数量" DECIMAL(15, 2) DEFAULT 0,
    "入荷単価" DECIMAL(15, 2) NOT NULL,
    "入荷金額" DECIMAL(15, 2) NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_入荷明細_入荷"
        FOREIGN KEY ("入荷ID") REFERENCES "入荷データ"("ID") ON DELETE CASCADE,
    CONSTRAINT "fk_入荷明細_発注明細"
        FOREIGN KEY ("発注明細ID") REFERENCES "発注明細データ"("ID"),
    CONSTRAINT "fk_入荷明細_商品"
        FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT "uk_入荷明細_入荷_行" UNIQUE ("入荷ID", "入荷行番号")
);

-- 仕入データ（検収確定後に作成）
CREATE TABLE "仕入データ" (
    "ID" SERIAL PRIMARY KEY,
    "仕入番号" VARCHAR(20) UNIQUE NOT NULL,
    "入荷ID" INTEGER NOT NULL,
    "仕入先コード" VARCHAR(20) NOT NULL,
    "仕入先枝番" VARCHAR(10) DEFAULT '00',
    "仕入日" DATE NOT NULL,
    "仕入合計金額" DECIMAL(15, 2) NOT NULL,
    "税額" DECIMAL(15, 2) NOT NULL,
    "備考" TEXT,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_仕入_入荷"
        FOREIGN KEY ("入荷ID") REFERENCES "入荷データ"("ID"),
    CONSTRAINT "fk_仕入_仕入先"
        FOREIGN KEY ("仕入先コード", "仕入先枝番") REFERENCES "仕入先マスタ"("仕入先コード", "仕入先枝番")
);

-- 仕入明細データ
CREATE TABLE "仕入明細データ" (
    "ID" SERIAL PRIMARY KEY,
    "仕入ID" INTEGER NOT NULL,
    "仕入行番号" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "仕入数量" DECIMAL(15, 2) NOT NULL,
    "仕入単価" DECIMAL(15, 2) NOT NULL,
    "仕入金額" DECIMAL(15, 2) NOT NULL,
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_仕入明細_仕入"
        FOREIGN KEY ("仕入ID") REFERENCES "仕入データ"("ID") ON DELETE CASCADE,
    CONSTRAINT "fk_仕入明細_商品"
        FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT "uk_仕入明細_仕入_行" UNIQUE ("仕入ID", "仕入行番号")
);

-- インデックス
CREATE INDEX "idx_入荷データ_発注ID" ON "入荷データ"("発注ID");
CREATE INDEX "idx_入荷データ_入荷日" ON "入荷データ"("入荷日");
CREATE INDEX "idx_入荷データ_ステータス" ON "入荷データ"("入荷ステータス");
CREATE INDEX "idx_入荷明細_発注明細ID" ON "入荷明細データ"("発注明細ID");
CREATE INDEX "idx_仕入データ_仕入先コード" ON "仕入データ"("仕入先コード");
CREATE INDEX "idx_仕入データ_仕入日" ON "仕入データ"("仕入日");

-- テーブルコメント
COMMENT ON TABLE "入荷データ" IS '入荷ヘッダ情報を管理するテーブル';
COMMENT ON TABLE "入荷明細データ" IS '入荷明細・検品情報を管理するテーブル';
COMMENT ON TABLE "仕入データ" IS '仕入ヘッダ情報を管理するテーブル';
COMMENT ON TABLE "仕入明細データ" IS '仕入明細情報を管理するテーブル';
COMMENT ON COLUMN "入荷データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "仕入データ"."バージョン" IS '楽観ロック用バージョン番号';
