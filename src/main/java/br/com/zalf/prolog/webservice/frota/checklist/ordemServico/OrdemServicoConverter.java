package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

/**
 * Created on 07/08/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@VisibleForTesting
public final class OrdemServicoConverter {

    private OrdemServicoConverter() {
        throw new IllegalStateException(OrdemServicoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @VisibleForTesting
    @NotNull
    public static OrdemServico createOrdemServico(@NotNull final ResultSet rSet) throws Throwable {
        // TODO:
        return null;
    }
}