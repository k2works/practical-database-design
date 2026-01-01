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
    "顧客請求区分" 請求区分 DEFAULT '締め',
    "顧客締日1" INTEGER,
    "顧客支払月1" INTEGER,
    "顧客支払日1" INTEGER,
    "顧客支払方法1" 支払方法,
    "顧客締日2" INTEGER,
    "顧客支払月2" INTEGER,
    "顧客支払日2" INTEGER,
    "顧客支払方法2" 支払方法,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "作成者名" VARCHAR(50),
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(50),
    PRIMARY KEY ("顧客コード", "顧客枝番")
);

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

-- インデックス
CREATE INDEX idx_取引先マスタ_取引先グループ ON "取引先マスタ"("取引先グループコード");
CREATE INDEX idx_顧客マスタ_請求先 ON "顧客マスタ"("請求先コード", "請求先枝番");
