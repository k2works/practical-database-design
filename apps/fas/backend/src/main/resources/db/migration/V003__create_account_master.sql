-- 財務会計システム 勘定科目マスタ・課税取引マスタ作成
-- chapter15: 勘定科目の設計

-- 課税取引マスタ
CREATE TABLE "課税取引マスタ" (
    "課税取引コード" VARCHAR(2) PRIMARY KEY,
    "課税取引名" VARCHAR(20) NOT NULL,
    "税率" DECIMAL(5,3) NOT NULL DEFAULT 0.10,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(12)
);

-- 勘定科目マスタ
CREATE TABLE "勘定科目マスタ" (
    "勘定科目コード" VARCHAR(5) PRIMARY KEY,
    "勘定科目名" VARCHAR(40) NOT NULL,
    "勘定科目略名" VARCHAR(10),
    "勘定科目カナ" VARCHAR(40),
    "BSPL区分" "BSPL区分" NOT NULL,
    "貸借区分" "貸借区分" NOT NULL,
    "取引要素区分" "取引要素区分" NOT NULL,
    "集計区分" "集計区分" NOT NULL,
    "管理会計区分" VARCHAR(1),
    "費用区分" VARCHAR(1),
    "元帳出力区分" VARCHAR(1),
    "補助科目種別" VARCHAR(1),
    "消費税計算区分" "消費税計算区分",
    "課税取引コード" VARCHAR(2) REFERENCES "課税取引マスタ"("課税取引コード"),
    "期日管理区分" VARCHAR(1),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(12)
);

-- インデックス
CREATE INDEX idx_勘定科目マスタ_BSPL区分 ON "勘定科目マスタ"("BSPL区分");
CREATE INDEX idx_勘定科目マスタ_取引要素区分 ON "勘定科目マスタ"("取引要素区分");
CREATE INDEX idx_勘定科目マスタ_集計区分 ON "勘定科目マスタ"("集計区分");

-- 課税取引マスタ初期データ
INSERT INTO "課税取引マスタ" ("課税取引コード", "課税取引名", "税率") VALUES
('00', '非課税', 0.000),
('08', '軽減税率', 0.080),
('10', '標準税率', 0.100);
