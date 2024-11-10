FROM openjdk:11-slim

# 作業ディレクトリを設定
WORKDIR /app

# Javaファイルをコピー
COPY Main.java /app

# Javaファイルをコンパイル
RUN javac Main.java

# ポート8080を公開
EXPOSE 8080

# アプリケーションを実行
CMD ["java", "Main"]
