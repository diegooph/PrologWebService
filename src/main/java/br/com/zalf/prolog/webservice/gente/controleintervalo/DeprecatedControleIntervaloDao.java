package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

/**
 * Created by Zart on 18/08/2017.
 */
@Deprecated
public interface DeprecatedControleIntervaloDao {

    @Nullable
    @Deprecated
    Long iniciaIntervalo(@NotNull final Long codUnidade,
                         @NotNull final Long cpf,
                         @NotNull final Long codTipoIntervalo) throws SQLException;

    @Deprecated
    boolean insereFinalizacaoIntervalo(@NotNull final Long codUnidade,
                                       @NotNull final Intervalo intervalo) throws SQLException;
}