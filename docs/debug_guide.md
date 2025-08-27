# Kafka 同步與異步消息調試指南

## 調試環境設置

### 1. 調試配置
已在 `.vscode/launch.json` 中配置了四個調試選項：
- **Debug Sync Producer**: 調試同步生產者
- **Debug Async Producer**: 調試異步生產者  
- **Debug Consumer**: 調試消費者
- **Debug Sync vs Async Comparison**: 調試性能對比

### 2. 關鍵調試點

#### 同步發送 (SyncProducerExample.java)
設置斷點在以下位置來理解同步發送原理：

```java
// 斷點 1: 查看 send() 方法返回的 Future 對象
Future<RecordMetadata> future = producer.send(record);

// 斷點 2: 觀察 get() 方法如何阻塞線程
RecordMetadata metadata = future.get(); // 這裡會阻塞
```

**關鍵原理**：
- `producer.send()` 返回一個 `Future<RecordMetadata>`
- 調用 `future.get()` 會阻塞當前線程，直到消息發送完成
- 網絡 I/O 和序列化都在後台線程處理，但主線程會等待結果

#### 異步發送 (AsyncProducerExample.java)
設置斷點在以下位置來理解異步發送原理：

```java
// 斷點 1: 觀察 send() 方法立即返回
Future<RecordMetadata> future = producer.send(record, callback);

// 斷點 2: 在 Callback 的 onCompletion 方法中
public void onCompletion(RecordMetadata metadata, Exception exception) {
    // 這個方法在後台線程中執行
}
```

**關鍵原理**：
- `producer.send()` 立即返回，不阻塞主線程
- 消息被放入內部緩衝區，由後台線程批量發送
- 當發送完成時，在後台線程中調用 Callback

## 深入源碼理解

### KafkaProducer 內部機制

1. **RecordAccumulator**: 消息累加器，緩存待發送的消息
2. **Sender 線程**: 後台線程，負責實際的網絡發送
3. **Future 和 Callback**: 處理同步/異步結果

### 調試步驟

1. **啟動 Kafka 服務**（必須）
   ```bash
   # 使用 Docker
   docker-compose up -d
   
   # 創建主題
   docker exec kafka kafka-topics --create --topic test-topic --bootstrap-server localhost:9092
   ```

2. **編譯項目**
   ```bash
   mvn compile
   ```

3. **設置斷點並調試**
   - 在 VS Code 中打開要調試的 Java 文件
   - 在關鍵行設置斷點（點擊行號左側）
   - 按 F5 或選擇對應的調試配置運行

4. **觀察調試變量**
   - 查看 `Future` 對象的狀態
   - 觀察 `RecordMetadata` 的內容
   - 監控線程執行順序

## 性能對比

運行 `SyncVsAsyncComparison.java` 可以看到：
- 同步發送：每條消息都會阻塞等待結果
- 異步發送：所有消息立即發送，回調並行處理

## 實際應用建議

- **同步發送**：適合對消息順序和可靠性要求極高的場景
- **異步發送**：適合高吞吐量場景，可以批量處理和並行發送