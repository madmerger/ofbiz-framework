# Spring Boot コンポーネント移行ガイド

## 目的と構成

各コンポーネントは独立した `migration/<comp>-springboot` Gradle サブプロジェクトとして実装する。共通の JPA モデルと汎用ランタイムには `migration:springboot-datamodel` と `migration:springboot-common` を依存させる。`settings.gradle` は `migration/*/build.gradle` を自動登録するため、コンポーネント追加時に編集しない。

HumanRes は先行パイロットであり、既存コードを `springboot-common` に載せ替える作業は後続とする。

## コンポーネント移行手順

1. 対象の Entity XML、Service XML、Java/Groovy、画面、権限、データを調べ、仕様・依存関係・レビュー記録を `docs/migration/<comp>/` に日本語でまとめる。
2. `migration/<comp>-springboot` を作り、共通モデルとランタイムに依存させる。
   ```groovy
   dependencies {
       implementation project(':migration:springboot-common')
       implementation project(':migration:springboot-datamodel')
   }
   ```
3. `ofbiz2spring.py` を Gradle の生成タスクから呼び、対象の entitymodel/service XML を `--entity-model` / `--service-def` として渡す。パッケージは `--entity-package`、`--repository-package`、`--service-package` で指定し、`--report` も build ディレクトリへ出す。実行時カタログを `build/generated/sources` に生成し、生成物はコミットしない。
4. 全 entity-auto サービスを登録し、業務上必要な simple service の主要フローを Java/Groovy に移植する。コンポーネント間の Java サービス呼び出しは直接結合せず、インターフェースを持つ Port と当該コンポーネントの adapter を用意する。
   ```java
   registry.registerHandler("ordermgr", "approveOrder", "UPDATE", input -> approve(input));
   registry.registerHandler("accounting", "findPayHistory", "VIEW", "PayHistory", input -> find(input));
   ```
   権限には `VIEW` / `UPDATE` などの action または `ORDERMGR_UPDATE` のような authority を指定する。省略時は `<service-permission-prefix>_ADMIN` のみ許可する。結果が機微 Entity を含む handler は Entity 名も登録し、handler 実行前の `HUMANRES_SENSITIVE_VIEW` 等の確認を有効にする。
5. `migration.security.base-permissions` と service catalog の `permissionMainAction` / invoke 権限を設定する。読み取り・書き込み対象の機微 Entity を `migration.security.sensitive-entities` に列挙し、正規 Entity 名による認可、秘密情報のマスキング、multi-service 全 rollback、HTTPS 要件をテストする。
6. H2 で Entity/Service/API のテストを実行し、必要な PostgreSQL/Testcontainers テストも用意する。local 起動スクリプトを追加し、Swagger UI の `Authorize` と主要 API を用いた E2E 手順を記録する。
7. spec、notes、review、テスト結果、未対応 Entity XML を `docs/migration/<comp>/` に記録する。PR の変更は対象コンポーネントに限定する。

## ローカル環境

- Java 17 と Gradle 9.8.2 を使う。OFBiz checkout に `gradle-wrapper.jar` が無い場合は、ルートで `gradle wrapper --gradle-version 9.8.2` を実行する。生成 JAR は gitignore 対象で、追跡ファイルは変更しない。
- Maven Central が HTTP 429 を返す場合は、環境に合わせた公開ミラーを `~/.gradle/init.d/mirror.gradle` に設定する。例えば Google の Maven Central mirror を先に解決先へ追加する内容は以下の通り。社内 mirror を使う場合は URL を置き換え、認証情報や環境固有設定はリポジトリへコミットしない。
  ```groovy
  allprojects {
      repositories {
          maven {
              name = 'CentralMirror'
              url = uri('https://maven-central.storage-download.googleapis.com/maven2')
          }
      }
  }
  ```
- Docker 29 で PostgreSQL/Testcontainers を実行する場合は、コンポーネントの `build.gradle` で Testcontainers を `1.21.4` 以上へ override する。
  ```groovy
  ext['testcontainers.version'] = '1.21.4'
  ```
- local profile の起動は `./gradlew :migration:<comp>-springboot:bootRun --args='--spring.profiles.active=local'` を基本とし、必要なデモパスワードは環境変数から渡す。Swagger UI は `http://localhost:8080/swagger-ui/index.html`、API docs は `/v3/api-docs` を使う。
- ブラウザテストに画面環境が必要なら `Xvfb :99 -screen 0 1280x720x24` を起動し、テストプロセスの `DISPLAY=:99` を設定する。
- Swagger UI と OpenAPI docs は `local` プロファイルのみ有効にする。運用プロファイルでは無効にし、HTTPS 終端の実プロキシを `MIGRATION_SECURITY_TRUSTED_PROXIES` に明示する。

## 成果物とレビュー

各コンポーネントの spec、notes、review、移行テスト手順は `docs/migration/<comp>/` に置く。受け入れ基準、未対応 Entity XML、view-entity、権限差分を明記する。生成時の Entity/Service カタログは build-time に再現可能であることを確認し、生成 Java ソースはコミットしない。
