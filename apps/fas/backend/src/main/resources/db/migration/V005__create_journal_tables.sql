-- 仕訳テーブル関連

-- 仕訳伝票区分（既存の仕訳区分と同等だが明確化のため別定義）
CREATE TYPE "仕訳伝票区分" AS ENUM ('通常', '決算', '自動', '振替');

-- 仕訳行貸借区分（貸借区分を仕訳明細用にも使用）
CREATE TYPE "仕訳行貸借区分" AS ENUM ('借方', '貸方');

-- 消費税区分（仕訳用）
CREATE TYPE "消費税区分" AS ENUM ('課税', '非課税', '免税', '不課税', '対象外');

-- 消費税計算区分
CREATE TYPE "仕訳消費税計算区分" AS ENUM ('外税', '内税', '税なし');

-- 仕訳ヘッダ
CREATE TABLE "仕訳" (
    "仕訳伝票番号" VARCHAR(10) PRIMARY KEY,
    "起票日" DATE NOT NULL,
    "入力日" DATE NOT NULL DEFAULT CURRENT_DATE,
    "決算仕訳フラグ" SMALLINT DEFAULT 0 NOT NULL,
    "単振フラグ" SMALLINT DEFAULT 0 NOT NULL,
    "仕訳伝票区分" "仕訳伝票区分" DEFAULT '通常' NOT NULL,
    "定期計上フラグ" SMALLINT DEFAULT 0 NOT NULL,
    "社員コード" VARCHAR(10),
    "部門コード" VARCHAR(5),
    "赤伝フラグ" SMALLINT DEFAULT 0 NOT NULL,
    "赤黒伝票番号" INTEGER,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者名" VARCHAR(12)
);

-- 仕訳明細
CREATE TABLE "仕訳明細" (
    "仕訳伝票番号" VARCHAR(10) NOT NULL,
    "仕訳行番号" SMALLINT NOT NULL,
    "行摘要" VARCHAR(1000),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY ("仕訳伝票番号", "仕訳行番号"),
    CONSTRAINT "fk_仕訳明細_仕訳"
        FOREIGN KEY ("仕訳伝票番号") REFERENCES "仕訳"("仕訳伝票番号") ON DELETE CASCADE
);

-- 仕訳貸借明細
CREATE TABLE "仕訳貸借明細" (
    "仕訳伝票番号" VARCHAR(10) NOT NULL,
    "仕訳行番号" SMALLINT NOT NULL,
    "仕訳行貸借区分" "仕訳行貸借区分" NOT NULL,
    "勘定科目コード" VARCHAR(5) NOT NULL,
    "補助科目コード" VARCHAR(10),
    "部門コード" VARCHAR(5),
    "プロジェクトコード" VARCHAR(10),
    "仕訳金額" DECIMAL(14,2) NOT NULL,
    "通貨コード" VARCHAR(3) DEFAULT 'JPY',
    "為替レート" DECIMAL(8,2) DEFAULT 1.00,
    "基軸換算仕訳金額" DECIMAL(14,2),
    "消費税区分" "消費税区分" DEFAULT '課税',
    "消費税率" SMALLINT DEFAULT 10,
    "消費税計算区分" "仕訳消費税計算区分" DEFAULT '外税',
    "期日" DATE,
    "資金繰フラグ" SMALLINT DEFAULT 0,
    "セグメントコード" VARCHAR(10),
    "相手勘定科目コード" VARCHAR(5),
    "相手補助科目コード" VARCHAR(10),
    "付箋コード" VARCHAR(1),
    "付箋内容" VARCHAR(60),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY ("仕訳伝票番号", "仕訳行番号", "仕訳行貸借区分"),
    CONSTRAINT "fk_仕訳貸借明細_仕訳明細"
        FOREIGN KEY ("仕訳伝票番号", "仕訳行番号")
        REFERENCES "仕訳明細"("仕訳伝票番号", "仕訳行番号") ON DELETE CASCADE,
    CONSTRAINT "fk_仕訳貸借明細_勘定科目"
        FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード")
);

-- インデックス
CREATE INDEX "idx_仕訳_起票日" ON "仕訳"("起票日");
CREATE INDEX "idx_仕訳_部門コード" ON "仕訳"("部門コード");
CREATE INDEX "idx_仕訳_仕訳伝票区分" ON "仕訳"("仕訳伝票区分");
CREATE INDEX "idx_仕訳貸借明細_勘定科目コード" ON "仕訳貸借明細"("勘定科目コード");
CREATE INDEX "idx_仕訳貸借明細_部門コード" ON "仕訳貸借明細"("部門コード");
