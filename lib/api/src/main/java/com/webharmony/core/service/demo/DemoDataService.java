package com.webharmony.core.service.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DemoDataService {


    public void initializeDemoData() {
        log.info("Core demo initialization");
    }

    protected void printStartDataCreation() {
        log.info("Starting Demo Data Creation");
    }

    protected void printEndDataCreation() {
        log.info("Finished Demo Data Creation");
    }

}
