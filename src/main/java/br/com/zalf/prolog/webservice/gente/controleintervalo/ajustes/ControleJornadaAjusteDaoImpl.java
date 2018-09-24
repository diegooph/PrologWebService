package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.*;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.ConsolidadoMarcacoesDia;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoAjusteHistoricoExibicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoColaboradorAjuste;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoInconsistenciaExibicao;
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
    public void adicionarMarcacaoAjuste(@NotNull final MarcacaoAjusteAdicao marcacaoAjuste,
                                        @NotNull final String token) throws Throwable {

    }

    @Override
    public void adicionarMarcacaoAjusteInicioFim(@NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste,
                                                 @NotNull final String token) throws Throwable {

    }

    @Override
    public void ativarInativarMarcacaoAjuste(@NotNull final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste,
                                             @NotNull final String token) throws Throwable {

    }

    @Override
    public void editarMarcacaoAjuste(@NotNull final MarcacaoAjusteEdicao marcacaoAjuste,
                                     @NotNull final String token) throws Throwable {

    }

    @NotNull
    @Override
    public List<ConsolidadoMarcacoesDia> getMarcacoesConsolidadasParaAjuste(@NotNull final Long codUnidade,
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
            @NotNull final LocalDate data) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoAjusteHistoricoExibicao> getMarcacaoAjusteHistorio(@NotNull final Long codMarcacao) throws
            Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoInconsistenciaExibicao> getMarcacoesInconsistentes(@NotNull final Long codMarcacao) throws
            Throwable {
        return null;
    }
}