package com.yildizan.newsfrom.locator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Service
@RequiredArgsConstructor
public class BufferService {

    private final EntityManager entityManager;

    public void clearBuffer() {
        entityManager.createStoredProcedureQuery("clear_buffer").execute();
    }

    public void updateNews() {
        entityManager.createStoredProcedureQuery("update_news").execute();
    }

}
