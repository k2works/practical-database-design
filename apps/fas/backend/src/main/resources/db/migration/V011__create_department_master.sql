-- 部門マスタテーブル
-- D社の組織構成（本部-部-課の3階層構造）を管理

CREATE TABLE "部門マスタ" (
    "部門コード" VARCHAR(5) PRIMARY KEY,
    "部門名" VARCHAR(100) NOT NULL,
    "部門略名" VARCHAR(20),
    "組織階層" INTEGER NOT NULL CHECK ("組織階層" >= 0 AND "組織階層" <= 3),
    "部門パス" VARCHAR(100) NOT NULL,
    "最下層フラグ" INTEGER NOT NULL DEFAULT 0 CHECK ("最下層フラグ" IN (0, 1)),
    "作成日時" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "更新日時" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- インデックス
CREATE INDEX idx_department_organization_level ON "部門マスタ" ("組織階層");
CREATE INDEX idx_department_path ON "部門マスタ" ("部門パス");
CREATE INDEX idx_department_lowest_level ON "部門マスタ" ("最下層フラグ");

COMMENT ON TABLE "部門マスタ" IS 'D社の組織構成を管理するマスタテーブル';
COMMENT ON COLUMN "部門マスタ"."部門コード" IS '部門を一意に識別する5桁コード';
COMMENT ON COLUMN "部門マスタ"."部門名" IS '部門の正式名称';
COMMENT ON COLUMN "部門マスタ"."部門略名" IS '部門の略称（帳票出力用）';
COMMENT ON COLUMN "部門マスタ"."組織階層" IS '0:全社, 1:本部, 2:部, 3:課';
COMMENT ON COLUMN "部門マスタ"."部門パス" IS '上位部門からのパス（チルダ連結）';
COMMENT ON COLUMN "部門マスタ"."最下層フラグ" IS '0:中間階層, 1:最下層（仕訳可能）';
