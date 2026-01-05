-- 財務会計システム H2 デモ用スキーマ
-- PostgreSQL ENUM は H2 では VARCHAR で代替

-- 勘定科目マスタ
CREATE TABLE IF NOT EXISTS "勘定科目マスタ" (
    "勘定科目コード" VARCHAR(10) PRIMARY KEY,
    "勘定科目名" VARCHAR(100) NOT NULL,
    "BSPL区分" VARCHAR(2) NOT NULL CHECK ("BSPL区分" IN ('BS', 'PL')),
    "貸借区分" VARCHAR(2) NOT NULL CHECK ("貸借区分" IN ('借方', '貸方')),
    "集計区分" VARCHAR(2) NOT NULL CHECK ("集計区分" IN ('集計', '明細')),
    "課税区分" VARCHAR(4) CHECK ("課税区分" IN ('課税', '非課税', '免税', '対象外')),
    "表示順" INTEGER,
    "有効フラグ" BOOLEAN DEFAULT TRUE,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 勘定科目構成マスタ（階層構造）
CREATE TABLE IF NOT EXISTS "勘定科目構成マスタ" (
    "勘定科目コード" VARCHAR(10) NOT NULL,
    "親勘定科目コード" VARCHAR(10),
    "階層レベル" INTEGER NOT NULL,
    "勘定科目パス" VARCHAR(200),
    PRIMARY KEY ("勘定科目コード"),
    FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード"),
    FOREIGN KEY ("親勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード")
);

-- 部門マスタ
CREATE TABLE IF NOT EXISTS "部門マスタ" (
    "部門コード" VARCHAR(10) PRIMARY KEY,
    "部門名" VARCHAR(100) NOT NULL,
    "親部門コード" VARCHAR(10),
    "有効フラグ" BOOLEAN DEFAULT TRUE,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("親部門コード") REFERENCES "部門マスタ"("部門コード")
);

-- 仕訳データ
CREATE TABLE IF NOT EXISTS "仕訳データ" (
    "仕訳番号" VARCHAR(20) PRIMARY KEY,
    "仕訳日付" DATE NOT NULL,
    "仕訳区分" VARCHAR(2) NOT NULL CHECK ("仕訳区分" IN ('通常', '振替', '決算', '自動')),
    "仕訳ステータス" VARCHAR(3) NOT NULL CHECK ("仕訳ステータス" IN ('入力中', '確定', '承認済', '取消')),
    "摘要" VARCHAR(200),
    "作成者" VARCHAR(50),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 仕訳明細データ
CREATE TABLE IF NOT EXISTS "仕訳明細データ" (
    "仕訳番号" VARCHAR(20) NOT NULL,
    "明細行番号" INTEGER NOT NULL,
    "明細摘要" VARCHAR(200),
    PRIMARY KEY ("仕訳番号", "明細行番号"),
    FOREIGN KEY ("仕訳番号") REFERENCES "仕訳データ"("仕訳番号")
);

-- 仕訳貸借明細データ
CREATE TABLE IF NOT EXISTS "仕訳貸借明細データ" (
    "仕訳番号" VARCHAR(20) NOT NULL,
    "明細行番号" INTEGER NOT NULL,
    "貸借行番号" INTEGER NOT NULL,
    "貸借区分" VARCHAR(2) NOT NULL CHECK ("貸借区分" IN ('借方', '貸方')),
    "勘定科目コード" VARCHAR(10) NOT NULL,
    "部門コード" VARCHAR(10),
    "金額" DECIMAL(15, 0) NOT NULL,
    "消費税額" DECIMAL(15, 0) DEFAULT 0,
    PRIMARY KEY ("仕訳番号", "明細行番号", "貸借行番号"),
    FOREIGN KEY ("仕訳番号", "明細行番号") REFERENCES "仕訳明細データ"("仕訳番号", "明細行番号"),
    FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード"),
    FOREIGN KEY ("部門コード") REFERENCES "部門マスタ"("部門コード")
);

-- 日次勘定科目残高
CREATE TABLE IF NOT EXISTS "日次勘定科目残高" (
    "勘定科目コード" VARCHAR(10) NOT NULL,
    "部門コード" VARCHAR(10),
    "残高日付" DATE NOT NULL,
    "借方金額" DECIMAL(15, 0) DEFAULT 0,
    "貸方金額" DECIMAL(15, 0) DEFAULT 0,
    "残高金額" DECIMAL(15, 0) DEFAULT 0,
    PRIMARY KEY ("勘定科目コード", "残高日付"),
    FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード")
);

-- 月次勘定科目残高
CREATE TABLE IF NOT EXISTS "月次勘定科目残高" (
    "勘定科目コード" VARCHAR(10) NOT NULL,
    "部門コード" VARCHAR(10),
    "会計年度" INTEGER NOT NULL,
    "会計月" INTEGER NOT NULL,
    "期首残高" DECIMAL(15, 0) DEFAULT 0,
    "借方合計" DECIMAL(15, 0) DEFAULT 0,
    "貸方合計" DECIMAL(15, 0) DEFAULT 0,
    "期末残高" DECIMAL(15, 0) DEFAULT 0,
    PRIMARY KEY ("勘定科目コード", "会計年度", "会計月"),
    FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード")
);
