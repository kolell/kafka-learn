package com.example.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.CountDownLatch;

/**
 * 對比同步和異步發送的性能和實現差異
 */
public class SyncVsAsyncComparison {
    
    private static KafkaProducer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new KafkaProducer<>(props);
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Kafka 同步 vs 異步性能對比 ===\n");
        
        // 測試同步發送
        testSyncSend();
        
        Thread.sleep(1000); // 等待一秒
        
        // 測試異步發送
        testAsyncSend();
    }
    
    /**
     * 測試同步發送 - 每次調用都會阻塞等待結果
     */
    private static void testSyncSend() throws Exception {
        System.out.println("--- 同步發送測試 ---");
        KafkaProducer<String, String> producer = createProducer();
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 5; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>(
                "test-topic", "sync-key-" + i, "同步消息-" + i);
            
            System.out.println("發送第 " + (i + 1) + " 條同步消息...");
            
            // 關鍵：get() 會阻塞，等待每條消息發送完成才繼續
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get(); // 阻塞調用
            
            System.out.printf("  -> 消息 %d 發送完成，offset: %d\n", i + 1, metadata.offset());
        }
        
        long endTime = System.currentTimeMillis();
        System.out.printf("同步發送 5 條消息總耗時: %d ms\n\n", endTime - startTime);
        
        producer.close();
    }
    
    /**
     * 測試異步發送 - 所有消息立即發送，通過回調處理結果
     */
    private static void testAsyncSend() throws Exception {
        System.out.println("--- 異步發送測試 ---");
        KafkaProducer<String, String> producer = createProducer();
        
        CountDownLatch latch = new CountDownLatch(5); // 等待 5 個回調完成
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 5; i++) {
            final int messageId = i + 1;
            ProducerRecord<String, String> record = new ProducerRecord<>(
                "test-topic", "async-key-" + i, "異步消息-" + i);
            
            System.out.println("發送第 " + messageId + " 條異步消息...");
            
            // 關鍵：send() 立即返回，不阻塞
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception == null) {
                        System.out.printf("  -> 異步回調：消息 %d 發送完成，offset: %d\n", 
                                         messageId, metadata.offset());
                    } else {
                        System.err.printf("  -> 異步回調：消息 %d 發送失敗: %s\n", 
                                         messageId, exception.getMessage());
                    }
                    latch.countDown(); // 通知計數器
                }
            });
        }
        
        long sendTime = System.currentTimeMillis();
        System.out.printf("所有異步消息發送調用完成，耗時: %d ms\n", sendTime - startTime);
        
        System.out.println("等待所有異步回調完成...");
        latch.await(); // 等待所有回調完成
        
        long endTime = System.currentTimeMillis();
        System.out.printf("異步發送 5 條消息總耗時: %d ms\n", endTime - startTime);
        
        producer.close();
        System.out.println("=== 對比測試完成 ===");
    }
}