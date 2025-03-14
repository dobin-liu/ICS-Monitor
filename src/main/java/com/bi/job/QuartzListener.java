package com.bi.job;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class QuartzListener implements ServletContextListener {

    private Scheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
        	
            // 1. 創建排程器
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();

            // 2. 定義 Job
            JobDetail job = JobBuilder.newJob(MonitorJob.class).withIdentity("monitorJob", "group1").build();

            // 3. 定義觸發器 (每兩分鐘)
            Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("monitorJobTrigger", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/30 * * * * ? *"))
                .build();

            // 4. 啟動排程器並註冊 Job 和 Trigger
            scheduler.scheduleJob(job, trigger);

            // 5. 啟動排程器
            scheduler.start();

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            // 在應用停止時關閉排程器
            if (scheduler != null) {
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}