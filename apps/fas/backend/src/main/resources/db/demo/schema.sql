-- =============================================================================
-- 財務会計システム H2 デモ用スキーマ
-- PostgreSQL ENUM は H2 では VARCHAR + CHECK で代替
-- V001-V011 マイグレーションファイルと同期
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 課税取引マスタ (V003)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "課税取引マスタ" (
    "課税取引コード" VARCHAR(2) PRIMARY KEY,
    "課税取引名" VARCHAR(20) NOT NULL,
    "税率" DECIMAL(5,3) NOT NULL DEFAULT 0.10,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(12)
);

-- -----------------------------------------------------------------------------
-- 勘定科目マスタ (V003)
-- -----------------------------------------------------------------------------
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

CREATE INDEX IF NOT EXISTS idx_勘定科目マスタ_BSPL区分 ON "勘定科目マスタ"("BSPL区分");
CREATE INDEX IF NOT EXISTS idx_勘定科目マスタ_取引要素区分 ON "勘定科目マスタ"("取引要素区分");
CREATE INDEX IF NOT EXISTS idx_勘定科目マスタ_集計区分 ON "勘定科目マスタ"("集計区分");

-- -----------------------------------------------------------------------------
-- 勘定科目構成マスタ (V004)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "勘定科目構成マスタ" (
    "勘定科目コード" VARCHAR(5) PRIMARY KEY,
    "勘定科目パス" VARCHAR(100) NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(12),
    FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード")
);

CREATE INDEX IF NOT EXISTS idx_勘定科目構成マスタ_パス ON "勘定科目構成マスタ"("勘定科目パス");

-- -----------------------------------------------------------------------------
-- 部門マスタ (V011)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "部門マスタ" (
    "部門コード" VARCHAR(5) PRIMARY KEY,
    "部門名" VARCHAR(100) NOT NULL,
    "部門略名" VARCHAR(20),
    "組織階層" INTEGER NOT NULL CHECK ("組織階層" >= 0 AND "組織階層" <= 3),
    "部門パス" VARCHAR(100) NOT NULL,
    "最下層フラグ" INTEGER NOT NULL DEFAULT 0 CHECK ("最下層フラグ" IN (0, 1)),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_department_organization_level ON "部門マスタ" ("組織階層");
CREATE INDEX IF NOT EXISTS idx_department_path ON "部門マスタ" ("部門パス");
CREATE INDEX IF NOT EXISTS idx_department_lowest_level ON "部門マスタ" ("最下層フラグ");

-- -----------------------------------------------------------------------------
-- 仕訳ヘッダ (V005)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "仕訳" (
    "仕訳伝票番号" VARCHAR(10) PRIMARY KEY,
    "起票日" DATE NOT NULL,
    "入力日" DATE NOT NULL DEFAULT CURRENT_DATE,
    "決算仕訳フラグ" SMALLINT DEFAULT 0 NOT NULL,
    "単振フラグ" SMALLINT DEFAULT 0 NOT NULL,
    "仕訳伝票区分" VARCHAR(4) DEFAULT '通常' NOT NULL CHECK ("仕訳伝票区分" IN ('通常', '決算', '自動', '振替')),
    "定期計上フラグ" SMALLINT DEFAULT 0 NOT NULL,
    "社員コード" VARCHAR(10),
    "部門コード" VARCHAR(5),
    "赤伝フラグ" SMALLINT DEFAULT 0 NOT NULL,
    "赤黒伝票番号" INTEGER,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者名" VARCHAR(12),
    "バージョン" INTEGER DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_仕訳_起票日 ON "仕訳"("起票日");
CREATE INDEX IF NOT EXISTS idx_仕訳_部門コード ON "仕訳"("部門コード");
CREATE INDEX IF NOT EXISTS idx_仕訳_仕訳伝票区分 ON "仕訳"("仕訳伝票区分");

-- -----------------------------------------------------------------------------
-- 仕訳明細 (V005)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "仕訳明細" (
    "仕訳伝票番号" VARCHAR(10) NOT NULL,
    "仕訳行番号" SMALLINT NOT NULL,
    "行摘要" VARCHAR(1000),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "バージョン" INTEGER DEFAULT 1,
    PRIMARY KEY ("仕訳伝票番号", "仕訳行番号"),
    FOREIGN KEY ("仕訳伝票番号") REFERENCES "仕訳"("仕訳伝票番号") ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- 仕訳貸借明細 (V005)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "仕訳貸借明細" (
    "仕訳伝票番号" VARCHAR(10) NOT NULL,
    "仕訳行番号" SMALLINT NOT NULL,
    "仕訳行貸借区分" VARCHAR(2) NOT NULL CHECK ("仕訳行貸借区分" IN ('借方', '貸方')),
    "勘定科目コード" VARCHAR(5) NOT NULL,
    "補助科目コード" VARCHAR(10),
    "部門コード" VARCHAR(5),
    "プロジェクトコード" VARCHAR(10),
    "仕訳金額" DECIMAL(14,2) NOT NULL,
    "通貨コード" VARCHAR(3) DEFAULT 'JPY',
    "為替レート" DECIMAL(8,2) DEFAULT 1.00,
    "基軸換算仕訳金額" DECIMAL(14,2),
    "消費税区分" VARCHAR(4) DEFAULT '課税' CHECK ("消費税区分" IN ('課税', '非課税', '免税', '不課税', '対象外')),
    "消費税率" SMALLINT DEFAULT 10,
    "消費税計算区分" VARCHAR(4) DEFAULT '外税' CHECK ("消費税計算区分" IN ('外税', '内税', '税なし')),
    "期日" DATE,
    "資金繰フラグ" SMALLINT DEFAULT 0,
    "セグメントコード" VARCHAR(10),
    "相手勘定科目コード" VARCHAR(5),
    "相手補助科目コード" VARCHAR(10),
    "付箋コード" VARCHAR(1),
    "付箋内容" VARCHAR(60),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "バージョン" INTEGER DEFAULT 1,
    PRIMARY KEY ("仕訳伝票番号", "仕訳行番号", "仕訳行貸借区分"),
    FOREIGN KEY ("仕訳伝票番号", "仕訳行番号") REFERENCES "仕訳明細"("仕訳伝票番号", "仕訳行番号") ON DELETE CASCADE,
    FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード")
);

CREATE INDEX IF NOT EXISTS idx_仕訳貸借明細_勘定科目コード ON "仕訳貸借明細"("勘定科目コード");
CREATE INDEX IF NOT EXISTS idx_仕訳貸借明細_部門コード ON "仕訳貸借明細"("部門コード");

-- -----------------------------------------------------------------------------
-- 自動仕訳パターンマスタ (V007)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "自動仕訳パターンマスタ" (
    "パターンコード" VARCHAR(10) PRIMARY KEY,
    "パターン名" VARCHAR(50) NOT NULL,
    "商品グループ" VARCHAR(10) DEFAULT 'ALL',
    "顧客グループ" VARCHAR(10) DEFAULT 'ALL',
    "売上区分" VARCHAR(2) DEFAULT '01',
    "借方勘定科目コード" VARCHAR(5) NOT NULL,
    "借方補助科目設定" VARCHAR(20),
    "貸方勘定科目コード" VARCHAR(5) NOT NULL,
    "貸方補助科目設定" VARCHAR(20),
    "返品時借方科目コード" VARCHAR(5),
    "返品時貸方科目コード" VARCHAR(5),
    "消費税処理区分" VARCHAR(2) DEFAULT '01',
    "有効開始日" DATE DEFAULT CURRENT_DATE,
    "有効終了日" DATE DEFAULT '9999-12-31',
    "優先順位" INTEGER DEFAULT 100,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY ("借方勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード"),
    FOREIGN KEY ("貸方勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード")
);

CREATE INDEX IF NOT EXISTS idx_自動仕訳パターン_商品グループ ON "自動仕訳パターンマスタ"("商品グループ");
CREATE INDEX IF NOT EXISTS idx_自動仕訳パターン_顧客グループ ON "自動仕訳パターンマスタ"("顧客グループ");
CREATE INDEX IF NOT EXISTS idx_自動仕訳パターン_優先順位 ON "自動仕訳パターンマスタ"("優先順位");

-- -----------------------------------------------------------------------------
-- 自動仕訳データ (V007)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "自動仕訳データ" (
    "自動仕訳番号" VARCHAR(15) PRIMARY KEY,
    "売上番号" VARCHAR(10) NOT NULL,
    "売上行番号" SMALLINT NOT NULL,
    "パターンコード" VARCHAR(10) NOT NULL,
    "起票日" DATE NOT NULL,
    "仕訳行貸借区分" VARCHAR(2) NOT NULL CHECK ("仕訳行貸借区分" IN ('借方', '貸方')),
    "勘定科目コード" VARCHAR(5) NOT NULL,
    "補助科目コード" VARCHAR(10),
    "部門コード" VARCHAR(5),
    "仕訳金額" DECIMAL(14,2) NOT NULL,
    "消費税額" DECIMAL(14,2) DEFAULT 0,
    "処理ステータス" VARCHAR(10) DEFAULT '処理待ち' NOT NULL CHECK ("処理ステータス" IN ('処理待ち', '処理中', '処理完了', '転記済', 'エラー')),
    "転記済フラグ" SMALLINT DEFAULT 0,
    "転記日" DATE,
    "仕訳伝票番号" VARCHAR(10),
    "エラーコード" VARCHAR(10),
    "エラーメッセージ" VARCHAR(200),
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY ("パターンコード") REFERENCES "自動仕訳パターンマスタ"("パターンコード"),
    FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード")
);

CREATE INDEX IF NOT EXISTS idx_自動仕訳_売上番号 ON "自動仕訳データ"("売上番号");
CREATE INDEX IF NOT EXISTS idx_自動仕訳_処理ステータス ON "自動仕訳データ"("処理ステータス");
CREATE INDEX IF NOT EXISTS idx_自動仕訳_転記済フラグ ON "自動仕訳データ"("転記済フラグ");
CREATE INDEX IF NOT EXISTS idx_自動仕訳_起票日 ON "自動仕訳データ"("起票日");

-- -----------------------------------------------------------------------------
-- 自動仕訳処理履歴 (V007)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "自動仕訳処理履歴" (
    "処理番号" VARCHAR(15) PRIMARY KEY,
    "処理日時" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "処理対象開始日" DATE NOT NULL,
    "処理対象終了日" DATE NOT NULL,
    "処理件数" INTEGER DEFAULT 0,
    "成功件数" INTEGER DEFAULT 0,
    "エラー件数" INTEGER DEFAULT 0,
    "処理金額合計" DECIMAL(15,2) DEFAULT 0,
    "処理者" VARCHAR(50),
    "備考" TEXT,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 日次勘定科目残高 (V008)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "日次勘定科目残高" (
    "起票日" DATE NOT NULL,
    "勘定科目コード" VARCHAR(5) NOT NULL,
    "補助科目コード" VARCHAR(10) NOT NULL DEFAULT '',
    "部門コード" VARCHAR(5) NOT NULL DEFAULT '00000',
    "プロジェクトコード" VARCHAR(10) NOT NULL DEFAULT '',
    "決算仕訳フラグ" SMALLINT NOT NULL DEFAULT 0,
    "借方金額" DECIMAL(15, 0) NOT NULL DEFAULT 0,
    "貸方金額" DECIMAL(15, 0) NOT NULL DEFAULT 0,
    "バージョン" INTEGER NOT NULL DEFAULT 1,
    "作成日時" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("起票日", "勘定科目コード", "補助科目コード", "部門コード", "プロジェクトコード", "決算仕訳フラグ"),
    FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード")
);

CREATE INDEX IF NOT EXISTS idx_日次残高_起票日 ON "日次勘定科目残高"("起票日");
CREATE INDEX IF NOT EXISTS idx_日次残高_勘定科目 ON "日次勘定科目残高"("勘定科目コード");
CREATE INDEX IF NOT EXISTS idx_日次残高_部門 ON "日次勘定科目残高"("部門コード");

-- -----------------------------------------------------------------------------
-- 月次勘定科目残高 (V009)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "月次勘定科目残高" (
    "決算期" INTEGER NOT NULL,
    "月度" SMALLINT NOT NULL,
    "勘定科目コード" VARCHAR(5) NOT NULL,
    "補助科目コード" VARCHAR(10) NOT NULL DEFAULT '',
    "部門コード" VARCHAR(5) NOT NULL DEFAULT '00000',
    "プロジェクトコード" VARCHAR(10) NOT NULL DEFAULT '',
    "決算仕訳フラグ" SMALLINT NOT NULL DEFAULT 0,
    "月初残高" DECIMAL(15, 0) NOT NULL DEFAULT 0,
    "借方金額" DECIMAL(15, 0) NOT NULL DEFAULT 0,
    "貸方金額" DECIMAL(15, 0) NOT NULL DEFAULT 0,
    "月末残高" DECIMAL(15, 0) NOT NULL DEFAULT 0,
    "バージョン" INTEGER NOT NULL DEFAULT 1,
    "作成日時" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("決算期", "月度", "勘定科目コード", "補助科目コード", "部門コード", "プロジェクトコード", "決算仕訳フラグ"),
    FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード"),
    CHECK ("月度" BETWEEN 1 AND 12)
);

CREATE INDEX IF NOT EXISTS idx_月次残高_決算期月度 ON "月次勘定科目残高"("決算期", "月度");
CREATE INDEX IF NOT EXISTS idx_月次残高_勘定科目 ON "月次勘定科目残高"("勘定科目コード");

-- -----------------------------------------------------------------------------
-- 変更ログ (V010) - H2 用簡略版（トリガーは省略）
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "変更ログ" (
    "ログID" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "テーブル名" VARCHAR(50) NOT NULL,
    "レコードキー" VARCHAR(100) NOT NULL,
    "操作種別" VARCHAR(10) NOT NULL CHECK ("操作種別" IN ('INSERT', 'UPDATE', 'DELETE')),
    "操作前データ" TEXT,
    "操作後データ" TEXT,
    "操作日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "操作者" VARCHAR(50),
    "操作端末" VARCHAR(100),
    "備考" TEXT
);

CREATE INDEX IF NOT EXISTS idx_変更ログ_テーブル名 ON "変更ログ"("テーブル名");
CREATE INDEX IF NOT EXISTS idx_変更ログ_レコードキー ON "変更ログ"("レコードキー");
CREATE INDEX IF NOT EXISTS idx_変更ログ_操作日時 ON "変更ログ"("操作日時");
CREATE INDEX IF NOT EXISTS idx_変更ログ_操作種別 ON "変更ログ"("操作種別");
CREATE INDEX IF NOT EXISTS idx_変更ログ_操作者 ON "変更ログ"("操作者");
