# Kafka 快速啟動指南

## 方式一：使用 Docker (推薦)

```bash
# 1. 克隆項目
git clone https://github.com/kolell/kafka-learn.git
cd kafka-learn

# 2. 啟動 Kafka 和 Zookeeper
docker-compose up -d

# 3. 等待服務啟動 (約30秒)
# 4. 創建主題
docker exec kafka kafka-topics --create --topic test-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# 5. 驗證主題創建
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

## 方式二：本地安裝

### 下載和啟動
1. 下載 Kafka: https://kafka.apache.org/downloads
2. 解壓到本地目錄
3. 啟動服務：

```bash
# 啟動 Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties
# Windows: bin\windows\zookeeper-server-start.bat config\zookeeper.properties

# 啟動 Kafka (新終端)
bin/kafka-server-start.sh config/server.properties  
# Windows: bin\windows\kafka-server-start.bat config\server.properties

# 創建主題 (新終端)
bin/kafka-topics.sh --create --topic test-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
# Windows: bin\windows\kafka-topics.bat --create --topic test-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

## 編譯和運行

```bash
# 編譯項目
mvn compile

# 運行同步生產者
mvn exec:java -Dexec.mainClass="com.example.kafka.SyncProducerExample"

# 運行異步生產者
mvn exec:java -Dexec.mainClass="com.example.kafka.AsyncProducerExample"

# 運行消費者
mvn exec:java -Dexec.mainClass="com.example.kafka.ConsumerExample"

# 運行性能對比
mvn exec:java -Dexec.mainClass="com.example.kafka.SyncVsAsyncComparison"
```

## 調試步驟

1. **確保 Kafka 運行**
   - 檢查 localhost:9092 是否可訪問
   - 確認 test-topic 主題已創建

2. **在 VS Code 中調試**
   - 打開要調試的 Java 文件
   - 在關鍵行設置斷點（點擊行號左側）
   - 按 F5 選擇對應的調試配置

3. **推薦的斷點位置**

   **同步發送 (SyncProducerExample.java)**：
   ```java
   Future<RecordMetadata> future = producer.send(record); // 斷點1
   RecordMetadata metadata = future.get(); // 斷點2 - 觀察阻塞
   ```

   **異步發送 (AsyncProducerExample.java)**：
   ```java
   producer.send(record, new Callback() { // 斷點1
       public void onCompletion(RecordMetadata metadata, Exception exception) {
           // 斷點2 - 觀察回調執行
       }
   });
   ```

4. **觀察要點**
   - **同步**：主線程在 `get()` 處阻塞等待
   - **異步**：主線程立即繼續，回調在後台線程執行
   - **性能差異**：運行 SyncVsAsyncComparison 查看時間差異

## 常見問題

1. **連接失敗**：確保 Kafka 在 localhost:9092 運行
2. **主題不存在**：手動創建 test-topic 主題
3. **編碼問題**：已在 pom.xml 中配置 UTF-8 支持
4. **Java 版本**：確保使用 Java 8 或更高版本