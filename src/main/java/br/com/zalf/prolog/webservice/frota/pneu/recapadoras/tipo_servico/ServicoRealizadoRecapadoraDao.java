package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico.model.ServicoRealizadoRecapadora;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created on 05/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ServicoRealizadoRecapadoraDao {

    /**
     * Insere no banco de dados um {@link ServicoRealizadoRecapadora} contendo os dados
     * referentes ao serviço executado no {@link Pneu}.
     *
     * @param conn             - {@link Connection} que será utilizada para conectar ao banco de dados.
     * @param codUnidade       - Código da {@link Unidade} que o serviço foi realizado.
     * @param codPneu          - Código do {@link Pneu} que o serviço foi realizado.
     * @param servicoRealizado - {@link ServicoRealizadoRecapadora} que foi realizado no {@link Pneu}.
     * @return - o código do {@link ServicoRealizadoRecapadora} inserido.
     * @throws SQLException - Se algum erro ocorrer na inserção.
     */
    Long insert(@NotNull final Connection conn,
                @NotNull final Long codUnidade,
                @NotNull final Long codPneu,
                @NotNull final ServicoRealizadoRecapadora servicoRealizado) throws SQLException;
}
