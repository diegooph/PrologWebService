package br.com.zalf.prolog.webservice.colaborador.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.List;

/**
 * Created on 05/04/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class ColaboradorRelatorioDaoImpl implements ColaboradorRelatorioDao {
    @Override
    public void getListagemColaboradoresByUnidadeCsv(@NotNull final OutputStream out,
                                                     @NotNull final List<Long> codUnidades,
                                                     @NotNull final String userToken) {

    }

    @Override
    public Report getListagemColaboradoresByUnidadeReport(@NotNull final List<Long> codUnidades,
                                                          @NotNull final String userToken) {
        return null;
    }
}
