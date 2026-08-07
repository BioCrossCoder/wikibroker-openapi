.PHONY: \
	init-js build-js test-js cloc-js \
	init-go build-go test-go cloc-go \
	init-py build-py test-py cloc-py \
	init-java build-java test-java cloc-java \
	init-php build-php test-php cloc-php \
	init-cs build-cs test-cs cloc-cs \
	init-dart build-dart test-dart cloc-dart \
	init-sw build-sw test-sw cloc-sw \
	init-rs build-rs test-rs cloc-rs \
	init-kt build-kt test-kt cloc-kt \
	build test cloc doc

init-js:
	@cd javascript-sdk && pnpm install

init-go:
	@cd golang-sdk && go mod tidy

init-py:
	@cd python-sdk && uv sync --all-groups

init-java:
	@cd java-sdk && mvn install

init-php:
	@cd php-sdk && composer install

init-cs:
	@cd dotnet-sdk && dotnet restore ./src && dotnet restore ./tests

init-dart:
	@cd dart-sdk && dart pub get

init-sw:
	@cd swift-sdk && swift package resolve

init-rs:
	@cd rust-sdk && cargo fetch

init-kt:
	@cd kotlin-sdk && ./gradlew dependencies

build:
	@echo "清理已构建 SDK 包..."
	@echo ""
	@rm -rf wikibroker*.tgz wikibroker*.whl wikibroker*.jar wikibroker*.zip WikiBroker*.nupkg
	@echo ""
	@echo "开始构建所有 SDK ..."
	@echo ""
	@echo "[1/10] 开始构建 JavaScript SDK..." && $(MAKE) build-js && echo "[1/10] JavaScript SDK 构建完成 ✓"
	@echo "[2/10] 开始构建 Go SDK..." && $(MAKE) build-go && echo "[2/10] Go SDK 构建完成 ✓"
	@echo "[3/10] 开始构建 Python SDK..." && $(MAKE) build-py && echo "[3/10] Python SDK 构建完成 ✓"
	@echo "[4/10] 开始构建 Java SDK..." && $(MAKE) build-java && echo "[4/10] Java SDK 构建完成 ✓"
	@echo "[5/10] 开始构建 PHP SDK..." && $(MAKE) build-php && echo "[5/10] PHP SDK 构建完成 ✓"
	@echo "[6/10] 开始构建 .NET SDK..." && $(MAKE) build-cs && echo "[6/10] .NET SDK 构建完成 ✓"
	@echo "[7/10] 开始构建 Dart SDK..." && $(MAKE) build-dart && echo "[7/10] Dart SDK 构建完成 ✓"
	@echo "[8/10] 开始构建 Swift SDK..." && $(MAKE) build-sw && echo "[8/10] Swift SDK 构建完成 ✓"
	@echo "[9/10] 开始构建 Rust SDK..." && $(MAKE) build-rs && echo "[9/10] Rust SDK 构建完成 ✓"
	@echo "[10/10] 开始构建 Kotlin SDK..." && $(MAKE) build-kt && echo "[10/10] Kotlin SDK 构建完成 ✓"
	@echo ""
	@echo "所有SDK构建成功！"

build-js: init-js
	@cd javascript-sdk && rm -rf dist && pnpm pack && rename 's/openapi-sdk/openapi-js-sdk/' wikibroker-*.tgz && mv wikibroker-*.tgz ..

GO_SDK_VERSION = 1.0.1

build-go:
	@cp -r golang-sdk wikibroker_openapi_sdk && tar zcf wikibroker-openapi-go-sdk-$(GO_SDK_VERSION).tgz wikibroker_openapi_sdk && rm -rf wikibroker_openapi_sdk

build-py: init-py
	@cd python-sdk && rm -rf dist && uv build && mv dist/wikibroker*.whl ..

build-java: init-java
	@cd java-sdk && rm -rf target && mvn package && mv target/wikibroker*.jar ..

build-php: init-php
	@cd php-sdk && composer build && rename 's/wikiglobal-wikibroker-openapi-sdk/wikibroker-openapi-php-sdk/' wikiglobal*.zip && mv wikibroker*.zip ..

build-cs:
	@cd dotnet-sdk/src && rm -rf bin/ obj/ && dotnet pack && mv bin/Release/WikiBroker*.nupkg ../..

SWIFT_SDK_VERSION = 0.1.0-alpha

build-sw:
	@cd swift-sdk && cp -r . WikibrokerOpenapiSdk && swift package archive-source --package-path=WikibrokerOpenapiSdk && zip -d WikibrokerOpenapiSdk/WikibrokerOpenapiSdk.zip "WikibrokerOpenapiSdk/.*/*" && mv WikibrokerOpenapiSdk/WikibrokerOpenapiSdk.zip ../wikibroker-openapi-swift-sdk-$(SWIFT_SDK_VERSION).zip && rm -rf WikibrokerOpenapiSdk/

DART_SDK_VERSION := $(shell yq '.version' dart-sdk/pubspec.yaml)

build-dart:
	@cp -r dart-sdk wikibroker_openapi_sdk && cd wikibroker_openapi_sdk && rm -rf .dart_tool .idea *.iml && cd .. && tar zcf wikibroker-openapi-dart-sdk-$(DART_SDK_VERSION).tgz wikibroker_openapi_sdk && rm -rf wikibroker_openapi_sdk

RUST_SDK_VERSION := $(shell grep '^version = ' rust-sdk/Cargo.toml | head -1 | sed 's/version = "\(.*\)"/\1/')

build-rs:
	@cp -r rust-sdk wikibroker_openapi_sdk && cd wikibroker_openapi_sdk && rm -rf target .gitignore && cd .. && tar zcf wikibroker-openapi-rust-sdk-$(RUST_SDK_VERSION).tgz wikibroker_openapi_sdk && rm -rf wikibroker_openapi_sdk

KOTLIN_SDK_VERSION := $(shell grep '^version = ' kotlin-sdk/build.gradle.kts | head -1 | sed 's/version = "\(.*\)"/\1/')

build-kt:
	@cd kotlin-sdk && ./gradlew clean jar && mv build/libs/wikibroker-openapi-sdk-$(KOTLIN_SDK_VERSION).jar ../wikibroker-openapi-kotlin-sdk-$(KOTLIN_SDK_VERSION).jar

test:
	@echo "运行所有 SDK 测试..."
	@echo "[1/10] 运行 JavaScript SDK 测试..." && $(MAKE) test-js && echo "[1/10] JavaScript SDK 测试通过 ✓"
	@echo "[2/10] 运行 Go SDK 测试..." && $(MAKE) test-go && echo "[2/10] Go SDK 测试通过 ✓"
	@echo "[3/10] 运行 Python SDK 测试..." && $(MAKE) test-py && echo "[3/10] Python SDK 测试通过 ✓"
	@echo "[4/10] 运行 Java SDK 测试..." && $(MAKE) test-java && echo "[4/10] Java SDK 测试通过 ✓"
	@echo "[5/10] 运行 PHP SDK 测试..." && $(MAKE) test-php && echo "[5/10] PHP SDK 测试通过 ✓"
	@echo "[6/10] 运行 .NET SDK 测试..." && $(MAKE) test-cs && echo "[6/10] .NET SDK 测试通过 ✓"
	@echo "[7/10] 运行 Dart SDK 测试..." && $(MAKE) test-dart && echo "[7/10] Dart SDK 测试通过 ✓"
	@echo "[8/10] 运行 Swift SDK 测试..." && $(MAKE) test-sw && echo "[8/10] Swift SDK 测试通过 ✓"
	@echo "[9/10] 运行 Rust SDK 测试..." && $(MAKE) test-rs && echo "[9/10] Rust SDK 测试通过 ✓"
	@echo "[10/10] 运行 Kotlin SDK 测试..." && $(MAKE) test-kt && echo "[10/10] Kotlin SDK 测试通过 ✓"
	@echo "所有 SDK 测试完成！"

test-js: init-js
	@cd javascript-sdk && pnpm test

test-go: init-go
	@cd golang-sdk && go test -v

test-py: init-py
	@cd python-sdk && uv run pytest

test-java: init-java
	@cd java-sdk && mvn test

test-php: init-php
	@cd php-sdk && composer test

test-cs:
	@cd dotnet-sdk/tests && dotnet test

test-dart: init-dart
	@cd dart-sdk && dart test

test-sw:
	@cd swift-sdk && xcrun swift test

test-rs:
	@cd rust-sdk && cargo test

test-kt:
	@cd kotlin-sdk && ./gradlew test

doc:
	@echo "生成可视化文档..."
	@cd docs && npx @redocly/cli build-docs openapi.json -o index.html
	@echo "可视化文档生成完成！"


cloc:
	@echo "统计所有 SDK 核心逻辑代码行数..."; \
	TS=$$($(MAKE) cloc-js | grep TypeScript | awk '{print $$1 "\t" $$5}'); \
	GO=$$($(MAKE) cloc-go | grep "Go  " | awk '{print $$1 "\t" $$5}'); \
	PY=$$($(MAKE) cloc-py | grep "Python  " | awk '{print $$1 "\t" $$5}'); \
	JAVA=$$($(MAKE) cloc-java | grep "Java  " | awk '{print $$1 "\t" $$5}'); \
	PHP=$$($(MAKE) cloc-php | grep "PHP  " | awk '{print $$1 "\t" $$5}'); \
	CS=$$($(MAKE) cloc-cs | grep "C#  " | awk '{print $$1 "\t" $$5}'); \
	DART=$$($(MAKE) cloc-dart | grep "Dart  " | awk '{print $$1 "\t" $$5}'); \
	SW=$$($(MAKE) cloc-sw | grep "Swift  " | awk '{print $$1 "\t" $$5}'); \
	RS=$$($(MAKE) cloc-rs | grep "Rust  " | awk '{print $$1 "\t" $$5}'); \
	KT=$$($(MAKE) cloc-kt | grep "Kotlin  " | awk '{print $$1 "\t" $$5}'); \
	printf "%-15s %15s\n" $$TS $$GO $$PY $$JAVA $$PHP $$CS $$DART $$SW $$RS $$KT
	@echo "所有 SDK 代码行数统计完毕！"

cloc-js:
	@echo "统计 JavaScript SDK 核心逻辑代码行数..."
	@cd javascript-sdk && cloc src/core.ts

cloc-go:
	@echo "统计 Go SDK 核心逻辑代码行数..."
	@cd golang-sdk && cloc core.go

cloc-py:
	@echo "统计 Python SDK 核心逻辑代码行数..."
	@cd python-sdk && cloc src/wikibroker_openapi_sdk/core.py

cloc-java:
	@echo "统计 Java SDK 核心逻辑代码行数..."
	@cd java-sdk && cloc src/main/java/com/wikiglobal/wikibroker/openapi/Core.java

cloc-php:
	@echo "统计 PHP SDK 核心逻辑代码行数..."
	@cd php-sdk && cloc src/Core.php

cloc-cs:
	@echo "统计 .NET SDK 核心逻辑代码行数..."
	@cd dotnet-sdk && cloc src/Core.cs

cloc-dart:
	@echo "统计 Dart SDK 核心逻辑代码行数..."
	@cd dart-sdk && cloc lib/src/core.dart

cloc-sw:
	@echo "统计 Swift SDK 核心逻辑代码行数..."
	@cd swift-sdk && cloc Sources/WikibrokerOpenapiSdk/Core.swift

cloc-rs:
	@echo "统计 Rust SDK 核心逻辑代码行数..."
	@cd rust-sdk && cloc src/core.rs

cloc-kt:
	@echo "统计 Kotlin SDK 核心逻辑代码行数..."
	@cd kotlin-sdk && cloc src/main/kotlin/com/wikiglobal/wikibroker/openapi/Core.kt