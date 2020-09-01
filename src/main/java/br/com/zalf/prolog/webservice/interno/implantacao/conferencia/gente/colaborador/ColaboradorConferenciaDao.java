package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.gente.colaborador;

import br.com.zalf.prolog.webservice.interno.implantacao.conferencia._model.TipoImport;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 29/07/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface ColaboradorConferenciaDao {

    /**
     * Método para retornar a conferência da planilha de import de colaborador.
     *
     * @param codEmpresa            Código da empresa para a qual as informações serão conferidas.
     * @param codUnidade            Código da unidade para a qual as informações serão conferidas.
     * @param jsonPlanilha          Informações da planilha de import de colaboradores em formato Json.
     * @param tipoImportColaborador Informação do tipo de import.
     * @throws Throwable Se algum erro ocorrer.
     */
    void importPlanilhaColaborador(@NotNull final Long codEmpresa,
                                   @NotNull final Long codUnidade,
                                   @NotNull final String usuario,
                                   @NotNull final String jsonPlanilha,
                                   @NotNull final TipoImport tipoImportColaborador) throws Throwable;
}


