package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VeiculoConferenciaDao {

    /**
     * Método para retornar a conferência da planilha de import de veículos.
     *
     * @param out          Streaming onde os dados serão escritos.
     * @param codUnidade   Código da unidade para a qual as informações serão conferidas.
     * @param jsonPlanilha Informações da planilha de import de veículos em formato Json.
     * @throws Throwable Se algum erro ocorrer.
     */
    void importPlanilhaVeiculos(@NotNull final OutputStream out,
                                @NotNull final Long codEmpresa,
                                @NotNull final Long codUnidade,
                                @NotNull final String usuario,
                                @NotNull final String jsonPlanilha) throws Throwable;
}