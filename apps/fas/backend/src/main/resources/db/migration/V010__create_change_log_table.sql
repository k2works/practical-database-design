-- V010__create_change_log_table.sql
-- 変更ログテーブル（マスタデータの変更履歴を保存）

-- 操作種別 ENUM
CREATE TYPE 操作種別 AS ENUM ('INSERT', 'UPDATE', 'DELETE');

-- 変更ログテーブル
CREATE TABLE "変更ログ" (
    "ログID"           SERIAL PRIMARY KEY,
    "テーブル名"        VARCHAR(50) NOT NULL,
    "レコードキー"      VARCHAR(100) NOT NULL,
    "操作種別"         操作種別 NOT NULL,
    "操作前データ"      JSONB,
    "操作後データ"      JSONB,
    "操作日時"         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "操作者"           VARCHAR(50),
    "操作端末"         VARCHAR(100),
    "備考"            TEXT
);

-- インデックス
CREATE INDEX "idx_変更ログ_テーブル名" ON "変更ログ"("テーブル名");
CREATE INDEX "idx_変更ログ_レコードキー" ON "変更ログ"("レコードキー");
CREATE INDEX "idx_変更ログ_操作日時" ON "変更ログ"("操作日時");
CREATE INDEX "idx_変更ログ_操作種別" ON "変更ログ"("操作種別");
CREATE INDEX "idx_変更ログ_操作者" ON "変更ログ"("操作者");

-- 勘定科目マスタの変更ログトリガー関数
CREATE OR REPLACE FUNCTION log_account_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO "変更ログ" (
            "テーブル名", "レコードキー", "操作種別",
            "操作前データ", "操作後データ", "操作者"
        ) VALUES (
            '勘定科目マスタ',
            NEW."勘定科目コード",
            'INSERT',
            NULL,
            row_to_json(NEW)::jsonb,
            NEW."更新者名"
        );
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO "変更ログ" (
            "テーブル名", "レコードキー", "操作種別",
            "操作前データ", "操作後データ", "操作者"
        ) VALUES (
            '勘定科目マスタ',
            NEW."勘定科目コード",
            'UPDATE',
            row_to_json(OLD)::jsonb,
            row_to_json(NEW)::jsonb,
            NEW."更新者名"
        );
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO "変更ログ" (
            "テーブル名", "レコードキー", "操作種別",
            "操作前データ", "操作後データ", "操作者"
        ) VALUES (
            '勘定科目マスタ',
            OLD."勘定科目コード",
            'DELETE',
            row_to_json(OLD)::jsonb,
            NULL,
            OLD."更新者名"
        );
        RETURN OLD;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- トリガー作成
CREATE TRIGGER "trg_勘定科目マスタ_変更ログ"
AFTER INSERT OR UPDATE OR DELETE ON "勘定科目マスタ"
FOR EACH ROW
EXECUTE FUNCTION log_account_changes();

COMMENT ON TABLE "変更ログ" IS 'マスタデータの変更履歴を保存するテーブル';
COMMENT ON COLUMN "変更ログ"."ログID" IS 'ログの一意識別子';
COMMENT ON COLUMN "変更ログ"."テーブル名" IS '変更対象のテーブル名';
COMMENT ON COLUMN "変更ログ"."レコードキー" IS '変更対象レコードの主キー値';
COMMENT ON COLUMN "変更ログ"."操作種別" IS 'INSERT/UPDATE/DELETE';
COMMENT ON COLUMN "変更ログ"."操作前データ" IS '変更前のレコード内容（JSON形式）';
COMMENT ON COLUMN "変更ログ"."操作後データ" IS '変更後のレコード内容（JSON形式）';
COMMENT ON COLUMN "変更ログ"."操作日時" IS '操作実行日時';
COMMENT ON COLUMN "変更ログ"."操作者" IS '操作を実行したユーザー';
COMMENT ON COLUMN "変更ログ"."操作端末" IS '操作を実行した端末情報';
COMMENT ON COLUMN "変更ログ"."備考" IS '任意の備考';
