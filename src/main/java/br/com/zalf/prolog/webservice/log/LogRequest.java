package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.log._model.LogLevel;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 13/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface LogRequest {
    @NotNull
    LogLevel logLevel() default LogLevel.ALL;
}
