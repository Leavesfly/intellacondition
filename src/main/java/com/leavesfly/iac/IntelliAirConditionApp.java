package com.leavesfly.iac;

import com.leavesfly.iac.async.AsyncExecutorManager;
import com.leavesfly.iac.facade.IntelliAirConditionFacade;
import com.leavesfly.iac.service.EvaluationService;
import com.leavesfly.iac.service.ModelTrainingService;
import com.leavesfly.iac.service.PowerSchedulingService;
import com.leavesfly.iac.service.impl.DefaultEvaluationService;
import com.leavesfly.iac.service.impl.DefaultModelTrainingService;
import com.leavesfly.iac.service.impl.PsoSchedulingService;

/**
 * 智能空调仿真平台现代化主类
 * 
 * 采用依赖注入和门面模式重构，提供清晰的API和优雅的错误处理
 * 支持同步和异步两种运行模式
 */
public final class IntelliAirConditionApp {

    /**
     * 程序入口点
     */
    public static void main(String[] args) {
        IntelliAirConditionApp app = new IntelliAirConditionApp();
        
        try {
            // 检查命令行参数
            if (args.length > 0 && "async".equalsIgnoreCase(args[0])) {
                app.runAsync();
            } else {
                app.runSync();
            }
        } catch (Exception e) {
            System.err.println("程序执行失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 确保资源清理
            AsyncExecutorManager.getInstance().shutdown();
        }
    }

    /**
     * 同步运行模式
     */
    public void runSync() {
        System.out.println("=== 智能空调仿真平台启动（同步模式）===");
        
        // 创建门面实例
        IntelliAirConditionFacade facade = createFacade();
        
        // 执行完整优化流程
        long startTime = System.currentTimeMillis();
        IntelliAirConditionFacade.OptimizationResult result = facade.optimizeAirCondition();
        long duration = System.currentTimeMillis() - startTime;
        
        // 输出结果
        System.out.println("\n=== 优化完成 ===");
        System.out.println(result);
        System.out.printf("总用时：%d 毫秒%n", duration);
    }

    /**
     * 异步运行模式
     */
    public void runAsync() {
        System.out.println("=== 智能空调仿真平台启动（异步模式）===");
        
        // 创建门面实例
        IntelliAirConditionFacade facade = createFacade();
        
        // 异步执行优化
        long startTime = System.currentTimeMillis();
        facade.optimizeAirConditionAsync()
            .thenAccept(result -> {
                long duration = System.currentTimeMillis() - startTime;
                System.out.println("\n=== 异步优化完成 ===");
                System.out.println(result);
                System.out.printf("总用时：%d 毫秒%n", duration);
            })
            .exceptionally(throwable -> {
                System.err.println("异步优化失败: " + throwable.getMessage());
                throwable.printStackTrace();
                return null;
            })
            .join(); // 等待完成
    }

    /**
     * 分步骤运行模式（演示）
     */
    public void runStepwise() {
        System.out.println("=== 智能空调仿真平台启动（分步模式）===");
        
        IntelliAirConditionFacade facade = createFacade();
        
        // 分步骤执行
        IntelliAirConditionFacade.OptimizationResult result = facade.createStepwiseOptimization()
            .trainModels()       // 步骤1：训练模型
            .schedulePower()     // 步骤2：功率调度
            .getResult();        // 步骤3：获取结果
        
        System.out.println("\n=== 分步优化完成 ===");
        System.out.println(result);
    }

    /**
     * 创建门面实例 - 依赖注入配置
     */
    private IntelliAirConditionFacade createFacade() {
        // 创建服务实例（这里简化，实际应该使用DI容器）
        ModelTrainingService trainingService = new DefaultModelTrainingService();
        PowerSchedulingService schedulingService = new PsoSchedulingService();
        EvaluationService evaluationService = new DefaultEvaluationService();
        
        return new IntelliAirConditionFacade(
            trainingService,
            schedulingService, 
            evaluationService
        );
    }

    /**
     * 显示帮助信息
     */
    public static void showHelp() {
        System.out.println("智能空调仿真平台使用说明：");
        System.out.println("java IntelliAirConditionApp [模式]");
        System.out.println("模式选项：");
        System.out.println("  (无参数)  - 同步模式运行");
        System.out.println("  async     - 异步模式运行");
        System.out.println("  stepwise  - 分步模式运行");
        System.out.println("  help      - 显示此帮助信息");
    }
}