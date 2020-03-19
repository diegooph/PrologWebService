package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoInsercao;
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
     * Insere um {@link MotivoInsercao colaborador} no banco de dados.
     * <p>
     * Todos os dados são obrigatórios.
     *
     * @param motivoInsercao Dados do motivo de troca a ser inserido.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    Long insert(@NotNull final MotivoInsercao motivoInsercao) throws Throwable;

    /**
     * Busca um motivo baseado no seu código.
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
     * Busca todos os motivos baseado no código da empresa.
     *
     * @param codEmpresa        um código de uma empresa;
     * @param apenasAtivos      null, true ou false. Se true, traz apenas os ativos. Qualquer outro, traz todos.
     * @param tokenAutenticacao token de autorização do header da requisição.
     * @return uma {@link List<MotivoVisualizacaoListagem> lista de motivos}.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    List<MotivoVisualizacaoListagem> getMotivosListagem(@NotNull final Long codEmpresa,
                                                        @Nullable final Boolean apenasAtivos,
                                                        @NotNull String tokenAutenticacao) throws Throwable;

    @Nullable
    void update(@NotNull MotivoEdicao motivoEdicao);

}
