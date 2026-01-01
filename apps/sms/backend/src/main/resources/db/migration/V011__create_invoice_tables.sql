-- 請求ステータス
CREATE TYPE 請求ステータス AS ENUM ('未発行', '発行済', '一部入金', '入金済', '回収遅延');

-- 請求区分は V001 で既に定義済み（'都度', '締め'）

-- 請求データ（ヘッダ）
CREATE TABLE "請求データ" (
    "ID" SERIAL PRIMARY KEY,
    "請求番号" VARCHAR(20) UNIQUE NOT NULL,
    "請求日" DATE NOT NULL,
    "請求先コード" VARCHAR(20) NOT NULL,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "締日" DATE,
    "請求区分" 請求区分 NOT NULL,
    "前回請求残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "入金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "繰越残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "今回売上額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "今回消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "今回請求額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "請求残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "回収予定日" DATE,
    "ステータス" 請求ステータス DEFAULT '未発行' NOT NULL,
    "備考" TEXT,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT "fk_請求データ_顧客"
        FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番")
);

-- 請求明細
CREATE TABLE "請求明細" (
    "ID" SERIAL PRIMARY KEY,
    "請求ID" INTEGER NOT NULL,
    "行番号" INTEGER NOT NULL,
    "売上ID" INTEGER,
    "売上番号" VARCHAR(20),
    "売上日" DATE,
    "売上金額" DECIMAL(15, 2) NOT NULL,
    "消費税額" DECIMAL(15, 2) NOT NULL,
    "合計金額" DECIMAL(15, 2) NOT NULL,
    CONSTRAINT "fk_請求明細_請求"
        FOREIGN KEY ("請求ID") REFERENCES "請求データ"("ID") ON DELETE CASCADE,
    CONSTRAINT "fk_請求明細_売上"
        FOREIGN KEY ("売上ID") REFERENCES "売上データ"("ID"),
    CONSTRAINT "uk_請求明細_請求_行" UNIQUE ("請求ID", "行番号")
);

-- 請求締履歴
CREATE TABLE "請求締履歴" (
    "ID" SERIAL PRIMARY KEY,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "締年月" VARCHAR(7) NOT NULL,
    "締日" DATE NOT NULL,
    "売上件数" INTEGER NOT NULL,
    "売上合計" DECIMAL(15, 2) NOT NULL,
    "消費税合計" DECIMAL(15, 2) NOT NULL,
    "請求ID" INTEGER,
    "処理日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_請求締履歴_顧客"
        FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番"),
    CONSTRAINT "fk_請求締履歴_請求"
        FOREIGN KEY ("請求ID") REFERENCES "請求データ"("ID"),
    CONSTRAINT "uk_請求締履歴_顧客_年月" UNIQUE ("顧客コード", "顧客枝番", "締年月")
);

-- 売掛金残高
CREATE TABLE "売掛金残高" (
    "ID" SERIAL PRIMARY KEY,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "基準日" DATE NOT NULL,
    "前月残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "当月売上" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "当月入金" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "当月残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_売掛金残高_顧客"
        FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番"),
    CONSTRAINT "uk_売掛金残高_顧客_基準日" UNIQUE ("顧客コード", "顧客枝番", "基準日")
);

-- インデックス
CREATE INDEX "idx_請求データ_顧客コード" ON "請求データ"("顧客コード");
CREATE INDEX "idx_請求データ_請求日" ON "請求データ"("請求日");
CREATE INDEX "idx_請求データ_ステータス" ON "請求データ"("ステータス");
CREATE INDEX "idx_請求明細_請求ID" ON "請求明細"("請求ID");
CREATE INDEX "idx_売掛金残高_基準日" ON "売掛金残高"("基準日");

-- テーブルコメント
COMMENT ON TABLE "請求データ" IS '請求ヘッダ情報を管理するテーブル';
COMMENT ON TABLE "請求明細" IS '請求に含まれる売上明細を管理するテーブル';
COMMENT ON TABLE "請求締履歴" IS '月次締処理の履歴を管理するテーブル';
COMMENT ON TABLE "売掛金残高" IS '顧客別月次売掛金残高を管理するテーブル';

-- カラムコメント
COMMENT ON COLUMN "請求データ"."バージョン" IS '楽観ロック用バージョン番号';
