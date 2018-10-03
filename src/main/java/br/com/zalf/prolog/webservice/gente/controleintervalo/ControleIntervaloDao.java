package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.IntervaloMarcacao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoMarcacao;
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

    /**
     * Método utilizado para sincronizar no Banco de Dados uma Marcação.
     *
     * @param intervaloMarcacao Uma {@link IntervaloMarcacao marcação} contendo os dados a serem inseridos.
     * @return Código único que identifica a marcação no banco de dados.
     * @throws SQLException Se ocorrer algum erro na sincronização.
     */
    @NotNull
    Long insertMarcacaoIntervalo(@NotNull final IntervaloMarcacao intervaloMarcacao) throws SQLException;

    /**
     * Método utilizado para buscar a versão em que os dados dos Intervalos se encontram.
     *
     * @param codUnidade Código da unidade de onde a versão será buscada.
     * @return Valor {@link Long} que representa a versão atual dos dados de Intervalos.
     * @throws SQLException Se algum erro ocorrer na busca dos dados.
     */
    @NotNull
    Optional<Long> getVersaoDadosIntervaloByUnidade(@NotNull final Long codUnidade) throws SQLException;

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