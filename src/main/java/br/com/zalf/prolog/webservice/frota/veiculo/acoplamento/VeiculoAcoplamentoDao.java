package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.VeiculoAcoplamentoResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created on 2020-11-03
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VeiculoAcoplamentoDao {
    /**
     * Busca os processos de acoplamentos com base nos parâmetros fornecidos.
     *
     * @param codUnidades Códigos das unidades para as quais as informações serão filtradas.
     * @param codVeiculos Códigos dos veículos para os quais as informações serão filtradas.
     * @param dataInicial Data inicial para a qual as informações serão filtradas.
     * @param dataFinal   Data final para a qual as informações serão filtradas.
     * @return os processos de acoplamentos realizados com base nos parâmetros fornecidos.
     * @throws Throwable Se algum erro ocorrer.
     */
    Optional<VeiculoAcoplamentoResponse> getVeiculoAcoplamentos(@NotNull final List<Long> codUnidades,
                                                                @Nullable final List<Long> codVeiculos,
                                                                @Nullable final LocalDate dataInicial,
                                                                @Nullable final LocalDate dataFinal) throws Throwable;
}
