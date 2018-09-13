package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ControleJornadaAjusteDaoImpl implements ControleJornadaAjusteDao {
    @Override
    public void ajustaMarcacaoAdicao(@NotNull final Long codUnidade,
                                     @NotNull final MarcacaoAjusteAdicao marcacaoAjuste,
                                     @NotNull final String token) throws Throwable {

    }

    @Override
    public void ajustaMarcacaoAdicaoInicioFim(@NotNull final Long codUnidade,
                                              @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste,
                                              @NotNull final String token) throws Throwable {

    }

    @Override
    public void ajustaMarcacaoAtivacaoInativacao(@NotNull final Long codUnidade,
                                                 @NotNull final Long codMarcacao,
                                                 @NotNull final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste,
                                                 @NotNull final String token) throws Throwable {

    }

    @Override
    public void ajustaMarcacaoEdicao(@NotNull final Long codUnidade,
                                     @NotNull final Long codMarcacao,
                                     @NotNull final MarcacaoAjusteEdicao marcacaoAjuste,
                                     @NotNull final String token) throws Throwable {

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
    public List<MarcacaoAjusteHistorico> getMarcacaoAjusteHistorio(@NotNull final Long codMarcacao) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoInconsistenciaExibicao> getMarcacoesInconsistentes(@NotNull final Long codMarcacao) throws Throwable {
        return null;
    }
}
