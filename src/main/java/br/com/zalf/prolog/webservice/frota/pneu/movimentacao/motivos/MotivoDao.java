package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoListagemApp;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2020-03-17
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface MotivoDao {

    /**
     * Insere um {@link MotivoInsercao motivo} no banco de dados.
     * <p>
     * Todos os dados são obrigatórios.
     *
     * @param motivoInsercao    Dados do motivo de troca a ser inserido.
     * @param tokenAutenticacao o token de sessão do usuário;
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    Long insert(@NotNull final MotivoInsercao motivoInsercao, String tokenAutenticacao) throws Throwable;

    /**
     * Busca um {@link MotivoVisualizacaoListagem motivo} baseado no seu código.
     *
     * @param codMotivo         um código de um motivo.
     * @param tokenAutenticacao token de autorização do header da requisição.
     * @return uma {@link MotivoVisualizacaoListagem motivo}.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    MotivoVisualizacaoListagem getMotivoByCodigo(@NotNull Long codMotivo,
                                                 @NotNull final String tokenAutenticacao) throws Throwable;

    /**
     * Busca todos os {@link MotivoVisualizacaoListagem motivos} baseado no código da empresa.
     *
     * @param codEmpresa        um código de uma empresa;
     * @param tokenAutenticacao token de autorização do header da requisição.
     * @return uma {@link List<MotivoVisualizacaoListagem> lista de motivos}.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    List<MotivoVisualizacaoListagem> getMotivosListagem(@NotNull final Long codEmpresa,
                                                        @NotNull String tokenAutenticacao) throws Throwable;

    /**
     * Realiza a atualização de um {@link MotivoEdicao motivo}.
     *
     * @param motivoEdicao      um objeto de edição do motivo, com as informações necessárias para atualização.
     * @param tokenAutenticacao o token de sessão do usuário;
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @Nullable
    void update(@NotNull MotivoEdicao motivoEdicao, @NotNull final String tokenAutenticacao) throws Throwable;

    /**
     * Realiza a deleção de um motivo com base no ID.
     *
     * @param codMotivo um código de motivo.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @Nullable
    void delete(@NotNull final Long codMotivo, @NotNull final String tokenAutenticacao) throws Throwable;

    @NotNull
    List<MotivoListagemApp> getMotivosByOrigemAndDestino(@NotNull final OrigemDestinoEnum origem,
                                                         @NotNull final OrigemDestinoEnum destino);

}
