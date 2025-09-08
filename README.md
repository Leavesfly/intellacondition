#intellacondition#
智能空调仿真平台

## 项目概述

IntelliCondition 是一个基于机器学习和粒子群优化算法(PSO)的智能空调仿真平台。该系统通过训练功率-温度映射函数，并使用PSO算法优化空调功率分配，实现最佳的温度控制效果和能耗平衡。

## 核心特性

- 🧠 **智能训练模块** - 基于历史数据训练功率-温度映射函数
- 🔧 **PSO优化算法** - 使用粒子群优化和混沌PSO算法寻找最优功率分配
- 👤 **个性化舒适度** - 支持用户个性化温度偏好设置
- 📊 **多策略评估** - 提供个性化和全局优化两种模式
- 📈 **数据可视化** - 集成JFreeChart提供结果展示和分析
- 🔄 **多线程处理** - 支持并发训练和调度执行

## 技术架构

### 系统组成
```
IntelliCondition
├── 训练模块 (Train Module)
│   ├── 数据收集 (DataCollecter)
│   ├── 数据存储 (Lucene-based Storage)
│   ├── 功率-温度函数训练 (PtTrainer)
│   └── 多线程训练器 (PtMultiThreadTrainer)
├── 执行模块 (Execute Module)
│   ├── 功率调度器 (PowerScheduler)
│   ├── PSO算法实现 (PsoPowerScheduler)
│   └── 用户舒适度模块 (ContiUserComfortFunc)
├── 评估模块 (Evaluate Module)
│   ├── 解决方案构建器 (SolutionBuilder)
│   └── 评估器 (Evaluator)
└── 展示模块 (Display Module)
    ├── 数据可视化 (JFreeChart)
    └── 结果输出
```

### 核心算法
- **PSO粒子群优化算法** - 寻找最优功率分配方案
- **混沌PSO算法** - 改进的PSO算法实现，提高收敛性能
- **BP神经网络** - 3层神经网络训练功率-温度映射函数
- **线性回归(LR)模型** - 备选的训练模型

## 技术栈

- **Java 1.7** - 核心开发语言
- **Maven** - 项目构建和依赖管理
- **Apache Lucene 3.6.0** - 训练数据存储和检索
- **Weka 3.6.10** - 机器学习算法库
- **JFreeChart 1.0.7** - 数据可视化
- **Apache Commons Lang 2.6** - 工具类库
- **JUnit 4.5** - 单元测试

## 项目结构

```
src/main/java/com/leavesfly/iac/
├── IntelliAirCondition.java    # 主程序入口
├── config/                     # 配置常量
│   ├── AppContextConstant.java
│   ├── BpAlgorithmConstant.java
│   └── PsoAlgorithmConstant.java
├── datasource/                 # 数据工厂
│   └── DataFactory.java
├── domain/                     # 领域模型
│   ├── PowerVector.java
│   ├── PowerValue.java
│   ├── PtFitFunc.java
│   └── UserTempRange.java
├── train/                      # 训练模块
│   ├── collect/               # 数据收集
│   ├── domain/                # 训练数据模型
│   ├── store/                 # 数据存储(Lucene)
│   └── trainer/               # 训练器实现
├── execute/                    # 执行模块
│   ├── scheduler/             # 调度器实现
│   └── domain/                # 执行相关模型
├── evalute/                    # 评估模块
├── display/                    # 展示模块
└── util/                       # 工具类
```

## 系统约束

1. **功率范围**: 空调终端功率取值范围 0-400W
2. **温度区间**: 支持温度范围 15-35°C
3. **外部温度**: 预设外部温度为 35°C
4. **功率规约**: 计算时功率值规约到 0-1 之间

## 快速开始

### 环境要求
- Java 1.7+
- Maven 3.x

### 编译和运行

```bash
# 克隆项目
git clone <repository-url>
cd intellacondition

# 编译项目
mvn clean compile

# 运行主程序
mvn exec:java -Dexec.mainClass="com.leavesfly.iac.IntelliAirCondition"

# 或者直接运行
java -cp target/classes com.leavesfly.iac.IntelliAirCondition
```

## 系统工作流程

### 1. 训练阶段 (trainPhase)
- 从文本文件收集训练数据
- 使用Lucene存储训练数据集
- 通过多线程训练器构建功率-温度映射函数
- 支持BP神经网络和LR模型

### 2. 调度阶段 (schedulePhase)
- **个性化方案**: 根据用户温度偏好生成解决方案
- **PSO优化**: 使用标准PSO算法寻找最优功率向量
- **混沌PSO优化**: 使用改进的混沌PSO算法
- 并发执行多种优化策略

### 3. 展示阶段 (displayPhase)
- 输出各种解决方案的评估结果
- 显示总满意度、总功耗成本和功率效用

## 实验结果示例

系统会输出类似以下的优化结果：

```
solution_pso:[221.3,96.7,261.1,40.4,171.4,126.3,50.5,217.5]
totalSatisfaction:8.365845  totalPowerCost:1185.2312  powerUtility:7.058408

solution_pso_chaos:[131.2,143.0,202.2,143.1,159.6,141.6,105.9,141.8]
totalSatisfaction:8.24261  totalPowerCost:1168.3904  powerUtility:7.0546713
```

## 核心类说明

- **IntelliAirCondition**: 主程序入口，协调训练、调度和展示三个阶段
- **DataFactory**: 线程安全的数据工厂，管理全局数据
- **PsoPowerScheduler**: PSO算法实现，支持标准和混沌两种模式
- **PtMultiThreadTrainer**: 多线程训练器，支持并发模型训练
- **Evaluator**: 评估器，计算解决方案的各项指标

## 扩展功能

- 支持多种机器学习模型(BP神经网络、线性回归)
- 可配置的PSO算法参数
- 灵活的用户舒适度函数
- 完整的数据可视化支持

## 开发计划

- [x] 训练模块 - 数据收集和功率-温度函数训练
- [x] 执行模块 - PSO算法实现和功率向量优化
- [x] 评估模块 - 多策略评估和结果分析
- [x] 展示模块 - 数据可视化和结果输出

## 许可证

本项目采用开源许可证，具体信息请查看 LICENSE 文件。

## 贡献

欢迎提交 Issue 和 Pull Request 来改进项目。

---

*IntelliCondition - 让智能空调更智能* 🌡️