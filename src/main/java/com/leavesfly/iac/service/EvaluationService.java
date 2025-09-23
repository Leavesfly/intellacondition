package com.leavesfly.iac.service;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.leavesfly.iac.evalute.EvaluteResult;
import com.leavesfly.iac.evalute.Solution;

/**
 * 解决方案评估服务接口
 * 
 * 定义解决方案评估的核心业务接口，支持单个和批量评估
 */
public interface EvaluationService {

    /**
     * 评估单个解决方案
     * 
     * @param solution 解决方案
     * @return 评估结果
     */
    EvaluteResult evaluate(Solution solution);

    /**
     * 批量评估解决方案
     * 
     * @param solutions 解决方案集合
     * @return 评估结果集合
     */
    Collection<EvaluteResult> evaluateBatch(Collection<Solution> solutions);

    /**
     * 异步评估解决方案
     * 
     * @param solution 解决方案
     * @return 异步任务Future
     */
    CompletableFuture<EvaluteResult> evaluateAsync(Solution solution);

    /**
     * 异步批量评估解决方案
     * 
     * @param solutions 解决方案集合
     * @return 异步任务Future
     */
    CompletableFuture<Collection<EvaluteResult>> evaluateBatchAsync(Collection<Solution> solutions);

    /**
     * 比较两个解决方案
     * 
     * @param solution1 解决方案1
     * @param solution2 解决方案2
     * @return 比较结果（正数表示solution1更优）
     */
    double compare(Solution solution1, Solution solution2);
}