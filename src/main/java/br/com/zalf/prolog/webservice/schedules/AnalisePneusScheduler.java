package br.com.zalf.prolog.webservice.schedules;

import br.com.zalf.prolog.webservice.schedules.time.EveryDayAtThreeAm;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-05-27
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Profile("prod")
@Component
public class AnalisePneusScheduler implements Scheduler {

    @Override
    @EveryDayAtThreeAm
    public void doWork() {

    }
}
