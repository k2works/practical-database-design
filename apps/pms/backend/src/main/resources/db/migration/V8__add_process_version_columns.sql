-- V8__add_process_version_columns.sql
-- 工程管理テーブルに楽観ロック用バージョンカラムを追加

-- 作業指示データテーブルにバージョンカラムを追加
ALTER TABLE "作業指示データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 完成実績データテーブルにバージョンカラムを追加
ALTER TABLE "完成実績データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 工数実績データテーブルにバージョンカラムを追加
ALTER TABLE "工数実績データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- コメント追加
COMMENT ON COLUMN "作業指示データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "完成実績データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "工数実績データ"."バージョン" IS '楽観ロック用バージョン番号';
