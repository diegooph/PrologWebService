package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes;

import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjuste;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjusteAdicaoInicioFim;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjusteEdicao;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.exibicao.ConsolidadoMarcacoesDia;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.exibicao.MarcacaoColaboradorAjuste;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.historico.MarcacaoAjusteHistoricoExibicao;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.inconsistencias.MarcacaoInconsistencia;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.inconsistencias.TipoInconsistenciaMarcacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ControleJornadaAjusteDao {

    @NotNull
    List<ConsolidadoMarcacoesDia> getMarcacoesConsolidadasParaAjuste(
            @NotNull final Long codUnidade,
            @Nullable final Long codTipoMarcacao,
            @Nullable final Long codColaborador,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    List<MarcacaoColaboradorAjuste> getMarcacoesColaboradorParaAjuste(
            @NotNull final Long codColaborador,
            @Nullable final Long codTipoMarcacao,
            @NotNull final LocalDate dia) throws Throwable;

    void adicionarMarcacaoAjuste(@NotNull final String tokenResponsavelAjuste,
                                 @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws Throwable;

    void adicionarMarcacaoAjusteInicioFim(@NotNull final String tokenResponsavelAjuste,
                                          @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste) throws Throwable;

    void editarMarcacaoAjuste(@NotNull final String tokenResponsavelAjuste,
                              @NotNull final MarcacaoAjusteEdicao marcacaoAjuste) throws Throwable;

    void ativarInativarMarcacaoAjuste(@NotNull final String tokenResponsavelAjuste,
                                      @NotNull final MarcacaoAjuste marcacaoAjuste,
                                      @NotNull final Long codMarcacao,
                                      final boolean deveAtivar) throws Throwable;

    @NotNull
    List<MarcacaoAjusteHistoricoExibicao> getHistoricoAjusteMarcacoes(
            @NotNull final List<Long> codMarcacoes) throws Throwable;

    @NotNull
    List<MarcacaoInconsistencia> getInconsistenciasColaboradorDia(
            @NotNull final Long codColaborador,
            @NotNull final LocalDate dia,
            @NotNull final TipoInconsistenciaMarcacao tipoInconsistencia) throws Throwable;
}