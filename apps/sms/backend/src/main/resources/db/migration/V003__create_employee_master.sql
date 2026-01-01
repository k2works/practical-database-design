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
