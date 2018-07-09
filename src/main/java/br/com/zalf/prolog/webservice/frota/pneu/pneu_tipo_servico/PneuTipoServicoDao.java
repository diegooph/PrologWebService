package br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico;

import br.com.zalf.prolog.webservice.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico.model.PneuTipoServico;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 24/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface PneuTipoServicoDao {

    /**
     * Insere um tipo de serviço para ser utilizado na movimentação de pneus com {@link OrigemAnalise}.
     *
     * @param token       - Utilizado para buscar o id do usuário que está solicitando a operação.
     * @param tipoServico - {@link PneuTipoServico} para inserir no Banco de Dados.
     * @return - Um código único para o {@link PneuTipoServico}.
     * @throws Throwable - Se algum erro ocorrer na execução da função.
     */
    @NotNull
    Long insertPneuTipoServico(@NotNull final String token,
                               @NotNull final PneuTipoServico tipoServico) throws Throwable;

    /**
     * Atualiza um {@link PneuTipoServico} com os novos dados inseridos pelo usuário.
     *
     * @param token       - Utilizado para buscar o id do usuário que está solicitando a operação.
     * @param codEmpresa  - Código da {@link Empresa}.
     * @param tipoServico - {@link PneuTipoServico} que será inserido.
     * @throws Throwable - Se algum erro ocorrer na execução da função.
     */
    void atualizaPneuTipoServico(@NotNull final String token,
                                 @NotNull final Long codEmpresa,
                                 @NotNull final PneuTipoServico tipoServico) throws Throwable;

    /**
     * Busca todos os {@link PneuTipoServico} vinculados à {@link Empresa}.
     *
     * @param codEmpresa - Código da {@link Empresa}.
     * @param ativos     - {@link Boolean} para saber se buscaremos todos os tipos ou apenas os ativos.
     * @return - Um {@link List<PneuTipoServico>} com os tipos da empresa.
     * @throws Throwable - Se algum erro ocorrer na execução da função.
     */
    List<PneuTipoServico> getPneuTiposServicos(@NotNull final Long codEmpresa,
                                               @Nullable final Boolean ativos) throws Throwable;

    /**
     * Busca um {@link PneuTipoServico} específico através do {@link PneuTipoServico#codigo}.
     *
     * @param codEmpresa     - Código da {@link Empresa}.
     * @param codTipoServico - Código do {@link PneuTipoServico}.
     * @return - Um {@link PneuTipoServico} específico.
     * @throws Throwable - Se algum erro ocorrer na execução da função.
     */
    PneuTipoServico getPneuTipoServico(@NotNull final Long codEmpresa,
                                       @NotNull final Long codTipoServico) throws Throwable;

    /**
     * @param token       - Utilizado para buscar o id do usuário que está solicitando a operação.
     * @param codEmpresa  - Código da {@link Empresa}.
     * @param tipoServico - {@link PneuTipoServico} que será alterado.
     * @throws Throwable - Se algum erro ocorrer na execução da função.
     */
    void alterarStatusPneuTipoServico(@NotNull final String token,
                                      @NotNull final Long codEmpresa,
                                      @NotNull final PneuTipoServico tipoServico) throws Throwable;
}
