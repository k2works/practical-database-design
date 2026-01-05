-- 部門マスタ（日本語テーブル名・カラム名）
CREATE TABLE "部門マスタ" (
    "部門コード" VARCHAR(10) NOT NULL,
    "開始日" DATE NOT NULL,
    "終了日" DATE,
    "部門名" VARCHAR(40) NOT NULL,
    "組織階層" INTEGER NOT NULL DEFAULT 0,
    "部門パス" VARCHAR(100),
    "最下層区分" BOOLEAN NOT NULL DEFAULT FALSE,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(50),
    PRIMARY KEY ("部門コード", "開始日")
);

-- インデックス
CREATE INDEX idx_部門マスタ_部門パス ON "部門マスタ"("部門パス");
CREATE INDEX idx_部門マスタ_組織階層 ON "部門マスタ"("組織階層");
