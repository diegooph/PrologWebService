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

    /**
     * Busca os dados das marcações dos colaboradores respeitando os parâmetros de filtros e no padrão da portaria 1510.
     *
     * @param tokenIntegracao O token de integração que está requisitando as informações.
     * @param dataInicial Data inicial do período de busca.
     * @param dataFinal Data final do período de busca.
     * @param codUnidadeProLog Código da unidade do ProLog para busca dos dados, opcional.
     * @param codTipoMarcacao Código do tipo de marcação para busca dos dados, opcional.
     * @param cpfColaborador CPF do colaborador para busca dos dados, opcional.
     * @return Uma lista contendo todas as marcações no padrão da portaria 1510.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    List<ApiMarcacaoRelatorio1510> getRelatorioPortaria1510(
            @NotNull final String tokenIntegracao,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal,
            @Nullable final Long codUnidadeProLog,
            @Nullable final Long codTipoMarcacao,
            @Nullable final String cpfColaborador) throws Throwable;
}
