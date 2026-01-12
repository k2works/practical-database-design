-- V7__add_inventory_version_columns.sql
-- 在庫管理テーブルに楽観ロック用バージョンカラムを追加

-- 在庫情報テーブルにバージョンカラムを追加
ALTER TABLE "在庫情報" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 棚卸データテーブルにバージョンカラムを追加
ALTER TABLE "棚卸データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- コメント追加
COMMENT ON COLUMN "在庫情報"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "棚卸データ"."バージョン" IS '楽観ロック用バージョン番号';
