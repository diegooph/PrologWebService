package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.pneu;

import br.com.zalf.prolog.webservice.interno.implantacao.conferencia._model.TipoImport;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 19/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuConferenciaDao {

    void importPlanilhaPneus(@NotNull final Long codEmpresa,
                             @NotNull final Long codUnidade,
                             @NotNull final String usuario,
                             @NotNull final String jsonPlanilha,
                             @NotNull final TipoImport tipoImportPneu) throws Throwable;
}
