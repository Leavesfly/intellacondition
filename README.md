# 智能空调仿真平台 (IntelliAirCondition)

[![Java](https://img.shields.io/badge/Java-1.7+-blue.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.0+-green.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

一个基于机器学习和粒子群优化算法的智能空调功率调度仿真平台，支持多种优化策略和实时性能评估。

## 🎯 项目概述

智能空调仿真平台是一个综合性的空调系统优化解决方案，集成了：

- **机器学习模型训练** - BP神经网络、线性回归等多种算法
- **智能功率调度** - PSO粒子群优化算法
- **多维度性能评估** - 用户满意度、能耗成本综合评估
- **实时数据可视化** - JFreeChart图表展示
- **并发处理架构** - 高性能多线程计算

> **重要更新**: 本项目已经进行全面重构，采用现代化架构设计，提供更优雅的API和更高的性能。新的[IntelliAirConditionApp](src/main/java/com/leavesfly/iac/IntelliAirConditionApp.java)提供更好的使用体验。

## 🏗️ 系统架构

### 核心模块

```
graph TB
    subgraph "表现层"
        UI[用户界面]
        RD[结果展示器]
    end
    subgraph "业务逻辑层"
        IAC[IntelliAirCondition<br/>主控制器]
        DF[DataFactory<br/>数据工厂]
        EVAL[Evaluator<br/>评估器]
    end
    subgraph "算法层"
        PT[PtTrainer<br/>训练器]
        PSO[PsoAlgorithm<br/>PSO算法]
        BP[BpWekaModel<br/>BP神经网络]
    end
    subgraph "数据层"
        DC[DataCollecter<br/>数据收集器]
        TSM[TrainDataSetManager<br/>数据集管理器]
        RES[ResourceUtil<br/>资源工具]
    end
    UI --> IAC
    IAC --> DF
    IAC --> EVAL
    IAC --> PT
    IAC --> PSO
    PT --> BP
    PT --> DC
    DC --> TSM
    DC --> RES
    EVAL --> DF
    RD --> EVAL
```

### 技术栈

- **核心语言**: Java 1.7+
- **构建工具**: Maven 3.0+
- **机器学习**: Weka 3.6.10
- **数据检索**: Apache Lucene 3.6.0
- **数据可视化**: JFreeChart 1.0.7
- **工具库**: Apache Commons Lang 2.6
- **测试框架**: JUnit 4.5

## 🚀 快速开始

### 环境要求

- Java JDK 1.7 或更高版本
- Maven 3.0 或更高版本
- 至少 2GB 可用内存

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd intellacondition
   ```

2. **编译项目**
   ```bash
   mvn clean compile
   ```

3. **运行测试**
   ```bash
   mvn test
   ```

4. **启动仿真**
   ```bash
   # 传统模式
   mvn exec:java -Dexec.mainClass="com.leavesfly.iac.IntelliAirCondition"
   
   # 现代化模式（推荐）
   mvn exec:java -Dexec.mainClass="com.leavesfly.iac.IntelliAirConditionApp"
   
   # 异步模式
   mvn exec:java -Dexec.mainClass="com.leavesfly.iac.IntelliAirConditionApp" -Dexec.args="async"
   ```

### 使用示例

#### 基础使用

```
// 创建智能空调仿真实例
IntelliAirCondition simulation = new IntelliAirCondition();

// 执行完整的仿真流程
simulation.trainPhase();    // 训练阶段
simulation.schedulePhase(); // 调度阶段
simulation.displayPhase();  // 展示阶段
```

#### 现代化API（推荐）

```
// 使用门面模式的现代化API
IntelliAirConditionFacade facade = new IntelliAirConditionFacade(
    new DefaultModelTrainingService(),
    new PsoSchedulingService(),
    new DefaultEvaluationService()
);

// 一键式优化
OptimizationResult result = facade.optimizeAirCondition();
System.out.println("优化结果: " + result);

// 异步优化
facade.optimizeAirConditionAsync()
    .thenAccept(res -> System.out.println("异步优化完成: " + res))
    .exceptionally(ex -> {
        System.err.println("优化失败: " + ex.getMessage());
        return null;
    });
```

## 📊 核心功能

### 1. 模型训练模块

- **数据收集**: 从多种数据源收集训练数据
- **数据存储**: 基于Lucene的高效数据存储和检索
- **多模型支持**: BP神经网络、Weka集成、线性回归
- **并行训练**: 多线程并行训练提升效率

### 2. 功率调度模块

- **PSO算法**: 标准粒子群优化算法
- **改进PSO**: 混沌粒子群优化算法
- **实时调度**: 支持实时功率调度优化
- **约束处理**: 功率范围约束和用户需求约束

### 3. 性能评估模块

- **多维评估**: 用户满意度、功耗成本综合评估
- **实时计算**: 高效的评估算法
- **结果比较**: 多种解决方案性能对比
- **可视化展示**: 图表化的结果展示

### 4. 数据可视化

- **拟合曲线**: 功率-温度关系可视化
- **性能对比**: 不同算法性能对比图
- **实时监控**: 系统运行状态监控

## 📁 项目结构

```
src/
├── main/java/com/leavesfly/iac/
│   ├── IntelliAirCondition.java          # 主程序入口（传统）
│   ├── IntelliAirConditionApp.java       # 主程序入口（现代化）
│   ├── config/                           # 配置管理
│   │   ├── AppConfig.java               # 应用配置
│   │   ├── PsoConfig.java               # PSO算法配置
│   │   └── BpConfig.java                # BP神经网络配置
│   ├── datasource/                       # 数据源管理
│   │   ├── DataFactory.java            # 数据工厂
│   │   ├── DomainParser.java            # 数据解析器
│   │   └── datagene/                    # 数据生成工具
│   ├── domain/                          # 领域模型
│   │   ├── GeoPoint.java               # 地理坐标
│   │   ├── PowerVector.java            # 功率向量
│   │   ├── PtFitFunc.java              # 功率-温度拟合函数
│   │   └── improved/                   # 改进的领域模型
│   ├── train/                           # 训练模块
│   │   ├── trainer/                    # 训练器
│   │   ├── collect/                    # 数据收集
│   │   └── store/                      # 数据存储
│   ├── execute/                         # 执行模块
│   │   └── scheduler/                  # 调度器
│   ├── evalute/                         # 评估模块
│   ├── display/                         # 显示模块
│   ├── service/                         # 服务层（新）
│   ├── facade/                          # 门面层（新）
│   ├── async/                           # 异步处理（新）
│   ├── repository/                      # 数据访问层（新）
│   └── exception/                       # 异常处理（新）
└── test/                                # 测试代码
```

## ⚙️ 配置说明

### 系统参数配置

```
// 环境参数
AREA_LENGTH = 10        // 区域长度（米）
AREA_WIDTH = 10         // 区域宽度（米）
USER_NUM = 16          // 用户数量
SENSOR_NUM = 10        // 传感器数量
AIR_CONDITION_NUM = 8  // 空调数量

// 算法参数
OUTSIDE_TEMP = 35.0f   // 室外温度（摄氏度）
POWER_PRICE = 1.0f     // 电费单价
SATISFY_WEIGHT = 0.5f  // 满意度权重
```

### PSO算法参数

```
PSO_INIT_PARTICLE_NUM = 30  // 初始粒子数量
PSO_ITERATE_NUM = 300       // 迭代次数
W = 0.9f                    // 惯性权重
C1 = 2.0f                   // 学习因子1
C2 = 2.0f                   // 学习因子2
```

## 🧪 运行示例

### 控制台输出示例

```
=== 智能空调仿真平台启动（同步模式）===

=== 开始模型训练 ===
训练完成，生成 10 个预测模型

=== 开始功率调度优化 ===
调度完成，总功率：2150.50，平均功率：268.81

=== 开始结果评估 ===

=== 优化完成 ===
OptimizationResult{模型数量=10, 总功率=2150.50, 平均功率=268.81}
总用时：3247 毫秒
```

### 性能指标

- **用户满意度**: 基于温度舒适度函数计算
- **功耗成本**: 总功率消耗 × 电费单价
- **功率效用**: 单位功耗的用户满意度提升
- **算法收敛性**: PSO算法的收敛曲线分析

## 🔧 开发指南

### 扩展新的训练模型

```
public class CustomModel implements TrainModel {
    @Override
    public <T extends TrainDataItem<Float, Float>> void train(Collection<T> trainDataSet) {
        // 实现自定义训练逻辑
    }
    
    @Override
    public <T extends Number> float useMode(T[] feature) {
        // 实现预测逻辑
        return prediction;
    }
}
```

### 添加新的优化算法

```
public class CustomScheduler implements PowerScheduler {
    @Override
    public PowerVector schedule() {
        // 实现自定义调度算法
        return optimizedPowerVector;
    }
}
```

## 📈 性能优化

- **并行计算**: 多线程训练和评估
- **内存优化**: 懒加载和对象池技术
- **算法优化**: 改进的PSO算法收敛更快
- **缓存机制**: 计算结果缓存减少重复计算

## 🔄 架构重构

本项目已进行全面现代化重构，主要改进包括：

- **配置管理**: 统一的类型安全配置系统
- **领域模型**: 不可变对象设计，增强线程安全
- **服务层**: 依赖注入和接口抽象
- **异步处理**: 现代化的CompletableFuture API
- **门面模式**: 简化的客户端API
- **异常处理**: 统一的异常处理策略

详细重构说明请参考 [REFACTORING_GUIDE.md](REFACTORING_GUIDE.md)

## 🤝 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目主页: [GitHub Repository](https://github.com/username/intellacondition)
- 问题反馈: [Issues](https://github.com/username/intellacondition/issues)
- 邮箱: your.email@example.com

## 🙏 致谢

- [Apache Lucene](https://lucene.apache.org/) - 高性能全文检索引擎
- [Weka](https://www.cs.waikato.ac.nz/ml/weka/) - 机器学习工具包
- [JFreeChart](http://www.jfree.org/jfreechart/) - Java图表库
- [Apache Commons](https://commons.apache.org/) - Java实用工具库

---

**注意**: 本项目已进行全面重构，新增现代化API和架构。详细重构说明请参考 [REFACTORING_GUIDE.md](REFACTORING_GUIDE.md)