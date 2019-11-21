package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.pneu;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 19/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuConferenciaDao {

    /**
     * Método para retornar a conferência da planilha de import de pneus.
     *
     * @param codUnidade   Código da unidade para a qual as informações serão conferidas.
     * @param jsonPlanilha Informações da planilha de import de pneus em formato Json.
     * @throws Throwable Se algum erro ocorrer.
     */
    void importPlanilhaPneus(@NotNull final Long codEmpresa,
                             @NotNull final Long codUnidade,
                             @NotNull final String usuario,
                             @NotNull final String jsonPlanilha) throws Throwable;
}
