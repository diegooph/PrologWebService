package br.com.zalf.prolog.webservice.integracao.newimpl;

import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 2021-04-11
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Integrado {
    @NotNull
    RecursoIntegrado recursoIntegrado();
}