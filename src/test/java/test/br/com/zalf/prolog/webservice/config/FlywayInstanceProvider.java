package test.br.com.zalf.prolog.webservice.config;

import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-04
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface FlywayInstanceProvider {
    @NotNull
    Flyway getFlyway();
}
