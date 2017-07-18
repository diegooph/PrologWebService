package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDao;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDaoImpl;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;

/**
 * Provide a simple way to use DI by hand.
 */
public final class Injection {

    private Injection() {
        throw new IllegalStateException(Injection.class.getSimpleName() + " cannot be instantiated!");
    }

    public static ChecklistDao provideChecklistDao() {
        return new ChecklistDaoImpl();
    }

    public static VeiculoDao provideVeiculoDao() {
        return new VeiculoDaoImpl();
    }
}