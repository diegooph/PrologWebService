package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.gente.colaborador;

import br.com.zalf.prolog.webservice.interno.implantacao.conferencia._model.TipoImport;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 29/07/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface ColaboradorConferenciaDao {

    void importPlanilhaColaborador(@NotNull final Long codEmpresa,
                                   @NotNull final Long codUnidade,
                                   @NotNull final String usuario,
                                   @NotNull final String jsonPlanilha,
                                   @NotNull final TipoImport tipoImportColaborador) throws Throwable;
}


