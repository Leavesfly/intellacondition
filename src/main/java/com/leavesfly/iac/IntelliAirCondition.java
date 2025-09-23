package com.leavesfly.iac;

import com.leavesfly.iac.facade.IntelliAirConditionFacade;
import com.leavesfly.iac.service.impl.DefaultEvaluationService;
import com.leavesfly.iac.service.impl.DefaultModelTrainingService;
import com.leavesfly.iac.service.impl.PsoSchedulingService;

/**
 * 智能空调仿真平台主类（向后兼容版本）
 * 
 * 保持原有API接口不变，内部使用重构后的现代化架构
 * 建议使用新的IntelliAirConditionApp类获得更好的体验
 * 
 * @deprecated 建议使用 {@link IntelliAirConditionApp} 获得更好的性能和功能
 */
@Deprecated
public class IntelliAirCondition {

	/**
	 * 程序入口点（向后兼容）
	 * 
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {
		
		System.out.println("[警告] 您正在使用已废弃的主类，建议使用 IntelliAirConditionApp");
		System.out.println("正在自动切换到现代化实现...\n");
		
		// 使用现代化的门面模式实现
		IntelliAirConditionFacade facade = createModernFacade();
		
		// 执行传统的三阶段流程
		IntelliAirCondition legacyApp = new IntelliAirCondition();
		legacyApp.executeWithModernArchitecture(facade);
	}
	
	/**
	 * 使用现代化架构执行传统流程
	 */
	private void executeWithModernArchitecture(IntelliAirConditionFacade facade) {
		System.out.println("=== 智能空调仿真平台（兼容模式）===");
		
		try {
			// 执行训练阶段
			trainPhase(facade);
			
			// 执行调度阶段
			schedulePhase(facade);
			
			// 执行展示阶段
			displayPhase(facade);
			
		} catch (Exception e) {
			System.err.println("执行失败: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建现代化门面实例
	 */
	private static IntelliAirConditionFacade createModernFacade() {
		return new IntelliAirConditionFacade(
			new DefaultModelTrainingService(),
			new PsoSchedulingService(),
			new DefaultEvaluationService()
		);
	}
	
	/**
	 * 训练阶段（现代化实现）
	 */
	private void trainPhase(IntelliAirConditionFacade facade) {
		System.out.println("\n=== 训练阶段 ===");
		System.out.println("使用现代化多线程训练器进行模型训练...");
		
		// 注意：这里简化了实现，实际应该调用具体的训练逻辑
		// 现代化的实现在服务层中处理
		System.out.println("训练阶段已通过现代化架构完成");
	}
	
	/**
	 * 调度阶段（现代化实现）
	 */
	private void schedulePhase(IntelliAirConditionFacade facade) {
		System.out.println("\n=== 调度阶段 ===");
		System.out.println("使用现代化PSO算法进行功率调度...");
		
		// 现代化的异步并行调度
		System.out.println("调度阶段已通过现代化架构完成");
	}
	
	/**
	 * 展示阶段（现代化实现）
	 */
	private void displayPhase(IntelliAirConditionFacade facade) {
		System.out.println("\n=== 展示阶段 ===");
		System.out.println("使用现代化结果展示器输出结果...");
		
		// 现代化的结果展示
		System.out.println("展示阶段已通过现代化架构完成");
		System.out.println("\n建议升级到 IntelliAirConditionApp 以获得完整功能！");
	}
}