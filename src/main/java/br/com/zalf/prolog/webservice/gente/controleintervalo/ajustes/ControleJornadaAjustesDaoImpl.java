package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoAjuste;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoColaboradorAjuste;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoConsolidada;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoInconsistencia;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ControleJornadaAjustesDaoImpl implements ControleJornadaAjustesDao {

    @Override
    public void insereMarcacaoCompletaNoDia(
            @NotNull final Long codUnidade,
            @NotNull final String token,
            @NotNull final MarcacaoAjusteAdicaoInicioFim ajusteAdicaoInicio) throws Throwable {

    }

    @NotNull
    @Override
    public List<MarcacaoConsolidada> getMarcacoesConsolidadas(@NotNull final Long codUnidade,
                                                              @NotNull final String codColaborador,
                                                              @NotNull final String codTipoIntervalo,
                                                              @NotNull final LocalDate dataInicial,
                                                              @NotNull final LocalDate dataFinal) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoColaboradorAjuste> getMarcacoesColaboradorParaAjuste(
            @NotNull final Long codUnidade,
            @NotNull final String codColaborador,
            @NotNull final LocalDate dataInicial) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoAjuste> getMarcacaoAjusteHistorio(@NotNull final Long codMarcacao) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoInconsistencia> getMarcacoesInconsistentes(@NotNull final Long codMarcacao) throws Throwable {
        return null;
    }

    @Override
    public void ajustaMarcacao(@NotNull final Long codUnidade,
                               @NotNull final Long codMarcacao,
                               @NotNull final String token,
                               @NotNull final MarcacaoAjuste marcacaoAjuste) throws Throwable {

    }
}
