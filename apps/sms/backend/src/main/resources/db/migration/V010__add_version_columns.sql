-- 楽観ロック用バージョンカラムを追加

-- 受注データテーブルにバージョンカラムを追加
ALTER TABLE "受注データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 受注明細テーブルにバージョンカラムを追加
ALTER TABLE "受注明細" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 出荷データテーブルにバージョンカラムを追加
ALTER TABLE "出荷データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 出荷明細テーブルにバージョンカラムを追加
ALTER TABLE "出荷明細" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 売上データテーブルにバージョンカラムを追加
ALTER TABLE "売上データ" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 売上明細テーブルにバージョンカラムを追加
ALTER TABLE "売上明細" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- コメント追加
COMMENT ON COLUMN "受注データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "受注明細"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "出荷データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "出荷明細"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "売上データ"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "売上明細"."バージョン" IS '楽観ロック用バージョン番号';
