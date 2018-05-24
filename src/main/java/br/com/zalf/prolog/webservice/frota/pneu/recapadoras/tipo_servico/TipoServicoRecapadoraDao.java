package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico;

import br.com.zalf.prolog.webservice.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemAnalise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 24/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface TipoServicoRecapadoraDao {

    /**
     * Insere um tipo de serviço para ser utilizado na movimentação de pneus com {@link OrigemAnalise}.
     *
     * @param token       - Utilizado para buscar o id do usuário que está solicitando a operação.
     * @param tipoServico - {@link TipoServicoRecapadora} para inserir no Banco de Dados.
     * @return - Um código único para o {@link TipoServicoRecapadora}.
     * @throws SQLException - Se algum erro ocorrer na execução da função.
     */
    Long insertTipoServicoRecapadora(@NotNull final String token,
                                     @NotNull final TipoServicoRecapadora tipoServico) throws SQLException;

    /**
     * Atualiza um {@link TipoServicoRecapadora} com os novos dados inseridos pelo usuário.
     *
     * @param token       - Utilizado para buscar o id do usuário que está solicitando a operação.
     * @param codEmpresa  - Código da {@link Empresa}.
     * @param tipoServico - {@link TipoServicoRecapadora} que será inserido.
     * @throws SQLException - Se algum erro ocorrer na execução da função.
     */
    void atualizaTipoServicoRecapadora(@NotNull final String token,
                                       @NotNull final Long codEmpresa,
                                       @NotNull final TipoServicoRecapadora tipoServico) throws SQLException;

    /**
     * Busca todos os {@link TipoServicoRecapadora} vinculados à {@link Empresa}.
     *
     * @param codEmpresa - Código da {@link Empresa}.
     * @param ativas     - {@link Boolean} para saber se buscaremos todos os tipos ou apenas os ativos.
     * @return - Um {@link List<TipoServicoRecapadora>} com os tipos da empresa.
     * @throws SQLException - Se algum erro ocorrer na execução da função.
     */
    List<TipoServicoRecapadora> getTiposServicosRecapadora(@NotNull final Long codEmpresa,
                                                           @Nullable final Boolean ativas) throws SQLException;

    /**
     * Busca um {@link TipoServicoRecapadora} específico através do {@link TipoServicoRecapadora#codigo}.
     *
     * @param codEmpresa     - Código da {@link Empresa}.
     * @param codTipoServico - Código do {@link TipoServicoRecapadora}.
     * @return - Um {@link TipoServicoRecapadora} específico.
     * @throws SQLException - Se algum erro ocorrer na execução da função.
     */
    TipoServicoRecapadora getTipoServicoRecapadora(@NotNull final Long codEmpresa,
                                                   @NotNull final Long codTipoServico) throws SQLException;

    /**
     * @param token       - Utilizado para buscar o id do usuário que está solicitando a operação.
     * @param codEmpresa  - Código da {@link Empresa}.
     * @param tipoServico - {@link TipoServicoRecapadora} que será alterado.
     * @throws SQLException - Se algum erro ocorrer na execução da função.
     */
    void alterarStatusTipoServicoRecapadora(@NotNull final String token,
                                            @NotNull final Long codEmpresa,
                                            @NotNull final TipoServicoRecapadora tipoServico) throws SQLException;
}
