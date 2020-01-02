package br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
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
     * Processo de inserção de um {@link PneuServicoRealizado} disparado através de uma {@link Movimentacao}.
     * Insere no banco de dados um {@link PneuServicoRealizado} contendo os dados referentes ao serviço
     * executado no {@link Pneu}.
     *
     * @param conn             - {@link Connection} que será utilizada para conectar ao banco de dados.
     * @param pneuDao          - Obejto {@link PneuDao} utilizado para executar alterações no {@link Pneu}.
     * @param codUnidade       - Código da {@link Unidade} que o serviço foi realizado.
     * @param pneu             - {@link Pneu} que teve o serviço foi realizado.
     * @param servicoRealizado - {@link PneuServicoRealizado} que foi realizado no {@link Pneu}.
     * @return - o código do {@link PneuServicoRealizado} inserido.
     * @throws SQLException - Se algum erro ocorrer na inserção.
     */
    @NotNull
    Long insertServicoByMovimentacao(@NotNull final Connection conn,
                                     @NotNull final PneuDao pneuDao,
                                     @NotNull final Long codUnidade,
                                     @NotNull final Pneu pneu,
                                     @NotNull final PneuServicoRealizado servicoRealizado) throws Throwable;

    /**
     * Processo de inserção de um {@link PneuServicoRealizado} disparado através do cadastro de um {@link Pneu}
     * que não está na primeira vida.
     * Insere no banco de dados um {@link PneuServicoRealizado} contendo os dados referentes ao serviço executado
     * no {@link Pneu}.
     *
     * @param conn             - {@link Connection} que será utilizada para conectar ao banco de dados.
     * @param codUnidade       - Código da {@link Unidade} que o serviço foi realizado.
     * @param codPneu          - Código do {@link Pneu} que o serviço foi realizado.
     * @param servicoRealizado - {@link PneuServicoRealizado} que foi realizado no {@link Pneu}.
     * @return - o código do {@link PneuServicoRealizado} inserido.
     * @throws SQLException - Se algum erro ocorrer na inserção.
     */
    @NotNull
    Long insertServicoByPneuCadastro(@NotNull final Connection conn,
                                     @NotNull final Long codUnidade,
                                     @NotNull final Long codPneu,
                                     @NotNull final PneuServicoRealizado servicoRealizado) throws Throwable;
}
