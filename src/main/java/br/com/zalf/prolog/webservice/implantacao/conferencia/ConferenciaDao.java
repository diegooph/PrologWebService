package br.com.zalf.prolog.webservice.implantacao.conferencia;

import br.com.zalf.prolog.webservice.implantacao.conferencia._model.ConferenciaDadosTabelaImport;
import br.com.zalf.prolog.webservice.implantacao.conferencia._model.TipoImport;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/12/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface ConferenciaDao {

    ConferenciaDadosTabelaImport createDadosTabelaImport(@NotNull final Long codEmpresa,
                                                         @NotNull final Long codUnidade,
                                                         @NotNull final String usuario,
                                                         @NotNull final TipoImport tipoImport) throws Throwable;
}
