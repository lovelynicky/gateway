package com.spring.mvc.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by liluoqi on 15/4/27.
 */
public abstract class BaseJob implements Job {

    protected ApplicationContext applicationContext;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        getContext(jobExecutionContext);
        injectDependency();
        executeInternal(jobExecutionContext);
    }

    protected void getContext(JobExecutionContext jobExecutionContext) {
        try {
            applicationContext = (ApplicationContext) jobExecutionContext.getScheduler().getContext().get("applicationContext");
        } catch (SchedulerException e) {
            applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
    }

    protected abstract void executeInternal(JobExecutionContext jobExecutionContext);

    protected void injectDependency() {

    }
}
