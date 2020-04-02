package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.util.List;

/**
 * Created on 2020-03-17
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface MotivoRetiradaDao {

    /**
     * Insere um {@link MotivoRetiradaInsercao motivo} no banco de dados.
     * <p>
     * Todos os dados são obrigatórios.
     *
     * @param motivoRetiradaInsercao Dados do motivo de troca a ser inserido.
     * @param codigoColaborador      O código do colaborador que fez a requisição.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    Long insert(@NotNull final MotivoRetiradaInsercao motivoRetiradaInsercao,
                @NotNull final Long codigoColaborador) throws Throwable;

    /**
     * Busca um {@link MotivoRetiradaVisualizacao motivo} baseado no seu código.
     *
     * @param codMotivo um código de um motivo.
     * @param timeZone  Time Zone do usuário que fez a requisição, para retornar a data e hora da sua região.
     * @return uma {@link MotivoRetiradaVisualizacao motivo}.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    MotivoRetiradaVisualizacao getMotivoByCodigo(@NotNull final Long codMotivo,
                                                 @NotNull final ZoneId timeZone) throws Throwable;

    /**
     * Busca todos os {@link MotivoRetiradaListagemResumida motivos} baseado no código da empresa.
     *
     * @param codEmpresa   um código de uma empresa.
     * @param apenasAtivos um booleano que indica se traz apenas ativos no resultado.
     * @param timeZone     o time zone do usuário o qual fez a requisição.
     * @return uma {@link List<MotivoRetiradaListagemResumida> lista de motivos}.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    List<MotivoRetiradaListagem> getMotivosListagem(@NotNull final Long codEmpresa,
                                                    final boolean apenasAtivos,
                                                    @NotNull final ZoneId timeZone) throws Throwable;

    /**
     * Realiza a atualização de um {@link MotivoRetiradaEdicao motivo}.
     *
     * @param motivoRetiradaEdicao um objeto de edição do motivo, com as informações necessárias para atualização.
     * @param codColaboradorUpdate o código do colaborador que está realizando a atualização no motivo.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    void update(@NotNull final MotivoRetiradaEdicao motivoRetiradaEdicao,
                @NotNull final Long codColaboradorUpdate) throws Throwable;

    /**
     * Realiza a busca de uma lista do histórico de um {@link MotivoRetiradaHistoricoListagem motivo}.
     *
     * @param codMotivoRetirada um código que será utilizado para buscar o histórico.
     * @param timeZone          time zone do usuário que fez a requisição.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    List<MotivoRetiradaHistoricoListagem> getHistoricoByMotivo(@NotNull final Long codMotivoRetirada,
                                                               @NotNull final ZoneId timeZone) throws Throwable;

}
