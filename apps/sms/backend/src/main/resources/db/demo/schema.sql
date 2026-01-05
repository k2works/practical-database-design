-- =============================================================================
-- H2 デモ環境用スキーマ
-- PostgreSQL マイグレーションを H2 互換に変換
-- =============================================================================

-- ============================================================
-- マスタテーブル
-- ============================================================

-- 部門マスタ
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

CREATE INDEX idx_部門マスタ_部門パス ON "部門マスタ"("部門パス");
CREATE INDEX idx_部門マスタ_組織階層 ON "部門マスタ"("組織階層");

-- 社員マスタ
CREATE TABLE "社員マスタ" (
    "社員コード" VARCHAR(10) PRIMARY KEY,
    "社員名" VARCHAR(20) NOT NULL,
    "社員名カナ" VARCHAR(40),
    "部門コード" VARCHAR(10),
    "開始日" DATE,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(50),
    FOREIGN KEY ("部門コード", "開始日") REFERENCES "部門マスタ"("部門コード", "開始日")
);

CREATE INDEX idx_社員マスタ_部門コード ON "社員マスタ"("部門コード");

-- 商品分類マスタ
CREATE TABLE "商品分類マスタ" (
    "商品分類コード" VARCHAR(10) PRIMARY KEY,
    "商品分類名" VARCHAR(50) NOT NULL,
    "商品分類階層" INTEGER NOT NULL DEFAULT 0,
    "商品分類パス" VARCHAR(100),
    "最下層区分" BOOLEAN NOT NULL DEFAULT FALSE,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(50)
);

CREATE INDEX idx_商品分類マスタ_商品分類パス ON "商品分類マスタ"("商品分類パス");

-- 商品マスタ（ENUM は VARCHAR で代用）
CREATE TABLE "商品マスタ" (
    "商品コード" VARCHAR(20) PRIMARY KEY,
    "商品正式名" VARCHAR(200),
    "商品名" VARCHAR(100) NOT NULL,
    "商品名カナ" VARCHAR(200),
    "商品区分" VARCHAR(20) NOT NULL DEFAULT '商品',
    "製品型番" VARCHAR(50),
    "販売単価" DECIMAL(15, 2) DEFAULT 0,
    "仕入単価" DECIMAL(15, 2) DEFAULT 0,
    "税区分" VARCHAR(10) NOT NULL DEFAULT '外税',
    "商品分類コード" VARCHAR(10) REFERENCES "商品分類マスタ"("商品分類コード"),
    "雑区分" BOOLEAN DEFAULT FALSE,
    "在庫管理対象区分" BOOLEAN DEFAULT TRUE,
    "在庫引当区分" BOOLEAN DEFAULT TRUE,
    "仕入先コード" VARCHAR(20),
    "仕入先枝番" VARCHAR(10),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(50)
);

CREATE INDEX idx_商品マスタ_商品区分 ON "商品マスタ"("商品区分");
CREATE INDEX idx_商品マスタ_商品分類コード ON "商品マスタ"("商品分類コード");

-- 顧客別販売単価
CREATE TABLE "顧客別販売単価" (
    "商品コード" VARCHAR(20) NOT NULL REFERENCES "商品マスタ"("商品コード"),
    "取引先コード" VARCHAR(20) NOT NULL,
    "適用開始日" DATE NOT NULL,
    "適用終了日" DATE,
    "販売単価" DECIMAL(15, 2) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(50),
    PRIMARY KEY ("商品コード", "取引先コード", "適用開始日")
);

-- 取引先グループマスタ
CREATE TABLE "取引先グループマスタ" (
    "取引先グループコード" VARCHAR(10) PRIMARY KEY,
    "取引先グループ名" VARCHAR(50) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(50)
);

-- 取引先マスタ
CREATE TABLE "取引先マスタ" (
    "取引先コード" VARCHAR(20) PRIMARY KEY,
    "取引先名" VARCHAR(100) NOT NULL,
    "取引先カナ" VARCHAR(200),
    "顧客区分" BOOLEAN DEFAULT FALSE,
    "仕入先区分" BOOLEAN DEFAULT FALSE,
    "郵便番号" VARCHAR(10),
    "住所1" VARCHAR(100),
    "住所2" VARCHAR(100),
    "取引先分類コード" VARCHAR(10),
    "取引禁止フラグ" BOOLEAN DEFAULT FALSE,
    "雑区分" BOOLEAN DEFAULT FALSE,
    "取引先グループコード" VARCHAR(10) REFERENCES "取引先グループマスタ"("取引先グループコード"),
    "与信限度額" DECIMAL(15, 2) DEFAULT 0,
    "与信一時増加枠" DECIMAL(15, 2) DEFAULT 0,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(50)
);

CREATE INDEX idx_取引先マスタ_取引先グループ ON "取引先マスタ"("取引先グループコード");

-- 顧客マスタ
CREATE TABLE "顧客マスタ" (
    "顧客コード" VARCHAR(20) NOT NULL REFERENCES "取引先マスタ"("取引先コード"),
    "顧客枝番" VARCHAR(10) NOT NULL DEFAULT '00',
    "顧客区分" VARCHAR(10),
    "請求先コード" VARCHAR(20),
    "請求先枝番" VARCHAR(10),
    "回収先コード" VARCHAR(20),
    "回収先枝番" VARCHAR(10),
    "顧客名" VARCHAR(100),
    "顧客名カナ" VARCHAR(200),
    "自社担当者コード" VARCHAR(10),
    "顧客担当者名" VARCHAR(50),
    "顧客部門名" VARCHAR(50),
    "顧客郵便番号" VARCHAR(10),
    "顧客都道府県" VARCHAR(10),
    "顧客住所1" VARCHAR(100),
    "顧客住所2" VARCHAR(100),
    "顧客電話番号" VARCHAR(20),
    "顧客FAX番号" VARCHAR(20),
    "顧客メールアドレス" VARCHAR(100),
    "顧客請求区分" VARCHAR(10) DEFAULT '締め',
    "顧客締日1" INTEGER,
    "顧客支払月1" INTEGER,
    "顧客支払日1" INTEGER,
    "顧客支払方法1" VARCHAR(20),
    "顧客締日2" INTEGER,
    "顧客支払月2" INTEGER,
    "顧客支払日2" INTEGER,
    "顧客支払方法2" VARCHAR(20),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(50),
    PRIMARY KEY ("顧客コード", "顧客枝番")
);

CREATE INDEX idx_顧客マスタ_請求先 ON "顧客マスタ"("請求先コード", "請求先枝番");

-- 仕入先マスタ
CREATE TABLE "仕入先マスタ" (
    "仕入先コード" VARCHAR(20) NOT NULL REFERENCES "取引先マスタ"("取引先コード"),
    "仕入先枝番" VARCHAR(10) NOT NULL DEFAULT '00',
    "仕入先担当者名" VARCHAR(50),
    "部門名" VARCHAR(50),
    "電話番号" VARCHAR(20),
    "FAX番号" VARCHAR(20),
    "メールアドレス" VARCHAR(100),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(50),
    PRIMARY KEY ("仕入先コード", "仕入先枝番")
);

-- 地域マスタ
CREATE TABLE "地域マスタ" (
    "地域コード" VARCHAR(10) PRIMARY KEY,
    "地域名" VARCHAR(50) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(50)
);

-- 出荷先マスタ
CREATE TABLE "出荷先マスタ" (
    "取引先コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) NOT NULL,
    "出荷先番号" VARCHAR(10) NOT NULL,
    "出荷先名" VARCHAR(100) NOT NULL,
    "地域コード" VARCHAR(10) REFERENCES "地域マスタ"("地域コード"),
    "出荷先郵便番号" VARCHAR(10),
    "出荷先住所1" VARCHAR(100),
    "出荷先住所2" VARCHAR(100),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(50),
    PRIMARY KEY ("取引先コード", "顧客枝番", "出荷先番号"),
    FOREIGN KEY ("取引先コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番")
);

-- 倉庫マスタ
CREATE TABLE "倉庫マスタ" (
    "倉庫コード" VARCHAR(20) PRIMARY KEY,
    "倉庫名" VARCHAR(100) NOT NULL,
    "倉庫名カナ" VARCHAR(200),
    "郵便番号" VARCHAR(10),
    "住所" VARCHAR(200),
    "電話番号" VARCHAR(20),
    "有効フラグ" BOOLEAN DEFAULT TRUE,
    "倉庫区分" VARCHAR(10) DEFAULT '自社' NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者名" VARCHAR(50)
);

-- ロケーションマスタ
CREATE TABLE "ロケーションマスタ" (
    "ロケーションコード" VARCHAR(20) PRIMARY KEY,
    "倉庫コード" VARCHAR(20) NOT NULL,
    "棚番" VARCHAR(20) NOT NULL,
    "ゾーン" VARCHAR(10),
    "通路" VARCHAR(10),
    "ラック" VARCHAR(10),
    "段" VARCHAR(10),
    "間口" VARCHAR(10),
    "有効フラグ" BOOLEAN DEFAULT TRUE NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_ロケーション_倉庫 FOREIGN KEY ("倉庫コード") REFERENCES "倉庫マスタ"("倉庫コード"),
    UNIQUE ("倉庫コード", "棚番")
);

CREATE INDEX idx_ロケーション_倉庫 ON "ロケーションマスタ"("倉庫コード");

-- ============================================================
-- トランザクションテーブル
-- ============================================================

-- 見積データ
CREATE TABLE "見積データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "見積番号" VARCHAR(20) UNIQUE NOT NULL,
    "見積日" DATE NOT NULL,
    "見積有効期限" DATE,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "担当者コード" VARCHAR(20),
    "件名" VARCHAR(200),
    "見積金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "見積合計" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "ステータス" VARCHAR(20) DEFAULT '商談中' NOT NULL,
    "備考" CLOB,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_見積データ_顧客 FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番")
);

CREATE INDEX idx_見積データ_顧客コード ON "見積データ"("顧客コード");
CREATE INDEX idx_見積データ_見積日 ON "見積データ"("見積日");
CREATE INDEX idx_見積データ_ステータス ON "見積データ"("ステータス");

-- 見積明細
CREATE TABLE "見積明細" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "見積ID" INTEGER NOT NULL,
    "行番号" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "商品名" VARCHAR(100) NOT NULL,
    "数量" DECIMAL(15, 2) NOT NULL,
    "単位" VARCHAR(10),
    "単価" DECIMAL(15, 2) NOT NULL,
    "金額" DECIMAL(15, 2) NOT NULL,
    "税区分" VARCHAR(10) DEFAULT '外税' NOT NULL,
    "消費税率" DECIMAL(5, 2) DEFAULT 10.00 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "備考" CLOB,
    CONSTRAINT fk_見積明細_見積 FOREIGN KEY ("見積ID") REFERENCES "見積データ"("ID") ON DELETE CASCADE,
    CONSTRAINT fk_見積明細_商品 FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT uq_見積明細_行番号 UNIQUE ("見積ID", "行番号")
);

-- 受注データ
CREATE TABLE "受注データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "受注番号" VARCHAR(20) UNIQUE NOT NULL,
    "受注日" DATE NOT NULL,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "出荷先番号" VARCHAR(10),
    "担当者コード" VARCHAR(20),
    "希望納期" DATE,
    "出荷予定日" DATE,
    "受注金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "受注合計" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "ステータス" VARCHAR(20) DEFAULT '受付済' NOT NULL,
    "見積ID" INTEGER,
    "顧客注文番号" VARCHAR(50),
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_受注データ_顧客 FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番"),
    CONSTRAINT fk_受注データ_出荷先 FOREIGN KEY ("顧客コード", "顧客枝番", "出荷先番号") REFERENCES "出荷先マスタ"("取引先コード", "顧客枝番", "出荷先番号"),
    CONSTRAINT fk_受注データ_見積 FOREIGN KEY ("見積ID") REFERENCES "見積データ"("ID")
);

CREATE INDEX idx_受注データ_顧客コード ON "受注データ"("顧客コード");
CREATE INDEX idx_受注データ_受注日 ON "受注データ"("受注日");
CREATE INDEX idx_受注データ_ステータス ON "受注データ"("ステータス");
CREATE INDEX idx_受注データ_希望納期 ON "受注データ"("希望納期");

-- 受注明細
CREATE TABLE "受注明細" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "受注ID" INTEGER NOT NULL,
    "行番号" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "商品名" VARCHAR(100) NOT NULL,
    "受注数量" DECIMAL(15, 2) NOT NULL,
    "引当数量" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "出荷数量" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "残数量" DECIMAL(15, 2) NOT NULL,
    "単位" VARCHAR(10),
    "単価" DECIMAL(15, 2) NOT NULL,
    "金額" DECIMAL(15, 2) NOT NULL,
    "税区分" VARCHAR(10) DEFAULT '外税' NOT NULL,
    "消費税率" DECIMAL(5, 2) DEFAULT 10.00 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "倉庫コード" VARCHAR(20),
    "希望納期" DATE,
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT fk_受注明細_受注 FOREIGN KEY ("受注ID") REFERENCES "受注データ"("ID") ON DELETE CASCADE,
    CONSTRAINT fk_受注明細_商品 FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT uq_受注明細_行番号 UNIQUE ("受注ID", "行番号")
);

-- 出荷データ
CREATE TABLE "出荷データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "出荷番号" VARCHAR(20) UNIQUE NOT NULL,
    "出荷日" DATE NOT NULL,
    "受注ID" INTEGER NOT NULL,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "出荷先番号" VARCHAR(10),
    "出荷先名" VARCHAR(100),
    "出荷先郵便番号" VARCHAR(10),
    "出荷先住所1" VARCHAR(100),
    "出荷先住所2" VARCHAR(100),
    "担当者コード" VARCHAR(20),
    "倉庫コード" VARCHAR(20),
    "ステータス" VARCHAR(20) DEFAULT '出荷指示済' NOT NULL,
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_出荷データ_受注 FOREIGN KEY ("受注ID") REFERENCES "受注データ"("ID"),
    CONSTRAINT fk_出荷データ_顧客 FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番"),
    CONSTRAINT fk_出荷データ_出荷先 FOREIGN KEY ("顧客コード", "顧客枝番", "出荷先番号") REFERENCES "出荷先マスタ"("取引先コード", "顧客枝番", "出荷先番号")
);

CREATE INDEX idx_出荷データ_受注ID ON "出荷データ"("受注ID");
CREATE INDEX idx_出荷データ_顧客コード ON "出荷データ"("顧客コード");
CREATE INDEX idx_出荷データ_出荷日 ON "出荷データ"("出荷日");
CREATE INDEX idx_出荷データ_ステータス ON "出荷データ"("ステータス");

-- 出荷明細
CREATE TABLE "出荷明細" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "出荷ID" INTEGER NOT NULL,
    "行番号" INTEGER NOT NULL,
    "受注明細ID" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "商品名" VARCHAR(100) NOT NULL,
    "出荷数量" DECIMAL(15, 2) NOT NULL,
    "単位" VARCHAR(10),
    "単価" DECIMAL(15, 2) NOT NULL,
    "金額" DECIMAL(15, 2) NOT NULL,
    "税区分" VARCHAR(10) DEFAULT '外税' NOT NULL,
    "消費税率" DECIMAL(5, 2) DEFAULT 10.00 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "倉庫コード" VARCHAR(20),
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT fk_出荷明細_出荷 FOREIGN KEY ("出荷ID") REFERENCES "出荷データ"("ID") ON DELETE CASCADE,
    CONSTRAINT fk_出荷明細_受注明細 FOREIGN KEY ("受注明細ID") REFERENCES "受注明細"("ID"),
    CONSTRAINT fk_出荷明細_商品 FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT uq_出荷明細_行番号 UNIQUE ("出荷ID", "行番号")
);

-- 売上データ
CREATE TABLE "売上データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "売上番号" VARCHAR(20) UNIQUE NOT NULL,
    "売上日" DATE NOT NULL,
    "受注ID" INTEGER NOT NULL,
    "出荷ID" INTEGER,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "担当者コード" VARCHAR(20),
    "売上金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "売上合計" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "ステータス" VARCHAR(20) DEFAULT '計上済' NOT NULL,
    "請求ID" INTEGER,
    "伝票区分" VARCHAR(10) DEFAULT '通常' NOT NULL,
    "元伝票番号" VARCHAR(20),
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_売上データ_受注 FOREIGN KEY ("受注ID") REFERENCES "受注データ"("ID"),
    CONSTRAINT fk_売上データ_出荷 FOREIGN KEY ("出荷ID") REFERENCES "出荷データ"("ID"),
    CONSTRAINT fk_売上データ_顧客 FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番")
);

CREATE INDEX idx_売上データ_受注ID ON "売上データ"("受注ID");
CREATE INDEX idx_売上データ_出荷ID ON "売上データ"("出荷ID");
CREATE INDEX idx_売上データ_顧客コード ON "売上データ"("顧客コード");
CREATE INDEX idx_売上データ_売上日 ON "売上データ"("売上日");
CREATE INDEX idx_売上データ_ステータス ON "売上データ"("ステータス");

-- 売上明細
CREATE TABLE "売上明細" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "売上ID" INTEGER NOT NULL,
    "行番号" INTEGER NOT NULL,
    "受注明細ID" INTEGER NOT NULL,
    "出荷明細ID" INTEGER,
    "商品コード" VARCHAR(20) NOT NULL,
    "商品名" VARCHAR(100) NOT NULL,
    "売上数量" DECIMAL(15, 2) NOT NULL,
    "単位" VARCHAR(10),
    "単価" DECIMAL(15, 2) NOT NULL,
    "金額" DECIMAL(15, 2) NOT NULL,
    "税区分" VARCHAR(10) DEFAULT '外税' NOT NULL,
    "消費税率" DECIMAL(5, 2) DEFAULT 10.00 NOT NULL,
    "消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT fk_売上明細_売上 FOREIGN KEY ("売上ID") REFERENCES "売上データ"("ID") ON DELETE CASCADE,
    CONSTRAINT fk_売上明細_受注明細 FOREIGN KEY ("受注明細ID") REFERENCES "受注明細"("ID"),
    CONSTRAINT fk_売上明細_出荷明細 FOREIGN KEY ("出荷明細ID") REFERENCES "出荷明細"("ID"),
    CONSTRAINT fk_売上明細_商品 FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT uq_売上明細_行番号 UNIQUE ("売上ID", "行番号")
);

-- 請求データ
CREATE TABLE "請求データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "請求番号" VARCHAR(20) UNIQUE NOT NULL,
    "請求日" DATE NOT NULL,
    "請求先コード" VARCHAR(20) NOT NULL,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "締日" DATE,
    "請求区分" VARCHAR(10) NOT NULL,
    "前回請求残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "入金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "繰越残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "今回売上額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "今回消費税額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "今回請求額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "請求残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "回収予定日" DATE,
    "ステータス" VARCHAR(20) DEFAULT '未発行' NOT NULL,
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_請求データ_顧客 FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番")
);

CREATE INDEX idx_請求データ_顧客コード ON "請求データ"("顧客コード");
CREATE INDEX idx_請求データ_請求日 ON "請求データ"("請求日");
CREATE INDEX idx_請求データ_ステータス ON "請求データ"("ステータス");

-- 請求明細
CREATE TABLE "請求明細" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "請求ID" INTEGER NOT NULL,
    "行番号" INTEGER NOT NULL,
    "売上ID" INTEGER,
    "売上番号" VARCHAR(20),
    "売上日" DATE,
    "売上金額" DECIMAL(15, 2) NOT NULL,
    "消費税額" DECIMAL(15, 2) NOT NULL,
    "合計金額" DECIMAL(15, 2) NOT NULL,
    CONSTRAINT fk_請求明細_請求 FOREIGN KEY ("請求ID") REFERENCES "請求データ"("ID") ON DELETE CASCADE,
    CONSTRAINT fk_請求明細_売上 FOREIGN KEY ("売上ID") REFERENCES "売上データ"("ID"),
    CONSTRAINT uk_請求明細_請求_行 UNIQUE ("請求ID", "行番号")
);

CREATE INDEX idx_請求明細_請求ID ON "請求明細"("請求ID");

-- 請求締履歴
CREATE TABLE "請求締履歴" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "締年月" VARCHAR(7) NOT NULL,
    "締日" DATE NOT NULL,
    "売上件数" INTEGER NOT NULL,
    "売上合計" DECIMAL(15, 2) NOT NULL,
    "消費税合計" DECIMAL(15, 2) NOT NULL,
    "請求ID" INTEGER,
    "処理日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_請求締履歴_顧客 FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番"),
    CONSTRAINT fk_請求締履歴_請求 FOREIGN KEY ("請求ID") REFERENCES "請求データ"("ID"),
    CONSTRAINT uk_請求締履歴_顧客_年月 UNIQUE ("顧客コード", "顧客枝番", "締年月")
);

-- 売掛金残高
CREATE TABLE "売掛金残高" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "基準日" DATE NOT NULL,
    "前月残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "当月売上" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "当月入金" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "当月残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_売掛金残高_顧客 FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番"),
    CONSTRAINT uk_売掛金残高_顧客_基準日 UNIQUE ("顧客コード", "顧客枝番", "基準日")
);

CREATE INDEX idx_売掛金残高_基準日 ON "売掛金残高"("基準日");

-- 入金データ
CREATE TABLE "入金データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "入金番号" VARCHAR(20) UNIQUE NOT NULL,
    "入金日" DATE NOT NULL,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "入金方法" VARCHAR(20) NOT NULL,
    "入金金額" DECIMAL(15, 2) NOT NULL,
    "消込済金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "未消込金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "手数料" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "振込名義" VARCHAR(100),
    "銀行名" VARCHAR(50),
    "口座番号" VARCHAR(20),
    "ステータス" VARCHAR(20) DEFAULT '入金済' NOT NULL,
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_入金データ_顧客 FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番")
);

CREATE INDEX idx_入金データ_顧客コード ON "入金データ"("顧客コード");
CREATE INDEX idx_入金データ_入金日 ON "入金データ"("入金日");
CREATE INDEX idx_入金データ_ステータス ON "入金データ"("ステータス");

-- 入金消込明細
CREATE TABLE "入金消込明細" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "入金ID" INTEGER NOT NULL,
    "行番号" INTEGER NOT NULL,
    "請求ID" INTEGER,
    "消込日" DATE NOT NULL,
    "消込金額" DECIMAL(15, 2) NOT NULL,
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_入金消込明細_入金 FOREIGN KEY ("入金ID") REFERENCES "入金データ"("ID") ON DELETE CASCADE,
    CONSTRAINT fk_入金消込明細_請求 FOREIGN KEY ("請求ID") REFERENCES "請求データ"("ID"),
    CONSTRAINT uk_入金消込明細_入金_行 UNIQUE ("入金ID", "行番号")
);

CREATE INDEX idx_入金消込明細_入金ID ON "入金消込明細"("入金ID");
CREATE INDEX idx_入金消込明細_請求ID ON "入金消込明細"("請求ID");

-- 前受金データ
CREATE TABLE "前受金データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "前受金番号" VARCHAR(20) UNIQUE NOT NULL,
    "発生日" DATE NOT NULL,
    "顧客コード" VARCHAR(20) NOT NULL,
    "顧客枝番" VARCHAR(10) DEFAULT '00',
    "入金ID" INTEGER,
    "前受金額" DECIMAL(15, 2) NOT NULL,
    "使用済金額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "残高" DECIMAL(15, 2) NOT NULL,
    "備考" CLOB,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_前受金データ_顧客 FOREIGN KEY ("顧客コード", "顧客枝番") REFERENCES "顧客マスタ"("顧客コード", "顧客枝番"),
    CONSTRAINT fk_前受金データ_入金 FOREIGN KEY ("入金ID") REFERENCES "入金データ"("ID")
);

CREATE INDEX idx_前受金データ_顧客コード ON "前受金データ"("顧客コード");

-- 発注データ
CREATE TABLE "発注データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "発注番号" VARCHAR(20) UNIQUE NOT NULL,
    "仕入先コード" VARCHAR(20) NOT NULL,
    "仕入先枝番" VARCHAR(10) DEFAULT '00',
    "発注日" DATE NOT NULL,
    "希望納期" DATE,
    "発注ステータス" VARCHAR(20) DEFAULT '作成中' NOT NULL,
    "発注担当者コード" VARCHAR(20),
    "発注合計金額" DECIMAL(15, 2) DEFAULT 0,
    "税額" DECIMAL(15, 2) DEFAULT 0,
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_発注_仕入先 FOREIGN KEY ("仕入先コード", "仕入先枝番") REFERENCES "仕入先マスタ"("仕入先コード", "仕入先枝番")
);

CREATE INDEX idx_発注データ_仕入先コード ON "発注データ"("仕入先コード");
CREATE INDEX idx_発注データ_発注日 ON "発注データ"("発注日");
CREATE INDEX idx_発注データ_ステータス ON "発注データ"("発注ステータス");

-- 発注明細データ
CREATE TABLE "発注明細データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "発注ID" INTEGER NOT NULL,
    "発注行番号" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "発注数量" DECIMAL(15, 2) NOT NULL,
    "発注単価" DECIMAL(15, 2) NOT NULL,
    "発注金額" DECIMAL(15, 2) NOT NULL,
    "入荷予定日" DATE,
    "入荷済数量" DECIMAL(15, 2) DEFAULT 0,
    "残数量" DECIMAL(15, 2),
    "備考" CLOB,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_発注明細_発注 FOREIGN KEY ("発注ID") REFERENCES "発注データ"("ID") ON DELETE CASCADE,
    CONSTRAINT fk_発注明細_商品 FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT uk_発注明細_発注_行 UNIQUE ("発注ID", "発注行番号")
);

CREATE INDEX idx_発注明細_商品コード ON "発注明細データ"("商品コード");

-- 入荷データ
CREATE TABLE "入荷データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "入荷番号" VARCHAR(20) UNIQUE NOT NULL,
    "発注ID" INTEGER NOT NULL,
    "仕入先コード" VARCHAR(20) NOT NULL,
    "仕入先枝番" VARCHAR(10) DEFAULT '00',
    "入荷日" DATE NOT NULL,
    "入荷ステータス" VARCHAR(20) DEFAULT '入荷待ち' NOT NULL,
    "入荷担当者コード" VARCHAR(20),
    "倉庫コード" VARCHAR(20) NOT NULL,
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_入荷_発注 FOREIGN KEY ("発注ID") REFERENCES "発注データ"("ID"),
    CONSTRAINT fk_入荷_仕入先 FOREIGN KEY ("仕入先コード", "仕入先枝番") REFERENCES "仕入先マスタ"("仕入先コード", "仕入先枝番"),
    CONSTRAINT fk_入荷_倉庫 FOREIGN KEY ("倉庫コード") REFERENCES "倉庫マスタ"("倉庫コード")
);

CREATE INDEX idx_入荷データ_発注ID ON "入荷データ"("発注ID");
CREATE INDEX idx_入荷データ_入荷日 ON "入荷データ"("入荷日");
CREATE INDEX idx_入荷データ_ステータス ON "入荷データ"("入荷ステータス");

-- 入荷明細データ
CREATE TABLE "入荷明細データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "入荷ID" INTEGER NOT NULL,
    "入荷行番号" INTEGER NOT NULL,
    "発注明細ID" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "入荷数量" DECIMAL(15, 2) NOT NULL,
    "検品数量" DECIMAL(15, 2) DEFAULT 0,
    "合格数量" DECIMAL(15, 2) DEFAULT 0,
    "不合格数量" DECIMAL(15, 2) DEFAULT 0,
    "入荷単価" DECIMAL(15, 2) NOT NULL,
    "入荷金額" DECIMAL(15, 2) NOT NULL,
    "備考" CLOB,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_入荷明細_入荷 FOREIGN KEY ("入荷ID") REFERENCES "入荷データ"("ID") ON DELETE CASCADE,
    CONSTRAINT fk_入荷明細_発注明細 FOREIGN KEY ("発注明細ID") REFERENCES "発注明細データ"("ID"),
    CONSTRAINT fk_入荷明細_商品 FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT uk_入荷明細_入荷_行 UNIQUE ("入荷ID", "入荷行番号")
);

CREATE INDEX idx_入荷明細_発注明細ID ON "入荷明細データ"("発注明細ID");

-- 仕入データ
CREATE TABLE "仕入データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "仕入番号" VARCHAR(20) UNIQUE NOT NULL,
    "入荷ID" INTEGER NOT NULL,
    "仕入先コード" VARCHAR(20) NOT NULL,
    "仕入先枝番" VARCHAR(10) DEFAULT '00',
    "仕入日" DATE NOT NULL,
    "仕入合計金額" DECIMAL(15, 2) NOT NULL,
    "税額" DECIMAL(15, 2) NOT NULL,
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_仕入_入荷 FOREIGN KEY ("入荷ID") REFERENCES "入荷データ"("ID"),
    CONSTRAINT fk_仕入_仕入先 FOREIGN KEY ("仕入先コード", "仕入先枝番") REFERENCES "仕入先マスタ"("仕入先コード", "仕入先枝番")
);

CREATE INDEX idx_仕入データ_仕入先コード ON "仕入データ"("仕入先コード");
CREATE INDEX idx_仕入データ_仕入日 ON "仕入データ"("仕入日");

-- 仕入明細データ
CREATE TABLE "仕入明細データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "仕入ID" INTEGER NOT NULL,
    "仕入行番号" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "仕入数量" DECIMAL(15, 2) NOT NULL,
    "仕入単価" DECIMAL(15, 2) NOT NULL,
    "仕入金額" DECIMAL(15, 2) NOT NULL,
    "備考" CLOB,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_仕入明細_仕入 FOREIGN KEY ("仕入ID") REFERENCES "仕入データ"("ID") ON DELETE CASCADE,
    CONSTRAINT fk_仕入明細_商品 FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT uk_仕入明細_仕入_行 UNIQUE ("仕入ID", "仕入行番号")
);

-- 在庫データ
CREATE TABLE "在庫データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "倉庫コード" VARCHAR(20) NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "ロケーションコード" VARCHAR(20),
    "現在庫数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "引当数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "発注残数" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "最終入庫日" DATE,
    "最終出庫日" DATE,
    "ロット番号" VARCHAR(50),
    "シリアル番号" VARCHAR(50),
    "有効期限" DATE,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_在庫_倉庫 FOREIGN KEY ("倉庫コード") REFERENCES "倉庫マスタ"("倉庫コード"),
    CONSTRAINT fk_在庫_商品 FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT fk_在庫_ロケーション FOREIGN KEY ("ロケーションコード") REFERENCES "ロケーションマスタ"("ロケーションコード"),
    UNIQUE ("倉庫コード", "商品コード", "ロケーションコード", "ロット番号")
);

CREATE INDEX idx_在庫_倉庫コード ON "在庫データ"("倉庫コード");
CREATE INDEX idx_在庫_商品コード ON "在庫データ"("商品コード");
CREATE INDEX idx_在庫_ロケーション ON "在庫データ"("ロケーションコード");
CREATE INDEX idx_在庫_ロット番号 ON "在庫データ"("ロット番号");
CREATE INDEX idx_在庫_有効期限 ON "在庫データ"("有効期限");

-- 入出庫履歴データ
CREATE TABLE "入出庫履歴データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "倉庫コード" VARCHAR(20) NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "移動日時" TIMESTAMP NOT NULL,
    "移動区分" VARCHAR(20) NOT NULL,
    "移動数量" DECIMAL(15, 2) NOT NULL,
    "移動前在庫数" DECIMAL(15, 2) NOT NULL,
    "移動後在庫数" DECIMAL(15, 2) NOT NULL,
    "伝票番号" VARCHAR(20),
    "伝票種別" VARCHAR(20),
    "移動理由" CLOB,
    "ロケーションコード" VARCHAR(20),
    "ロット番号" VARCHAR(50),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    CONSTRAINT fk_入出庫履歴_倉庫 FOREIGN KEY ("倉庫コード") REFERENCES "倉庫マスタ"("倉庫コード"),
    CONSTRAINT fk_入出庫履歴_商品 FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード")
);

CREATE INDEX idx_入出庫履歴_倉庫コード ON "入出庫履歴データ"("倉庫コード");
CREATE INDEX idx_入出庫履歴_商品コード ON "入出庫履歴データ"("商品コード");
CREATE INDEX idx_入出庫履歴_移動日時 ON "入出庫履歴データ"("移動日時");
CREATE INDEX idx_入出庫履歴_伝票番号 ON "入出庫履歴データ"("伝票番号");

-- 棚卸データ
CREATE TABLE "棚卸データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "棚卸番号" VARCHAR(20) UNIQUE NOT NULL,
    "倉庫コード" VARCHAR(20) NOT NULL,
    "棚卸日" DATE NOT NULL,
    "棚卸開始日時" TIMESTAMP,
    "棚卸終了日時" TIMESTAMP,
    "ステータス" VARCHAR(20) DEFAULT '作成中' NOT NULL,
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_棚卸_倉庫 FOREIGN KEY ("倉庫コード") REFERENCES "倉庫マスタ"("倉庫コード")
);

CREATE INDEX idx_棚卸_倉庫コード ON "棚卸データ"("倉庫コード");
CREATE INDEX idx_棚卸_棚卸日 ON "棚卸データ"("棚卸日");
CREATE INDEX idx_棚卸_ステータス ON "棚卸データ"("ステータス");

-- 棚卸明細データ
CREATE TABLE "棚卸明細データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "棚卸ID" INTEGER NOT NULL,
    "棚卸行番号" INTEGER NOT NULL,
    "商品コード" VARCHAR(20) NOT NULL,
    "ロケーションコード" VARCHAR(20),
    "ロット番号" VARCHAR(50),
    "帳簿在庫数" DECIMAL(15, 2) NOT NULL,
    "実棚数" DECIMAL(15, 2),
    "差異数" DECIMAL(15, 2),
    "差異理由" CLOB,
    "調整済フラグ" BOOLEAN DEFAULT FALSE NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_棚卸明細_棚卸 FOREIGN KEY ("棚卸ID") REFERENCES "棚卸データ"("ID") ON DELETE CASCADE,
    CONSTRAINT fk_棚卸明細_商品 FOREIGN KEY ("商品コード") REFERENCES "商品マスタ"("商品コード"),
    CONSTRAINT uk_棚卸明細_棚卸_行 UNIQUE ("棚卸ID", "棚卸行番号")
);

CREATE INDEX idx_棚卸明細_商品コード ON "棚卸明細データ"("商品コード");

-- 支払データ
CREATE TABLE "支払データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "支払番号" VARCHAR(20) UNIQUE NOT NULL,
    "仕入先コード" VARCHAR(20) NOT NULL,
    "支払締日" DATE NOT NULL,
    "支払予定日" DATE NOT NULL,
    "支払方法" VARCHAR(20) NOT NULL,
    "支払金額" DECIMAL(15, 2) NOT NULL,
    "消費税額" DECIMAL(15, 2) NOT NULL,
    "源泉徴収額" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "差引支払額" DECIMAL(15, 2) NOT NULL,
    "支払実行日" DATE,
    "ステータス" VARCHAR(20) DEFAULT '作成中' NOT NULL,
    "振込先銀行コード" VARCHAR(10),
    "振込先支店コード" VARCHAR(10),
    "振込先口座種別" VARCHAR(10),
    "振込先口座番号" VARCHAR(20),
    "振込先口座名義" VARCHAR(100),
    "備考" CLOB,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新者" VARCHAR(50),
    CONSTRAINT fk_支払_仕入先 FOREIGN KEY ("仕入先コード") REFERENCES "取引先マスタ"("取引先コード")
);

CREATE INDEX idx_支払_仕入先コード ON "支払データ"("仕入先コード");
CREATE INDEX idx_支払_支払締日 ON "支払データ"("支払締日");
CREATE INDEX idx_支払_支払予定日 ON "支払データ"("支払予定日");
CREATE INDEX idx_支払_ステータス ON "支払データ"("ステータス");

-- 支払明細データ
CREATE TABLE "支払明細データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "支払ID" INTEGER NOT NULL,
    "支払行番号" INTEGER NOT NULL,
    "仕入番号" VARCHAR(20) NOT NULL,
    "仕入日" DATE NOT NULL,
    "仕入金額" DECIMAL(15, 2) NOT NULL,
    "消費税額" DECIMAL(15, 2) NOT NULL,
    "支払対象金額" DECIMAL(15, 2) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_支払明細_支払 FOREIGN KEY ("支払ID") REFERENCES "支払データ"("ID") ON DELETE CASCADE,
    CONSTRAINT fk_支払明細_仕入 FOREIGN KEY ("仕入番号") REFERENCES "仕入データ"("仕入番号"),
    CONSTRAINT uk_支払明細_支払_行 UNIQUE ("支払ID", "支払行番号")
);

CREATE INDEX idx_支払明細_仕入番号 ON "支払明細データ"("仕入番号");

-- 買掛金残高データ
CREATE TABLE "買掛金残高データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "仕入先コード" VARCHAR(20) NOT NULL,
    "年月" DATE NOT NULL,
    "前月残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "当月仕入高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "当月支払高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "当月残高" DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    "バージョン" INTEGER DEFAULT 1 NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_買掛金残高_仕入先 FOREIGN KEY ("仕入先コード") REFERENCES "取引先マスタ"("取引先コード"),
    UNIQUE ("仕入先コード", "年月")
);

CREATE INDEX idx_買掛金残高_年月 ON "買掛金残高データ"("年月");

-- 支払予定データ
CREATE TABLE "支払予定データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "仕入先コード" VARCHAR(20) NOT NULL,
    "支払予定日" DATE NOT NULL,
    "支払予定額" DECIMAL(15, 2) NOT NULL,
    "支払方法" VARCHAR(20) NOT NULL,
    "支払済フラグ" BOOLEAN DEFAULT FALSE NOT NULL,
    "支払ID" INTEGER,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_支払予定_仕入先 FOREIGN KEY ("仕入先コード") REFERENCES "取引先マスタ"("取引先コード"),
    CONSTRAINT fk_支払予定_支払 FOREIGN KEY ("支払ID") REFERENCES "支払データ"("ID")
);

CREATE INDEX idx_支払予定_支払予定日 ON "支払予定データ"("支払予定日");
CREATE INDEX idx_支払予定_支払済 ON "支払予定データ"("支払済フラグ");

-- 赤黒処理履歴データ
CREATE TABLE "赤黒処理履歴データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "処理番号" VARCHAR(20) UNIQUE NOT NULL,
    "処理日時" TIMESTAMP NOT NULL,
    "伝票種別" VARCHAR(20) NOT NULL,
    "元伝票番号" VARCHAR(20) NOT NULL,
    "赤伝票番号" VARCHAR(20) NOT NULL,
    "黒伝票番号" VARCHAR(20),
    "処理理由" CLOB NOT NULL,
    "処理者" VARCHAR(50) NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_赤黒処理履歴_元伝票番号 ON "赤黒処理履歴データ"("元伝票番号");
CREATE INDEX idx_赤黒処理履歴_処理日時 ON "赤黒処理履歴データ"("処理日時");

-- 採番マスタ
CREATE TABLE "採番マスタ" (
    "採番コード" VARCHAR(20) PRIMARY KEY,
    "採番名" VARCHAR(100) NOT NULL,
    "プレフィックス" VARCHAR(10) NOT NULL,
    "採番形式" VARCHAR(20) NOT NULL,
    "桁数" INTEGER NOT NULL,
    "現在値" BIGINT DEFAULT 0 NOT NULL,
    "最終採番日" DATE,
    "リセット対象" BOOLEAN DEFAULT false NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 採番履歴データ
CREATE TABLE "採番履歴データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "採番コード" VARCHAR(20) NOT NULL,
    "採番年月" VARCHAR(8) NOT NULL,
    "最終番号" BIGINT NOT NULL,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_採番履歴_採番マスタ FOREIGN KEY ("採番コード") REFERENCES "採番マスタ"("採番コード"),
    UNIQUE ("採番コード", "採番年月")
);

CREATE INDEX idx_採番履歴_採番年月 ON "採番履歴データ"("採番年月");

-- 変更履歴データ
CREATE TABLE "変更履歴データ" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "テーブル名" VARCHAR(100) NOT NULL,
    "レコードID" VARCHAR(100) NOT NULL,
    "操作種別" VARCHAR(10) NOT NULL,
    "変更日時" TIMESTAMP NOT NULL,
    "変更者" VARCHAR(50) NOT NULL,
    "変更前データ" CLOB,
    "変更後データ" CLOB,
    "変更理由" CLOB,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_変更履歴_テーブル名 ON "変更履歴データ"("テーブル名");
CREATE INDEX idx_変更履歴_レコードID ON "変更履歴データ"("レコードID");
CREATE INDEX idx_変更履歴_変更日時 ON "変更履歴データ"("変更日時");

-- 商品マスタ履歴
CREATE TABLE "商品マスタ履歴" (
    "ID" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "商品コード" VARCHAR(20) NOT NULL,
    "有効開始日" DATE NOT NULL,
    "有効終了日" DATE,
    "商品名" VARCHAR(100) NOT NULL,
    "商品区分" VARCHAR(20) NOT NULL,
    "単価" DECIMAL(15, 2),
    "税区分" VARCHAR(10),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "作成者" VARCHAR(50),
    UNIQUE ("商品コード", "有効開始日")
);

CREATE INDEX idx_商品マスタ履歴_商品コード ON "商品マスタ履歴"("商品コード");
CREATE INDEX idx_商品マスタ履歴_有効期間 ON "商品マスタ履歴"("有効開始日", "有効終了日");

-- 有効在庫ビュー
CREATE VIEW "有効在庫ビュー" AS
SELECT
    "倉庫コード",
    "商品コード",
    "ロケーションコード",
    "現在庫数",
    "引当数",
    "現在庫数" - "引当数" AS "有効在庫数",
    "発注残数",
    "現在庫数" - "引当数" + "発注残数" AS "予定在庫数"
FROM "在庫データ";
