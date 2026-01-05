-- 伝票区分
CREATE TYPE 伝票区分 AS ENUM ('通常', '赤伝', '黒伝');

-- 赤黒処理履歴データ
CREATE TABLE "赤黒処理履歴データ" (
    "ID" SERIAL PRIMARY KEY,
    "処理番号" VARCHAR(20) UNIQUE NOT NULL,
    "処理日時" TIMESTAMP NOT NULL,
    "伝票種別" VARCHAR(20) NOT NULL,
    "元伝票番号" VARCHAR(20) NOT NULL,
    "赤伝票番号" VARCHAR(20) NOT NULL,
    "黒伝票番号" VARCHAR(20),
    "処理理由" TEXT NOT NULL,
    "処理者" VARCHAR(50) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 売上データに伝票区分を追加
ALTER TABLE "売上データ" ADD COLUMN IF NOT EXISTS "伝票区分" 伝票区分 DEFAULT '通常' NOT NULL;
ALTER TABLE "売上データ" ADD COLUMN IF NOT EXISTS "元伝票番号" VARCHAR(20);

-- インデックス
CREATE INDEX "idx_赤黒処理履歴_元伝票番号" ON "赤黒処理履歴データ"("元伝票番号");
CREATE INDEX "idx_赤黒処理履歴_処理日時" ON "赤黒処理履歴データ"("処理日時");

-- テーブルコメント
COMMENT ON TABLE "赤黒処理履歴データ" IS '確定済み伝票の訂正履歴を管理するテーブル';
COMMENT ON COLUMN "赤黒処理履歴データ"."伝票種別" IS '対象伝票の種類（売上、仕入、請求、支払）';
