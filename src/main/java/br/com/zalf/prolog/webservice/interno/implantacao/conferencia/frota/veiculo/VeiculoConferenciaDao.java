package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.interno.implantacao.conferencia._model.TipoImport;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VeiculoConferenciaDao {

    void importPlanilhaVeiculos(@NotNull final Long codEmpresa,
                                @NotNull final Long codUnidade,
                                @NotNull final String usuario,
                                @NotNull final String jsonPlanilha,
                                @NotNull final TipoImport tipoImportVeiculo) throws Throwable;
}