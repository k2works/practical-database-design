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

-- 商品マスタ
CREATE TABLE "商品マスタ" (
    "商品コード" VARCHAR(20) PRIMARY KEY,
    "商品正式名" VARCHAR(200),
    "商品名" VARCHAR(100) NOT NULL,
    "商品名カナ" VARCHAR(200),
    "商品区分" 商品区分 NOT NULL DEFAULT '商品',
    "製品型番" VARCHAR(50),
    "販売単価" DECIMAL(15, 2) DEFAULT 0,
    "仕入単価" DECIMAL(15, 2) DEFAULT 0,
    "税区分" 税区分 NOT NULL DEFAULT '外税',
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

-- インデックス
CREATE INDEX idx_商品マスタ_商品区分 ON "商品マスタ"("商品区分");
CREATE INDEX idx_商品マスタ_商品分類コード ON "商品マスタ"("商品分類コード");
CREATE INDEX idx_商品分類マスタ_商品分類パス ON "商品分類マスタ"("商品分類パス");
