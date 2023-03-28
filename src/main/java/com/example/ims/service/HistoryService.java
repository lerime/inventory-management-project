package com.example.ims.service;

import com.example.ims.enums.TransactionType;
import com.example.ims.model.History;
import com.example.ims.repository.HistoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistoryService {
    private static HistoryService instance;
    @Autowired
    private HistoryRepository historyRepository;

    @PostConstruct
    public void registerInstance() {
        instance = this;
    }

    public static void createHistoryRecord(TransactionType type, Long modelId) {
        History history = new History();
        history.setType(type);
        history.setModelId(modelId);
        instance.historyRepository.save(history);
    }
}
