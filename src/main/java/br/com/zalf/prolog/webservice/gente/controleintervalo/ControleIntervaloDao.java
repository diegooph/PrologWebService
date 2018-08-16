package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.IntervaloMarcacao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Created on 08/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface ControleIntervaloDao {

    void insertMarcacaoIntervalo(@NotNull final IntervaloMarcacao intervaloMarcacao) throws SQLException;

    @Nullable
    IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(@NotNull final Long codUnidade,
                                                        @NotNull final Long cpf,
                                                        @NotNull final Long codTipoIntervalo) throws SQLException;

    @Nonnull
    @NotNull
    List<Intervalo> getMarcacoesIntervaloColaborador(@NotNull final Long codUnidade,
                                                     @NotNull final Long cpf,
                                                     @NotNull final String codTipo,
                                                     final long limit,
                                                     final long offset) throws SQLException;

    @NotNull
    Long insertTipoIntervalo(@NotNull final TipoIntervalo tipoIntervalo,
                             @NotNull final DadosIntervaloChangedListener listener) throws Throwable;

    void updateTipoIntervalo(@NotNull final TipoIntervalo tipoIntervalo,
                             @NotNull final DadosIntervaloChangedListener listener) throws Throwable;

    @NotNull
    List<TipoIntervalo> getTiposIntervalosByUnidade(@NotNull final Long codUnidade,
                                                    final Boolean apenasAtivos,
                                                    final boolean withCargos) throws SQLException;

    @NotNull
    TipoIntervalo getTipoIntervalo(@NotNull final Long codUnidade,
                                   @NotNull final Long codTipoIntervalo) throws SQLException;

    void inativarTipoIntervalo(@NotNull final Long codUnidade,
                               @NotNull final Long codTipoIntervalo,
                               @NotNull final TipoIntervalo tipoIntervalo,
                               @NotNull final DadosIntervaloChangedListener listener) throws Throwable;

    @NotNull
    Optional<Long> getVersaoDadosIntervaloByUnidade(@NotNull final Long codUnidade) throws SQLException;
}