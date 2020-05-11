package br.com.zalf.prolog.webservice.integracao.api.afericao;

import br.com.zalf.prolog.webservice.integracao.api.afericao._model.ApiPneuMedicaoRealizada;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
public interface ApiAfericaoDao {

    /**
     * Método responsável por buscar todas as aferições realizadas a partir do {@code codUltimaAfericao} ou do
     * {@code dataHoraUltimaAtualizacaoUtc} recebido.
     * Os parâmetros recebidos, são utilizados como um Offset de busca, todas as aferições a partir de tal parâmetro
     * serão retornadas por este método.
     *
     * @param tokenIntegracao              Token utilizado para a requisição. Este token será utilizado para
     *                                     descobrir qual empresa está requisitando as informações.
     * @param codigoProcessoAfericao       Código da Última aferição sincronizada.
     * @param dataHoraUltimaAtualizacaoUtc Data e hora da ultima aferição realizada.
     * @return Uma lista de {@link List<ApiPneuMedicaoRealizada> aferições}.
     * @throws Throwable Se algum erro ocorrer durante a busca das novas aferições.
     */
    List<ApiPneuMedicaoRealizada> getAfericoesRealizadas(@NotNull final String tokenIntegracao,
                                                         final Long codigoProcessoAfericao,
                                                         final LocalDateTime dataHoraUltimaAtualizacaoUtc) throws Throwable;
}
