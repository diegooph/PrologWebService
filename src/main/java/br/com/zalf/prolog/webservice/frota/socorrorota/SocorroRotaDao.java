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

    /**
     * Cria uma nova solicitação de socorro no banco de dados.
     *
     * @param socorroRotaAbertura Objeto contendo as informações para abertura de um socorro.
     * @return Código gerado pelo BD para a nova solicitação de socorro.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Long aberturaSocorro(@NotNull final SocorroRotaAbertura socorroRotaAbertura) throws Throwable;

    @NotNull
    List<ColaboradorNotificacaoAberturaSocorro> getColaboradoresNotificacaoAbertura(@NotNull final Long codUnidade)
            throws Throwable;

    /**
     * Busca as unidades disponíveis para a abertura de socorro em rota por colaborador
     *
     * @param codColaborador Código do colaborador
     * @return Uma lista de {@link UnidadeAberturaSocorro unidades} que o colaborador tem acesso
     * @throws Throwable Se algo der errado na busca.
     */
    @NotNull
    List<UnidadeAberturaSocorro> getUnidadesDisponiveisAberturaSocorroByCodColaborador(
            @NotNull final Long codColaborador) throws Throwable;

    /**
     * Busca os veículos disponíveis para a abertura de socorro em rota por unidade
     *
     * @param codUnidade Código da unidade
     * @return Uma lista de {@link VeiculoAberturaSocorro veículos} ativos por unidade
     * @throws Throwable Se algo der errado na busca.
     */
    @NotNull
    List<VeiculoAberturaSocorro> getVeiculosDisponiveisAberturaSocorroByUnidade(@NotNull final Long codUnidade)
            throws Throwable;

    /**
     * Busca a listagem dos socorros em rota por data inicial, final e unidades
     *
     * @param codUnidades Array com os códigos de unidade
     * @param dataInicial Data inicial da pesquisa
     * @param dataFinal   Data final da pesquisa
     * @return Uma lista de {@link SocorroRotaListagem socorros em rota}
     * @throws Throwable Se algo der errado na busca.
     */
    @NotNull
    List<SocorroRotaListagem> getListagemSocorroRota(@NotNull final List<Long> codUnidades,
                                                     @NotNull final LocalDate dataInicial,
                                                     @NotNull final LocalDate dataFinal,
                                                     @NotNull final String userToken) throws Throwable;

    /**
     * Invalida uma solicitação de socorro no banco de dados.
     *
     * @param socorroRotaInvalidacao Objeto contendo as informações para invalidação de um socorro.
     * @return Código gerado pelo BD para a invalidação do socorro.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Long invalidacaoSocorro(@NotNull final SocorroRotaInvalidacao socorroRotaInvalidacao) throws Throwable;

    /**
     * Atende uma solicitação de socorro no banco de dados.
     *
     * @param socorroRotaAtendimento Objeto contendo as informações para atendimento de um socorro.
     * @return Código gerado pelo BD para o atendimento do socorro.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Long atendimentoSocorro(@NotNull final SocorroRotaAtendimento socorroRotaAtendimento) throws Throwable;

    /**
     * Finaliza uma solicitação de socorro no banco de dados.
     *
     * @param socorroRotaFinalizacao Objeto contendo as informações para finalização de um socorro.
     * @return Código gerado pelo BD para a finalização do socorro.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Long finalizacaoSocorro(@NotNull final SocorroRotaFinalizacao socorroRotaFinalizacao) throws Throwable;

    /**
     * Busca as informações do socorro em rota através do código
     *
     * @param codSocorroRota código do socorro em rota a ser buscado
     * @return Uma visualização geral de {@link SocorroRotaVisualizacao socorro em rota}
     * @throws Throwable Se algo der errado na busca.
     */
    @NotNull
    SocorroRotaVisualizacao getVisualizacaoSocorroRota(@NotNull final Long codSocorroRota) throws Throwable;
}