FROM schemaspy/schemaspy:latest

# 日本語フォントをインストール
USER root
RUN apt-get update && apt-get install -y fonts-noto-cjk && rm -rf /var/lib/apt/lists/*
USER java
