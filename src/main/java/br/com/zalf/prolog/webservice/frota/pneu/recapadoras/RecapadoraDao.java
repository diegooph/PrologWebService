package br.com.zalf.prolog.webservice.frota.pneu.recapadoras;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 13/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface RecapadoraDao {

    @NotNull
    Long insertRecapadora(@NotNull final String token, @NotNull final Recapadora recapadora) throws SQLException;

    void atualizaRecapadoras(@NotNull final Long codEmpresa, @NotNull final Recapadora recapadora) throws SQLException;

    List<Recapadora> getRecapadoras(@NotNull final Long codEmpresa, final Boolean ativas) throws SQLException;

    Recapadora getRecapadora(Long codEmpresa, Long codRecapadora) throws SQLException;

    void alterarStatusRecapadoras(@NotNull final String token,
                                  @NotNull final Long codEmpresa,
                                  @NotNull final List<Recapadora> recapadoras) throws SQLException;
}