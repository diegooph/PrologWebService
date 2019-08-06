package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VeiculoConferenciaDao {

    /*void verificarPlanilha(@NotNull final Long codUnidade,
                           @NotNull final String json) throws Throwable;*/

    void getVerificacaoPlanilhaCsv(@NotNull final OutputStream out,
                   @NotNull final Long codUnidade,
                   @NotNull final String jsonPlanilha)throws Throwable;
}


