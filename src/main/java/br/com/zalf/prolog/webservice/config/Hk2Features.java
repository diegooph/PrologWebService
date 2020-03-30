package br.com.zalf.prolog.webservice.config;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

/**
 * Created on 2020-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Provider
public final class Hk2Features implements Feature {

    @Override
    public boolean configure(final FeatureContext context) {
        context.register(new PrologBinder());
        return true;
    }
}
