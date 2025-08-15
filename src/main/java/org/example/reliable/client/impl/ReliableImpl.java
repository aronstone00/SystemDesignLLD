package org.example.reliable.client.impl;

import org.example.reliable.client.Reliable;
import org.example.reliable.client.Task.TaskManager;
import org.example.reliable.client.helper.JobHelper;
import org.example.reliable.client.repo.entity.JobEntity;
import org.example.reliable.model.ReliableTaskRequest;
import org.example.reliable.model.ReliableTaskStatus;
import org.example.reliable.model.enums.TaskStatus;


public class ReliableImpl implements Reliable {


    private final JobHelper jobHelper;
    private final TaskManager taskManager;

    public ReliableImpl(JobHelper jobHelper, TaskManager taskManager) {
        this.jobHelper = jobHelper;
        this.taskManager = taskManager;
    }

    @Override
    public String submit(ReliableTaskRequest request) {
        JobEntity job = jobHelper.validateAndPersist(request);
        if (TaskStatus.valueOf(job.getStatus()) == TaskStatus.SUBMITTED) {

            taskManager.add(job.getJobId());
        }
        return job.getJobId();
    }


    @Override
    public ReliableTaskStatus status(String referenceId) {
        JobEntity job = jobHelper.fetchByRefId(referenceId);
        return ReliableTaskStatus.builder()
                .status(TaskStatus.valueOf(job.getStatus()))
                .response(job.getResponse())
                .build();

    }


}
