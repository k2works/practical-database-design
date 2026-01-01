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
