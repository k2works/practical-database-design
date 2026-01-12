-- 購買管理データテーブルにバージョンカラムを追加（楽観ロック対応）

-- 発注データテーブルにバージョンカラムを追加
ALTER TABLE "発注データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 発注明細データテーブルにバージョンカラムを追加
ALTER TABLE "発注明細データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 入荷受入データテーブルにバージョンカラムを追加
ALTER TABLE "入荷受入データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 受入検査データテーブルにバージョンカラムを追加
ALTER TABLE "受入検査データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 検収データテーブルにバージョンカラムを追加
ALTER TABLE "検収データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- コメント追加
COMMENT ON COLUMN "発注データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "発注明細データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "入荷受入データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "受入検査データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "検収データ"."バージョン" IS '楽観ロック用バージョン番号';
