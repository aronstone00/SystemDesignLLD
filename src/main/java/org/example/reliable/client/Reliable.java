package org.example.reliable.client;

import org.example.reliable.model.ReliableTaskRequest;
import org.example.reliable.model.ReliableTaskStatus;

public interface Reliable {


    String submit(ReliableTaskRequest request);

    ReliableTaskStatus status(String referenceId);
}
