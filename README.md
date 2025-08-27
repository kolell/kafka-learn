# Kafka Learn

本項目用於研究 Apache Kafka 的異步消息與同步消息機制，基於最新版本的 Kafka。

## 📋 主要內容
- Kafka 最新版本依賴配置 (Kafka 3.7.0)
- 同步消息生產與消費示例
- 異步消息生產與消費示例
- 性能對比和調試工具
- 詳細的源碼分析和調試指南

## 🚀 快速開始

### 1. 克隆項目
```bash
git clone https://github.com/kolell/kafka-learn.git
cd kafka-learn
```

### 2. 啟動 Kafka 環境
```bash
# 使用 Docker (推薦)
docker-compose up -d

# 創建測試主題
docker exec kafka kafka-topics --create --topic test-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

### 3. 編譯並運行
```bash
mvn compile

# 運行同步生產者示例
mvn exec:java -Dexec.mainClass="com.example.kafka.SyncProducerExample"

# 運行異步生產者示例  
mvn exec:java -Dexec.mainClass="com.example.kafka.AsyncProducerExample"

# 運行性能對比
mvn exec:java -Dexec.mainClass="com.example.kafka.SyncVsAsyncComparison"
```

## 🔍 調試功能

項目包含完整的 VS Code 調試配置，可以深入跟蹤 Kafka 客户端源碼：

1. **同步發送調試**：觀察 `future.get()` 如何阻塞線程
2. **異步發送調試**：觀察 Callback 在後台線程的執行
3. **性能對比**：實際測試同步與異步的性能差異

### 關鍵調試點
- `producer.send()` 方法的返回值
- `Future.get()` 的阻塞行為  
- `Callback.onCompletion()` 的異步執行
- `RecordMetadata` 的內容和時序

## 📁 項目結構
```
kafka-learn/
├── src/main/java/com/example/kafka/
│   ├── SyncProducerExample.java      # 同步發送示例
│   ├── AsyncProducerExample.java     # 異步發送示例
│   ├── ConsumerExample.java          # 消費者示例
│   └── SyncVsAsyncComparison.java    # 性能對比
├── .vscode/launch.json               # VS Code 調試配置
├── docker-compose.yml               # Kafka Docker 環境
├── docs/                            # 詳細文檔
│   ├── debug_guide.md               # 調試指南
│   ├── quick_start.md               # 快速開始
│   └── kafka_usage.md               # 使用說明
└── README.md
```

## 📖 核心概念

### 同步發送
- `producer.send(record).get()` 會阻塞主線程
- 等待每條消息確認後才繼續
- 適合對順序和可靠性要求極高的場景

### 異步發送  
- `producer.send(record, callback)` 立即返回
- 使用 Callback 處理發送結果
- 適合高吞吐量場景

## 🛠 依賴技術

- **Java 8+**
- **Apache Kafka 3.7.0** (最新版本)
- **Maven** 構建工具
- **Docker & Docker Compose** (可選)

## 📚 學習資源

- [Kafka 官方文檔](https://kafka.apache.org/documentation/)
- [Kafka Clients API](https://kafka.apache.org/37/javadoc/index.html)
- 項目內置調試指南：`docs/debug_guide.md`

## 🤝 貢獻

歡迎提交 PR 或 issue 共同完善項目！

## 📄 許可證

MIT License