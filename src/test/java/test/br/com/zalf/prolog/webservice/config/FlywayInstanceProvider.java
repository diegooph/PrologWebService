package test.br.com.zalf.prolog.webservice.config;

import org.flywaydb.core.Flyway;

/**
 * Created on 2021-03-04
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface FlywayInstanceProvider {
    Flyway getFlyway();
}
