package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ControleJornadaAjustesDao {

    void insereMarcacaoCompletaNoDia(@NotNull final Long codUnidade,
                                     @NotNull final String codColaborador,
                                     @NotNull final String codTipoIntervalo,
                                     @NotNull final String token,
                                     @NotNull final MarcacaoAjusteAdicaoInicioFim ajusteAdicaoInicio) throws Throwable;

    @NotNull
    List<MarcacaoConsolidada> getMarcacoesConsolidadas(@NotNull final Long codUnidade,
                                                       @NotNull final String codColaborador,
                                                       @NotNull final String codTipoIntervalo,
                                                       @NotNull final LocalDate dataInicial,
                                                       @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    List<MarcacaoColaboradorAjuste> getMarcacoesColaboradorParaAjuste(@NotNull final Long codUnidade,
                                                                      @NotNull final String codColaborador,
                                                                      @NotNull final LocalDate dataInicial) throws Throwable;

    @NotNull
    List<MarcacaoAjuste> getMarcacaoAjusteHistorio(@NotNull final Long codMarcacao) throws Throwable;

    @NotNull
    List<MarcacaoInconsistencia> getMarcacoesInconsistentes(@NotNull final Long codMarcacao) throws Throwable;

    void ajustaMarcacao(@NotNull final Long codUnidade,
                        @NotNull final Long codMarcacao,
                        @NotNull final String token,
                        @NotNull final MarcacaoAjuste marcacaoAjuste) throws Throwable;
}
