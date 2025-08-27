package com.example.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;

public class SyncProducerExample {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Kafka 同步消息發送示例 ===");
        
        // 配置 Producer 屬性
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // 可選：增加調試相關配置
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // 等待所有副本確認
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        
        System.out.println("1. 創建 KafkaProducer...");
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        
        System.out.println("2. 創建 ProducerRecord...");
        ProducerRecord<String, String> record = new ProducerRecord<>("test-topic", "sync-key", "同步消息內容");
        
        System.out.println("3. 調用 send().get() 進行同步發送...");
        long startTime = System.currentTimeMillis();
        
        // 關鍵點：send() 返回 Future，調用 get() 會阻塞等待結果
        Future<RecordMetadata> future = producer.send(record);
        System.out.println("4. Future 對象已創建，準備調用 get() 方法...");
        
        // 這裡會阻塞直到消息發送完成或失敗
        RecordMetadata metadata = future.get();
        
        long endTime = System.currentTimeMillis();
        System.out.printf("5. 同步消息發送完成！分區: %d, offset: %d, 耗時: %d ms\n", 
                         metadata.partition(), metadata.offset(), (endTime - startTime));
        
        System.out.println("6. 關閉 Producer...");
        producer.close();
        System.out.println("=== 同步發送完成 ===");
    }
}