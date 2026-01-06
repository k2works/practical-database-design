-- 財務会計システム H2 デモ用スキーマ
-- PostgreSQL ENUM は H2 では VARCHAR で代替

-- 課税取引マスタ
CREATE TABLE IF NOT EXISTS "課税取引マスタ" (
    "課税取引コード" VARCHAR(2) PRIMARY KEY,
    "課税取引名" VARCHAR(20) NOT NULL,
    "税率" DECIMAL(5,3) NOT NULL DEFAULT 0.10,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(12)
);

-- 勘定科目マスタ
CREATE TABLE IF NOT EXISTS "勘定科目マスタ" (
    "勘定科目コード" VARCHAR(5) PRIMARY KEY,
    "勘定科目名" VARCHAR(40) NOT NULL,
    "勘定科目略名" VARCHAR(10),
    "勘定科目カナ" VARCHAR(40),
    "BSPL区分" VARCHAR(2) NOT NULL CHECK ("BSPL区分" IN ('BS', 'PL')),
    "貸借区分" VARCHAR(2) NOT NULL CHECK ("貸借区分" IN ('借方', '貸方')),
    "取引要素区分" VARCHAR(2) NOT NULL CHECK ("取引要素区分" IN ('資産', '負債', '資本', '収益', '費用')),
    "集計区分" VARCHAR(4) NOT NULL CHECK ("集計区分" IN ('見出科目', '集計科目', '計上科目')),
    "管理会計区分" VARCHAR(1),
    "費用区分" VARCHAR(1),
    "元帳出力区分" VARCHAR(1),
    "補助科目種別" VARCHAR(1),
    "消費税計算区分" VARCHAR(3) CHECK ("消費税計算区分" IN ('課税', '非課税', '免税', '対象外')),
    "課税取引コード" VARCHAR(2) REFERENCES "課税取引マスタ"("課税取引コード"),
    "期日管理区分" VARCHAR(1),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(12)
);

-- 勘定科目構成マスタ（チルダ連結方式）
CREATE TABLE IF NOT EXISTS "勘定科目構成マスタ" (
    "勘定科目コード" VARCHAR(5) PRIMARY KEY,
    "勘定科目パス" VARCHAR(100) NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(12),
    FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード")
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
    "勘定科目コード" VARCHAR(5) NOT NULL,
    "部門コード" VARCHAR(10),
    "金額" DECIMAL(15, 0) NOT NULL,
    "消費税額" DECIMAL(15, 0) DEFAULT 0,
    PRIMARY KEY ("仕訳番号", "明細行番号", "貸借行番号"),
    FOREIGN KEY ("仕訳番号", "明細行番号") REFERENCES "仕訳明細データ"("仕訳番号", "明細行番号"),
    FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード"),
    FOREIGN KEY ("部門コード") REFERENCES "部門マスタ"("部門コード")
);

-- 月次勘定科目残高
CREATE TABLE IF NOT EXISTS "月次勘定科目残高" (
    "勘定科目コード" VARCHAR(5) NOT NULL,
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
