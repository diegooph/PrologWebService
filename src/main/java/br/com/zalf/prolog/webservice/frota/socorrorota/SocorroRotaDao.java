package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.frota.socorrorota._model.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface SocorroRotaDao {

    @NotNull
    Long aberturaSocorro(@NotNull final SocorroRotaAbertura socorroRotaAbertura) throws Throwable;

    @NotNull
    List<ColaboradorNotificacaoAberturaSocorroRota> getColaboradoresNotificacaoAbertura(
            @NotNull final Long codUnidade) throws Throwable;

    @NotNull
    List<ColaboradorNotificacaoAtendimentoSocorroRota> getColaboradoresNotificacaoAtendimento(
            @NotNull final Long codSocorroRota) throws Throwable;

    @NotNull
    List<ColaboradorNotificacaoInvalidacaoSocorroRota> getColaboradoresNotificacaoInvalidacao(
            @NotNull final Long codColaboradorInvalidacao,
            @NotNull final Long codSocorroRota) throws Throwable;

    @NotNull
    List<UnidadeAberturaSocorro> getUnidadesDisponiveisAberturaSocorroByCodColaborador(
            @NotNull final Long codColaborador) throws Throwable;

    @NotNull
    List<VeiculoAberturaSocorro> getVeiculosDisponiveisAberturaSocorroByUnidade(@NotNull final Long codUnidade)
            throws Throwable;

    @NotNull
    List<SocorroRotaListagem> getListagemSocorroRota(@NotNull final List<Long> codUnidades,
                                                     @NotNull final LocalDate dataInicial,
                                                     @NotNull final LocalDate dataFinal,
                                                     @NotNull final String userToken) throws Throwable;

    @NotNull
    Long invalidacaoSocorro(@NotNull final SocorroRotaInvalidacao socorroRotaInvalidacao) throws Throwable;

    @NotNull
    Long atendimentoSocorro(@NotNull final SocorroRotaAtendimento socorroRotaAtendimento) throws Throwable;

    void iniciaDeslocamento(@NotNull final SocorroRotaAtendimentoDeslocamento deslocamentoInicio) throws Throwable;

    void finalizaDeslocamento(@NotNull final SocorroRotaAtendimentoDeslocamento deslocamentoFim) throws Throwable;

    @NotNull
    Long finalizacaoSocorro(@NotNull final SocorroRotaFinalizacao socorroRotaFinalizacao) throws Throwable;

    @NotNull
    SocorroRotaVisualizacao getVisualizacaoSocorroRota(@NotNull final Long codColaboradorRequest,
                                                       @NotNull final Long codSocorroRota) throws Throwable;
}