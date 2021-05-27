package br.com.zalf.prolog.webservice.schedules.time;

import org.springframework.scheduling.annotation.Scheduled;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 2021-05-27
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Scheduled(cron = "0 0 3 * * *", zone = "America/Sao_Paulo")
public @interface EveryDayAtThreeAm {
}
