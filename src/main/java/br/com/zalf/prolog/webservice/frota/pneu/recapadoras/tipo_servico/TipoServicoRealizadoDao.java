package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

/**
 * Created on 05/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface TipoServicoRealizadoDao {

    /**
     * Insere no banco de dados um {@link TipoServicoRecapadora} contendo os dados
     * referentes ao serviço executado no pneu.
     *
     * @param codUnidade  - Código da {@link Unidade} que o serviço foi realizado.
     * @param codPneu     - Código do {@link Pneu} que o serviço foi realizado.
     * @param tipoServico - {@link TipoServicoRecapadora} que foi realizado no {@link Pneu}.
     * @return - o código do {@link TipoServicoRecapadora} inserido.
     * @throws SQLException - Se algum erro ocorrer na inserção.
     */
    Long insert(@NotNull final Long codUnidade,
                @NotNull final Long codPneu,
                @NotNull final TipoServicoRecapadora tipoServico) throws SQLException;
}
