package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.vinculoveiculopneu;

import br.com.zalf.prolog.webservice.interno.implantacao.conferencia._model.TipoImport;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 31/08/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VinculoVeiculoPneuDao {

    void importPlanilhaVinculoVeiculoPneu(@NotNull final Long codEmpresa,
                                          @NotNull final Long codUnidade,
                                          @NotNull final String usuario,
                                          @NotNull final String jsonPlanilha,
                                          @NotNull final TipoImport tipoImportVinculo) throws Throwable;
}
