package br.com.zalf.prolog.webservice;

/**
 * Created on 11/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class DummyData {

    protected void ensureDebugEnvironment() {
        if (!BuildConfig.DEBUG) {
            throw new IllegalStateException("Esse resource só pode ser utilizado em ambientes de testes");
        }
    }
}
