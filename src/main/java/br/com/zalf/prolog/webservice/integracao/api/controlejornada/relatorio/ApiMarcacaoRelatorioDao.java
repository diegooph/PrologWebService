package br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio;

import br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio._model.ApiMarcacaoRelatorio1510;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 11/5/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiMarcacaoRelatorioDao {

    @NotNull
    List<ApiMarcacaoRelatorio1510> getRelatorioPortaria1510(
            @NotNull final String tokenIntegracao,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal,
            @Nullable final Long codUnidadeProLog,
            @Nullable final Long codTipoMarcacao,
            @Nullable final String cpfColaborador) throws Throwable;
}
