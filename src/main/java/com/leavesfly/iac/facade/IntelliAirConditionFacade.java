package com.leavesfly.iac.facade;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.leavesfly.iac.config.AppConfig;
import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.domain.improved.PowerVector;
import com.leavesfly.iac.evalute.EvaluteResult;
import com.leavesfly.iac.exception.IntelliAirConditionException;
import com.leavesfly.iac.service.EvaluationService;
import com.leavesfly.iac.service.ModelTrainingService;
import com.leavesfly.iac.service.PowerSchedulingService;

/**
 * 智能空调系统门面类
 * 
 * 提供统一的高级API接口，封装复杂的子系统交互，简化客户端调用
 * 应用门面模式，为复杂的空调调度系统提供简洁的接口
 */
public final class IntelliAirConditionFacade {

    private final ModelTrainingService trainingService;
    private final PowerSchedulingService schedulingService;
    private final EvaluationService evaluationService;
    private final AppConfig config;

    /**
     * 构造函数 - 依赖注入
     */
    public IntelliAirConditionFacade(
            ModelTrainingService trainingService,
            PowerSchedulingService schedulingService,
            EvaluationService evaluationService) {
        
        this.trainingService = trainingService;
        this.schedulingService = schedulingService;
        this.evaluationService = evaluationService;
        this.config = AppConfig.getInstance();
    }

    /**
     * 一键式智能空调优化
     * 
     * 执行完整的空调调度优化流程：训练模型 -> 功率调度 -> 结果评估
     * 
     * @return 优化结果
     */
    public OptimizationResult optimizeAirCondition() {
        try {
            // 1. 训练模型
            System.out.println("=== 开始模型训练 ===");
            Collection<PtFitFunc> fitFunctions = trainingService.trainModels(
                config.getPower().getOutsideTemp()
            );
            System.out.printf("训练完成，生成 %d 个预测模型%n", fitFunctions.size());

            // 2. 功率调度
            System.out.println("=== 开始功率调度优化 ===");
            PowerVector optimizedPower = schedulingService.schedule();
            System.out.printf("调度完成，总功率：%.2f，平均功率：%.2f%n", 
                optimizedPower.getTotalPower(), optimizedPower.getAveragePower());

            // 3. 结果评估
            System.out.println("=== 开始结果评估 ===");
            // 这里需要创建Solution对象并评估，简化示例
            
            return new OptimizationResult(fitFunctions, optimizedPower, null);
            
        } catch (Exception e) {
            throw new IntelliAirConditionException("IAC_OPTIMIZATION", 
                "空调优化过程失败: " + e.getMessage(), e);
        }
    }

    /**
     * 异步一键式优化
     * 
     * @return 异步优化结果
     */
    public CompletableFuture<OptimizationResult> optimizeAirConditionAsync() {
        return CompletableFuture.supplyAsync(this::optimizeAirCondition);
    }

    /**
     * 分步骤优化 - 适用于需要中间结果的场景
     */
    public StepwiseOptimization createStepwiseOptimization() {
        return new StepwiseOptimization();
    }

    /**
     * 批量比较不同调度策略
     * 
     * @param strategies 调度策略名称列表
     * @return 比较结果
     */
    public ComparisonResult compareStrategies(String... strategies) {
        // 实现多策略比较逻辑
        return new ComparisonResult();
    }

    /**
     * 优化结果类
     */
    public static final class OptimizationResult {
        private final Collection<PtFitFunc> fitFunctions;
        private final PowerVector optimizedPower;
        private final Collection<EvaluteResult> evaluationResults;

        public OptimizationResult(
                Collection<PtFitFunc> fitFunctions,
                PowerVector optimizedPower,
                Collection<EvaluteResult> evaluationResults) {
            this.fitFunctions = fitFunctions;
            this.optimizedPower = optimizedPower;
            this.evaluationResults = evaluationResults;
        }

        public Collection<PtFitFunc> getFitFunctions() { return fitFunctions; }
        public PowerVector getOptimizedPower() { return optimizedPower; }
        public Collection<EvaluteResult> getEvaluationResults() { return evaluationResults; }

        @Override
        public String toString() {
            return String.format(
                "OptimizationResult{模型数量=%d, 总功率=%.2f, 平均功率=%.2f}",
                fitFunctions != null ? fitFunctions.size() : 0,
                optimizedPower != null ? optimizedPower.getTotalPower() : 0,
                optimizedPower != null ? optimizedPower.getAveragePower() : 0
            );
        }
    }

    /**
     * 分步骤优化类
     */
    public final class StepwiseOptimization {
        private Collection<PtFitFunc> fitFunctions;
        private PowerVector optimizedPower;

        /**
         * 步骤1：训练模型
         */
        public StepwiseOptimization trainModels() {
            this.fitFunctions = trainingService.trainModels(
                config.getPower().getOutsideTemp()
            );
            System.out.printf("步骤1完成：训练了 %d 个模型%n", fitFunctions.size());
            return this;
        }

        /**
         * 步骤2：功率调度
         */
        public StepwiseOptimization schedulePower() {
            if (fitFunctions == null) {
                throw new IntelliAirConditionException("IAC_WORKFLOW", "必须先执行模型训练");
            }
            
            this.optimizedPower = schedulingService.schedule();
            System.out.printf("步骤2完成：总功率 %.2f%n", optimizedPower.getTotalPower());
            return this;
        }

        /**
         * 步骤3：获取结果
         */
        public OptimizationResult getResult() {
            if (fitFunctions == null || optimizedPower == null) {
                throw new IntelliAirConditionException("IAC_WORKFLOW", "必须完成所有步骤");
            }
            
            return new OptimizationResult(fitFunctions, optimizedPower, null);
        }

        /**
         * 获取中间结果
         */
        public Collection<PtFitFunc> getFitFunctions() { return fitFunctions; }
        public PowerVector getOptimizedPower() { return optimizedPower; }
    }

    /**
     * 比较结果类
     */
    public static final class ComparisonResult {
        // 比较结果实现
        @Override
        public String toString() {
            return "ComparisonResult{策略比较结果}";
        }
    }
}