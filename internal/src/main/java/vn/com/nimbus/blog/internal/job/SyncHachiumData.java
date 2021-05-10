package vn.com.nimbus.blog.internal.job;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.com.nimbus.blog.internal.service.HachiumService;

@Component
@Slf4j
public class SyncHachiumData {
    private final HachiumService hachiumService;

    @Autowired
    public SyncHachiumData(HachiumService hachiumService) {
        this.hachiumService = hachiumService;
    }


    @Scheduled(cron = "0 0 * * * ?")
    @SchedulerLock(name = "Hachium_syncData",
            lockAtMostFor = "PT5S", lockAtLeastFor = "PT10M")
    public void scheduledTask() {
        log.info("[Hachium_syncData] start");
        hachiumService.syncData();
        log.info("[Hachium_syncData] end");
    }

}
