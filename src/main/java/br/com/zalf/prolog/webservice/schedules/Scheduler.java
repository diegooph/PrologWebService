package br.com.zalf.prolog.webservice.schedules;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created on 2020-11-30
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface Scheduler {
    @Scheduled
    void doWork();
}
