package br.com.zalf.prolog.webservice.schedules;

import br.com.zalf.prolog.webservice.commons.util.files.FileUtils;
import br.com.zalf.prolog.webservice.schedules.time.Daily;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-02-25
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Profile("prod")
@Component
public class CreateTempDirScheduler implements Scheduler {
    @Daily
    @Override
    public void doWork() {
        FileUtils.createTempDir();
    }
}
