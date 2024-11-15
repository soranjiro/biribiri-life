# ベースイメージとしてGradleを使用
FROM gradle:7.2-jdk11 AS build

# 作業ディレクトリを設定
WORKDIR /app

# Gradleビルドファイルとソースコードをコピー
COPY build.gradle settings.gradle /app/
COPY src /app/src

# 依存関係をダウンロードしてビルド
RUN gradle build

# 実行用のベースイメージとしてOpenJDKを使用
FROM openjdk:11-jre-slim

# 作業ディレクトリを設定
WORKDIR /app

# ビルド成果物をコピー
COPY --from=build /app/build/libs/biribiri-life-0.0.1-SNAPSHOT.jar /app/app.jar

# アプリケーションを実行
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
