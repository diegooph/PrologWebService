package br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico;

import br.com.zalf.prolog.webservice.commons.OrderByClause;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuTipoServico;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 24/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface PneuTipoServicoDao {

    @NotNull
    Long insertPneuTipoServico(@NotNull final String token,
                               @NotNull final PneuTipoServico tipoServico) throws Throwable;

    void atualizaPneuTipoServico(@NotNull final String token,
                                 @NotNull final Long codEmpresa,
                                 @NotNull final PneuTipoServico tipoServico) throws Throwable;

    @NotNull
    List<PneuTipoServico> getPneuTiposServicos(@NotNull final Long codEmpresa,
                                               @NotNull final List<OrderByClause> orderBy,
                                               @Nullable final Boolean ativos) throws Throwable;

    @NotNull
    PneuTipoServico getPneuTipoServico(@NotNull final Long codEmpresa,
                                       @NotNull final Long codTipoServico) throws Throwable;

    void alterarStatusPneuTipoServico(@NotNull final String token,
                                      @NotNull final Long codEmpresa,
                                      @NotNull final PneuTipoServico tipoServico) throws Throwable;
}
