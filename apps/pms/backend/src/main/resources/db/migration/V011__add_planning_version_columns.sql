-- 生産計画データテーブルにバージョンカラムを追加（楽観ロック対応）

-- 基準生産計画テーブルにバージョンカラムを追加
ALTER TABLE "基準生産計画" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- オーダ情報テーブルにバージョンカラムを追加
ALTER TABLE "オーダ情報" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 所要情報テーブルにバージョンカラムを追加
ALTER TABLE "所要情報" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- 引当情報テーブルにバージョンカラムを追加
ALTER TABLE "引当情報" ADD COLUMN "バージョン" INTEGER DEFAULT 1 NOT NULL;

-- コメント追加
COMMENT ON COLUMN "基準生産計画"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "オーダ情報"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "所要情報"."バージョン" IS '楽観ロック用バージョン番号';
COMMENT ON COLUMN "引当情報"."バージョン" IS '楽観ロック用バージョン番号';
