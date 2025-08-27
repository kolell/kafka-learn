package com.example.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.CountDownLatch;

public class AsyncProducerExample {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Kafka 異步消息發送示例 ===");
        
        // 配置 Producer 屬性
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // 異步配置優化
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5); // 增加批次等待時間
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        
        System.out.println("1. 創建 KafkaProducer...");
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        
        // 使用 CountDownLatch 來等待異步回調完成
        CountDownLatch latch = new CountDownLatch(1);
        
        System.out.println("2. 創建 ProducerRecord...");
        ProducerRecord<String, String> record = new ProducerRecord<>("test-topic", "async-key", "異步消息內容");
        
        System.out.println("3. 調用 send() 並提供 Callback 進行異步發送...");
        long startTime = System.currentTimeMillis();
        
        // 關鍵點：send() 立即返回 Future，不會阻塞主線程
        Future<RecordMetadata> future = producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                long callbackTime = System.currentTimeMillis();
                System.out.println("4. 進入異步回調方法...");
                
                if (exception == null) {
                    System.out.printf("5. 異步消息發送成功！分區: %d, offset: %d, 回調耗時: %d ms\n", 
                                     metadata.partition(), metadata.offset(), (callbackTime - startTime));
                } else {
                    System.err.println("5. 異步消息發送失敗：" + exception.getMessage());
                    exception.printStackTrace();
                }
                
                // 通知主線程回調已完成
                latch.countDown();
            }
        });
        
        System.out.println("6. send() 方法已返回，主線程沒有阻塞！");
        System.out.println("7. 可以繼續執行其他操作...");
        
        // 可以檢查 Future 的狀態
        System.out.println("8. Future.isDone(): " + future.isDone());
        
        // 等待異步回調完成
        System.out.println("9. 等待異步回調完成...");
        latch.await();
        
        System.out.println("10. 關閉 Producer...");
        producer.close();
        System.out.println("=== 異步發送完成 ===");
    }
}