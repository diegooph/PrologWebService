package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VeiculoConferenciaDao {

    void verificarPlanilha(@NotNull final Long codUnidade,
                           @NotNull final String json) throws Throwable;
}
