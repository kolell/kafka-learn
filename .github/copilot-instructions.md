# copilot-instructions.md

- [x] Clarify Project Requirements
  - 研究 Kafka 的异步消息与同步消息，使用最新版本的 Kafka，包含示例代码和文档。

- [x] Scaffold the Project
  - 已创建 Maven 项目结构
  - 已添加 Kafka 3.7.0 依赖配置
  - 已创建示例代码文件和文档目录

- [x] Customize the Project
  - 已创建同步消息生产者示例 (SyncProducerExample.java)
  - 已创建异步消息生产者示例 (AsyncProducerExample.java)
  - 已创建消费者示例 (ConsumerExample.java)
  - 已创建性能对比示例 (SyncVsAsyncComparison.java)
  - 已添加详细的调试注释和说明

- [x] Install Required Extensions
  - 项目不需要特定的 VS Code 扩展

- [x] Compile the Project
  - 已配置 UTF-8 编码支持
  - 项目编译成功，所有依赖正确解析

- [x] Create and Run Task
  - 已创建 VS Code 调试配置 (.vscode/launch.json)
  - 支持调试同步生产者、异步生产者、消费者和性能对比

- [x] Launch the Project
  - 项目已成功推送到 GitHub

- [x] Ensure Documentation is Complete
  - 已创建完整的 README.md
  - 已添加详细的调试指南和快速开始文档

## 项目已成功推送到 GitHub

🎉 **GitHub 仓库**: https://github.com/kolell/kafka-learn

项目现在包含：
- **同步消息示例**：展示 `.get()` 方法如何阻塞线程
- **异步消息示例**：展示 Callback 机制和非阻塞发送
- **性能对比示例**：直观对比同步与异步的性能差异
- **完整调试配置**：可以在 VS Code 中设置断点调试源码
- **Docker 环境**：一键启动 Kafka 服务
- **详细文档**：包含调试指南和快速开始指南

### 使用步骤：
1. 克隆仓库：`git clone https://github.com/kolell/kafka-learn.git`
2. 启动 Kafka：`docker-compose up -d`
3. 创建主题：`docker exec kafka kafka-topics --create --topic test-topic --bootstrap-server localhost:9092`
4. 编译项目：`mvn compile`
5. 在 VS Code 中设置断点并调试

### 关键调试点：
- `producer.send()` 方法的返回值
- `future.get()` 的阻塞行为
- Callback 的异步执行时机
- RecordMetadata 的内容和时序