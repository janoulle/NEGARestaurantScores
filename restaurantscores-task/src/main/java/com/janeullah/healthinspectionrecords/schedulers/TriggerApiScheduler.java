package com.janeullah.healthinspectionrecords.schedulers;

import com.janeullah.healthinspectionrecords.jobs.TriggerApiJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Slf4j
@Component
public class TriggerApiScheduler {

    @EventListener(ContextRefreshedEvent.class)
    public void scheduleJob() {
        log.info("ContextRefreshedEvent triggered for TriggerApiScheduler");

        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.start();

            //http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-06.html
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(cronSchedule("0 0/2 8-17 * * ?"))
//                    .withSchedule(
//                            weeklyOnDayAndHourAndMinute(DateBuilder.SATURDAY, 10, 30)
//                                    .inTimeZone(TimeZone.getTimeZone("America/Denver")))
                    .build();

            scheduler.scheduleJob(newJob(TriggerApiJob.class).build(), trigger);
            log.info("event=Scheduler nextFireTime={}", trigger.getNextFireTime());
        } catch (SchedulerException e) {
            log.error("Unable to schedule job", e);
        }
    }
}
