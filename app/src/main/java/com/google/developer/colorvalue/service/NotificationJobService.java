package com.google.developer.colorvalue.service;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class NotificationJobService extends JobService {

    public static final int NOTIFICATION_ID = 18;

    @Override
    public boolean onStartJob(JobParameters params) {
        // notification

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}