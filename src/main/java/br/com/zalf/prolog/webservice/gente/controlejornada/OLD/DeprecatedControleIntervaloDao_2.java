package br.com.zalf.prolog.webservice.gente.controlejornada.OLD;

import br.com.zalf.prolog.webservice.gente.controlejornada.DadosIntervaloChangedListener;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.IntervaloMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoMarcacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 08/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Deprecated
public interface DeprecatedControleIntervaloDao_2 {

    void insertMarcacaoIntervalo(@NotNull final IntervaloMarcacao intervaloMarcacao) throws SQLException;

    @Nullable
    IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(@NotNull final Long codUnidade,
                                                        @NotNull final Long cpf,
                                                        @NotNull final Long codTipoIntervalo) throws SQLException;

    @NotNull
    Long insertTipoIntervalo(@NotNull final TipoMarcacao tipoIntervalo,
                             @NotNull final DadosIntervaloChangedListener listener) throws Throwable;

    void updateTipoIntervalo(@NotNull final TipoMarcacao tipoIntervalo,
                             @NotNull final DadosIntervaloChangedListener listener) throws Throwable;

    @NotNull
    List<TipoMarcacao> getTiposIntervalosByUnidade(@NotNull final Long codUnidade,
                                                    final boolean apenasAtivos,
                                                    final boolean withCargos) throws SQLException;

    @NotNull
    TipoMarcacao getTipoIntervalo(@NotNull final Long codUnidade,
                                   @NotNull final Long codTipoIntervalo) throws SQLException;

    void updateStatusAtivoTipoIntervalo(@NotNull final Long codUnidade,
                                        @NotNull final Long codTipoIntervalo,
                                        @NotNull final TipoMarcacao tipoIntervalo,
                                        @NotNull final DadosIntervaloChangedListener listener) throws Throwable;
}