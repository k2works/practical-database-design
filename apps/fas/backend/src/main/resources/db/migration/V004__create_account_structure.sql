-- 財務会計システム 勘定科目構成マスタ作成
-- chapter15: 勘定科目の設計（チルダ連結方式）

-- 勘定科目構成マスタ
CREATE TABLE "勘定科目構成マスタ" (
    "勘定科目コード" VARCHAR(5) PRIMARY KEY REFERENCES "勘定科目マスタ"("勘定科目コード"),
    "勘定科目パス" VARCHAR(100) NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "更新者名" VARCHAR(12)
);

-- インデックス（パス検索用）
CREATE INDEX idx_勘定科目構成マスタ_パス ON "勘定科目構成マスタ"("勘定科目パス");

-- チルダ連結方式のパス検索用コメント
COMMENT ON TABLE "勘定科目構成マスタ" IS '勘定科目の階層構造をチルダ（^）連結で表現するマスタ。LIKE検索で効率的に階層検索が可能。';
COMMENT ON COLUMN "勘定科目構成マスタ"."勘定科目パス" IS '階層パス。例: 11^11000^11190^11110（資産の部→流動資産→現金及び預金→現金）';
