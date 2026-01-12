-- 外注委託管理テーブルにバージョンカラムを追加

-- 支給データテーブルにバージョンカラムを追加
ALTER TABLE "支給データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 支給明細データテーブルにバージョンカラムと消費管理カラムを追加
ALTER TABLE "支給明細データ" ADD COLUMN "消費済数量" NUMERIC(18,4) DEFAULT 0;
ALTER TABLE "支給明細データ" ADD COLUMN "残数量" NUMERIC(18,4);
ALTER TABLE "支給明細データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 残数量の初期値を支給数と同じに設定
UPDATE "支給明細データ" SET "残数量" = "支給数" WHERE "残数量" IS NULL;

-- 消費データテーブルにバージョンカラムを追加
ALTER TABLE "消費データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 消費明細データテーブルにバージョンカラムを追加
ALTER TABLE "消費明細データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- コメント追加
COMMENT ON COLUMN "支給データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "支給明細データ"."消費済数量" IS '消費済みの数量';
COMMENT ON COLUMN "支給明細データ"."残数量" IS '支給数 - 消費済数量';
COMMENT ON COLUMN "支給明細データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "消費データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "消費明細データ"."バージョン" IS '楽観ロック用バージョン番号';
