-- 仕訳テーブルにバージョンカラムを追加（楽観ロック用）

-- 仕訳テーブルにバージョンカラムを追加
ALTER TABLE "仕訳" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 仕訳明細テーブルにバージョンカラムを追加
ALTER TABLE "仕訳明細" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 仕訳貸借明細テーブルにバージョンカラムを追加
ALTER TABLE "仕訳貸借明細" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- コメント追加
COMMENT ON COLUMN "仕訳"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "仕訳明細"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "仕訳貸借明細"."バージョン" IS '楽観ロック用バージョン番号';
