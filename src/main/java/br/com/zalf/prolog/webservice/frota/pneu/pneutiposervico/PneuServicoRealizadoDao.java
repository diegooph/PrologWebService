package br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico;

import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created on 05/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface PneuServicoRealizadoDao {

    @NotNull
    Long insertServicoByMovimentacao(@NotNull final Connection conn,
                                     @NotNull final PneuDao pneuDao,
                                     @NotNull final Long codUnidade,
                                     @NotNull final Pneu pneu,
                                     @NotNull final PneuServicoRealizado servicoRealizado) throws Throwable;

    @NotNull
    Long insertServicoByPneuCadastro(@NotNull final Connection conn,
                                     @NotNull final Long codUnidade,
                                     @NotNull final Long codPneu,
                                     @NotNull final PneuServicoRealizado servicoRealizado) throws Throwable;
}
