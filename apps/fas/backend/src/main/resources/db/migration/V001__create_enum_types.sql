-- 財務会計システム ENUM 型定義

-- BSPL区分（貸借対照表/損益計算書区分）
CREATE TYPE "BSPL区分" AS ENUM ('BS', 'PL');

-- 貸借区分
CREATE TYPE "貸借区分" AS ENUM ('借方', '貸方');

-- 集計区分
CREATE TYPE "集計区分" AS ENUM ('集計', '明細');

-- 課税区分
CREATE TYPE "課税区分" AS ENUM ('課税', '非課税', '免税', '対象外');

-- 仕訳区分
CREATE TYPE "仕訳区分" AS ENUM ('通常', '振替', '決算', '自動');

-- 仕訳ステータス
CREATE TYPE "仕訳ステータス" AS ENUM ('入力中', '確定', '承認済', '取消');

-- 自動仕訳パターン区分
CREATE TYPE "自動仕訳パターン区分" AS ENUM ('売上', '仕入', '入金', '出金', '減価償却', '引当');

-- 決算区分
CREATE TYPE "決算区分" AS ENUM ('月次', '四半期', '中間', '年次');
