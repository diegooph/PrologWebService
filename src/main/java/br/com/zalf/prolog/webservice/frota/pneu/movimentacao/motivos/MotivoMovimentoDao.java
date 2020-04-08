package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.MotivoMovimentoUnidade;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.util.List;

/**
 * Created on 2020-03-17
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface MotivoMovimentoDao {

    /**
     * Insere um {@link MotivoMovimentoInsercao motivo} no banco de dados.
     * <p>
     * Todos os dados são obrigatórios.
     *
     * @param motivoMovimentoInsercao Dados do motivo de movimento a ser inserido.
     * @param codigoColaborador       O código do colaborador que fez a requisição.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    Long insert(@NotNull final MotivoMovimentoInsercao motivoMovimentoInsercao,
                @NotNull final Long codigoColaborador) throws Throwable;

    /**
     * Busca um {@link MotivoMovimentoVisualizacao motivo} baseado no seu código.
     *
     * @param codMotivo um código de um motivo.
     * @param timeZone  Time Zone do usuário que fez a requisição, para retornar a data e hora da sua região.
     * @return uma {@link MotivoMovimentoVisualizacao motivo}.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    MotivoMovimentoVisualizacao getMotivoByCodigo(@NotNull final Long codMotivo,
                                                  @NotNull final ZoneId timeZone) throws Throwable;

    /**
     * Busca todos os {@link MotivoMovimentoUnidade motivos} baseado no código da empresa.
     *
     * @param codEmpresa   um código de uma empresa.
     * @param apenasAtivos um booleano que indica se traz apenas ativos no resultado.
     * @param timeZone     o time zone do usuário o qual fez a requisição.
     * @return uma {@link List<MotivoMovimentoUnidade> lista de motivos}.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    List<MotivoMovimentoListagem> getMotivosListagem(@NotNull final Long codEmpresa,
                                                     final boolean apenasAtivos,
                                                     @NotNull final ZoneId timeZone) throws Throwable;

    /**
     * Realiza a atualização de um {@link MotivoMovimentoEdicao motivo}.
     *
     * @param motivoMovimentoEdicao um objeto de edição do motivo, com as informações necessárias para atualização.
     * @param codColaboradorUpdate  o código do colaborador que está realizando a atualização no motivo.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    void update(@NotNull final MotivoMovimentoEdicao motivoMovimentoEdicao,
                @NotNull final Long codColaboradorUpdate) throws Throwable;

    /**
     * Realiza a busca de uma lista do histórico de um {@link MotivoMovimentoHistoricoListagem motivo}.
     *
     * @param codMotivoMovimento um código que será utilizado para buscar o histórico.
     * @param timeZone           time zone do usuário que fez a requisição.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    List<MotivoMovimentoHistoricoListagem> getHistoricoByMotivo(@NotNull final Long codMotivoMovimento,
                                                                @NotNull final ZoneId timeZone) throws Throwable;

}
