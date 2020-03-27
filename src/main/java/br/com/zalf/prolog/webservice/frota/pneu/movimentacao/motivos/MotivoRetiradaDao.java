package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * @param tokenAutenticacao      o token de sessão do usuário;
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    Long insert(@NotNull final MotivoRetiradaInsercao motivoRetiradaInsercao,
                String tokenAutenticacao) throws Throwable;

    /**
     * Busca um {@link MotivoRetiradaVisualizacao motivo} baseado no seu código.
     *
     * @param codMotivo         um código de um motivo.
     * @param tokenAutenticacao token de autorização do header da requisição.
     * @return uma {@link MotivoRetiradaVisualizacao motivo}.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    MotivoRetiradaVisualizacao getMotivoByCodigo(@NotNull Long codMotivo,
                                                 @NotNull final String tokenAutenticacao) throws Throwable;

    /**
     * Busca todos os {@link MotivoRetiradaListagem motivos} baseado no código da empresa.
     *
     * @param codEmpresa        um código de uma empresa;
     * @param tokenAutenticacao token de autorização do header da requisição.
     * @return uma {@link List< MotivoRetiradaListagem > lista de motivos}.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    List<MotivoRetiradaListagem> getMotivosListagem(@NotNull final Long codEmpresa,
                                                    @NotNull String tokenAutenticacao) throws Throwable;

    /**
     * Realiza a atualização de um {@link MotivoRetiradaEdicao motivo}.
     *
     * @param motivoRetiradaEdicao um objeto de edição do motivo, com as informações necessárias para atualização.
     * @param tokenAutenticacao    o token de sessão do usuário;
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @Nullable
    void update(@NotNull MotivoRetiradaEdicao motivoRetiradaEdicao,
                @NotNull final String tokenAutenticacao) throws Throwable;

    /**
     * Realiza a busca de uma lista do histórico de um {@link MotivoRetiradaHistoricoListagem motivo}.
     *
     * @param codMotivoRetirada um código que será utilizado para buscar o histórico.
     * @param tokenAutenticacao o token de sessão do usuário;
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    List<MotivoRetiradaHistoricoListagem> getHistoricoByMotivo(@NotNull Long codMotivoRetirada,
                                                               @NotNull String tokenAutenticacao);

}
