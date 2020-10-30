package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes;

import br.com.zalf.prolog.webservice.DummyData;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.*;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.exibicao.ConsolidadoMarcacoesDia;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.exibicao.MarcacaoColaboradorAjuste;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.historico.MarcacaoAjusteHistoricoExibicao;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.inconsistencias.InconsistenciaFimAntesInicio;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.inconsistencias.InconsistenciaSemVinculo;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.interceptors.debug.ResourceDebugOnly;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 06/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/dummies")
@ConsoleDebugLog
@ResourceDebugOnly
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DummyControleJornadaAjusteResource extends DummyData {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacoes-consolidadas-list")
    public List<ConsolidadoMarcacoesDia> getMarcacoesConsolidadas() {
        final List<ConsolidadoMarcacoesDia> marcacoesConsolidadas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            marcacoesConsolidadas.add(ConsolidadoMarcacoesDia.createDummy());
        }
        return marcacoesConsolidadas;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacoes-estratificadas-colaborador-list")
    public List<MarcacaoColaboradorAjuste> getMarcacoesColaboradorParaAjuste() {
        final List<MarcacaoColaboradorAjuste> marcacoesColaborador = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            marcacoesColaborador.add(MarcacaoColaboradorAjuste.createDummy());
        }
        return marcacoesColaborador;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacao-ajuste-adicao-inicio-fim")
    public MarcacaoAjusteAdicaoInicioFim getMarcacaoAjusteAdicaoInicioFim() {
        return MarcacaoAjusteAdicaoInicioFim.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacao-ajuste-adicao")
    public MarcacaoAjusteAdicao getMarcacaoAjusteAdicao() {
        return MarcacaoAjusteAdicao.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacao-ajuste-ativacao")
    public MarcacaoAjusteAtivacao getMarcacaoAjusteAtivacao() {
        return MarcacaoAjusteAtivacao.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacao-ajuste-inativacao")
    public MarcacaoAjusteInativacao getMarcacaoAjusteInativacao() {
        return MarcacaoAjusteInativacao.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacao-ajuste-edicao")
    public MarcacaoAjusteEdicao getMarcacaoAjusteEdicao() {
        return MarcacaoAjusteEdicao.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacoes-ajustes-historico-list")
    public List<MarcacaoAjusteHistoricoExibicao> getMarcacaoAjusteHistorio() {
        final List<MarcacaoAjusteHistoricoExibicao> marcacaoAjustes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            marcacaoAjustes.add(MarcacaoAjusteHistoricoExibicao.createDummy());
        }
        return marcacaoAjustes;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/inconsistencias-sem-vinculo-list")
    public List<InconsistenciaSemVinculo> getInconsistenciasSemVinculo() {
        final List<InconsistenciaSemVinculo> inconsistencia = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            inconsistencia.add(InconsistenciaSemVinculo.createDummy());
        }
        return inconsistencia;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/inconsistencias-fim-antes-inicio-list")
    public List<InconsistenciaFimAntesInicio> getInconsistenciasFimAntesInicio() {
        final List<InconsistenciaFimAntesInicio> inconsistencia = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            inconsistencia.add(InconsistenciaFimAntesInicio.createDummy());
        }
        return inconsistencia;
    }
}