package br.com.zalf.prolog.webservice.gente.controlejornada;

import br.com.zalf.prolog.webservice.gente.controlejornada.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created on 08/11/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ControleJornadaDao {

    @NotNull
    Long insertMarcacaoIntervalo(@NotNull final IntervaloMarcacao intervaloMarcacao) throws Throwable;

    @Nullable
    IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(@NotNull final Long codUnidade,
                                                        @NotNull final Long cpfColaborador,
                                                        @NotNull final Long codTipoIntervalo) throws Throwable;

    void insereMarcacaoInicioOuFim(@NotNull final Connection conn,
                                   @NotNull final Long codMarcacaoInserida,
                                   @NotNull final TipoInicioFim tipoInicioFim) throws Throwable;

    void insereVinculoInicioFim(@NotNull final Connection conn,
                                @NotNull final Long codMarcacaoInicio,
                                @NotNull final Long codMarcacaoFim) throws Throwable;

    @NotNull
    List<Intervalo> getMarcacoesIntervaloColaborador(@NotNull final Long codUnidade,
                                                     @NotNull final Long cpf,
                                                     @NotNull final String codTipo,
                                                     final long limit,
                                                     final long offset) throws Throwable;

    @NotNull
    List<MarcacaoListagem> getMarcacoesColaboradorPorData(@NotNull final Long codUnidade,
                                                          @Nullable final Long cpf,
                                                          @Nullable final Long codTipo,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws Throwable;

    boolean verifyIfTokenMarcacaoExists(@NotNull final String tokenMarcacaoJornada) throws Throwable;

    @NotNull
    Optional<DadosMarcacaoUnidade> getDadosMarcacaoUnidade(@NotNull final Long codUnidade) throws Throwable;

    boolean isMarcacaoInicioFinalizada(@NotNull final Long codMarcacao) throws Throwable;
}