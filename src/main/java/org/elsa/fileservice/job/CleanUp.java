package org.elsa.fileservice.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author valor
 * @date 2018-11-23 15:22
 */
@Service
public class CleanUp {

    /**
     * 定时清理一定时间内没有使用过的文件
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanUpFile() {

    }
}
