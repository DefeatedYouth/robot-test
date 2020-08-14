package com.robot.host.quartz.task;

/**
 * 定时任务接口，所有定时任务都要实现该接口
 *
 */
public interface ITask {

    /**
     * 执行定时任务接口
     *
     * @param params   参数，多参数使用JSON数据
     */
    void run(String params);

    /**
     * 添加任务执行日志
     * @param robotId
     * @param taskId
     * @param logContent
     */
    void saveOperationLog(Long robotId, Long taskId, String logContent, String deviceId, String taskCode);

}