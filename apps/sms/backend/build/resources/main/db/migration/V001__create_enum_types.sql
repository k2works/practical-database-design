-- 商品区分
CREATE TYPE 商品区分 AS ENUM ('商品', '製品', 'サービス', '諸口');

-- 取引先区分
CREATE TYPE 取引先区分 AS ENUM ('顧客', '仕入先', '両方');

-- 税区分
CREATE TYPE 税区分 AS ENUM ('外税', '内税', '非課税');

-- 請求区分
CREATE TYPE 請求区分 AS ENUM ('都度', '締め');

-- 支払方法
CREATE TYPE 支払方法 AS ENUM ('現金', '振込', '手形', '小切手', 'その他');
