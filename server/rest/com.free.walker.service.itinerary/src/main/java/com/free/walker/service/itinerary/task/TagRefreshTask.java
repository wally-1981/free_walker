package com.free.walker.service.itinerary.task;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Reloadable;
import com.free.walker.service.itinerary.basic.Tag;

public class TagRefreshTask extends TimerTask {
    private static final int SCHEDULE_PERIOD_HOURS = 24;

    private static Logger LOG = LoggerFactory.getLogger(TagRefreshTask.class);
    private static Timer timer = new Timer();

    private Reloadable tagReloader = new Tag();
    private int retryCount = 0;

    private TagRefreshTask() {
        ;
    }

    public void run() {
        try {
            tagReloader.reload();
            retryCount = 0;
            LOG.info(LocalMessages.getMessage(LocalMessages.schedule_task_success,
                TagRefreshTask.class.getSimpleName(), Calendar.getInstance().getTime()));
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.schedule_task_failed,
                TagRefreshTask.class.getSimpleName(), Calendar.getInstance().getTime(), retryCount++));
        }
    }

    public static void schedule() {
        Calendar firstStart = Calendar.getInstance();
        firstStart.set(Calendar.HOUR_OF_DAY, 3);
        firstStart.set(Calendar.MINUTE, 0);
        firstStart.set(Calendar.SECOND, 0);
        firstStart.add(Calendar.DAY_OF_MONTH, 1);

        timer.schedule(new TagRefreshTask(), firstStart.getTime(), SCHEDULE_PERIOD_HOURS * 1000 * 3600);
        LOG.info(LocalMessages.getMessage(LocalMessages.schedule_task_scheduled, TagRefreshTask.class.getSimpleName(),
            firstStart.getTime(), SCHEDULE_PERIOD_HOURS));
    }
}
