package br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticadoProvider;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

/**
 * Created on 2020-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PrologBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bindFactory(ColaboradorAutenticadoProvider.class).to(ColaboradorAutenticado.class).in(RequestScoped.class);
    }
}
