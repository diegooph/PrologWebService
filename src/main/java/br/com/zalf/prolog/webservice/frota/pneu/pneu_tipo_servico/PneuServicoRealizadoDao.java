package br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico.model.PneuServicoRealizado;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created on 05/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface PneuServicoRealizadoDao {

    /**
     * Processo de inserção de um {@link PneuServicoRealizado} disparado através de uma movimentação.
     * Insere no banco de dados um {@link PneuServicoRealizado} contendo os dados
     * referentes ao serviço executado no {@link Pneu}.
     *
     * @param conn             - {@link Connection} que será utilizada para conectar ao banco de dados.
     * @param codUnidade       - Código da {@link Unidade} que o serviço foi realizado.
     * @param codPneu          - Código do {@link Pneu} que o serviço foi realizado.
     * @param servicoRealizado - {@link PneuServicoRealizado} que foi realizado no {@link Pneu}.
     * @return - o código do {@link PneuServicoRealizado} inserido.
     * @throws SQLException - Se algum erro ocorrer na inserção.
     */
    Long insertServicoByMovimentacao(@NotNull final Connection conn,
                                     @NotNull final Long codUnidade,
                                     @NotNull final Long codPneu,
                                     @NotNull final PneuServicoRealizado servicoRealizado) throws SQLException;

    /**
     * Processo de inserção de um {@link PneuServicoRealizado} disparado através do cadastro
     * de um {@link Pneu} cujo não está na primeira vida.
     * Insere no banco de dados um {@link PneuServicoRealizado} contendo os dados
     * referentes ao serviço executado no {@link Pneu}.
     *
     * @param conn             - {@link Connection} que será utilizada para conectar ao banco de dados.
     * @param codUnidade       - Código da {@link Unidade} que o serviço foi realizado.
     * @param codPneu          - Código do {@link Pneu} que o serviço foi realizado.
     * @param servicoRealizado - {@link PneuServicoRealizado} que foi realizado no {@link Pneu}.
     * @return - o código do {@link PneuServicoRealizado} inserido.
     * @throws SQLException - Se algum erro ocorrer na inserção.
     */
    Long insertServicoByPneuCadastro(@NotNull final Connection conn,
                                     @NotNull final Long codUnidade,
                                     @NotNull final Long codPneu,
                                     @NotNull final PneuServicoRealizado servicoRealizado) throws SQLException;
}
