package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.DummyData;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

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
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DummyControleJornadaAjusteResource extends DummyData {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacoes-consolidadas-list")
    @Secured
    public List<MarcacaoConsolidada> getMarcacoesConsolidadas() {
        ensureDebugEnvironment();
        final List<MarcacaoConsolidada> marcacoesConsolidadas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            marcacoesConsolidadas.add(MarcacaoConsolidada.createDummy());
        }
        return marcacoesConsolidadas;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacoes-estratificadas-colaborador-list")
    @Secured
    public List<MarcacaoColaboradorAjuste> getMarcacoesColaboradorParaAjuste() {
        ensureDebugEnvironment();
        final List<MarcacaoColaboradorAjuste> marcacoesColaborador = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            marcacoesColaborador.add(MarcacaoColaboradorAjuste.createDummy());
        }
        return marcacoesColaborador;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacao-ajuste-adicao-inicio-fim")
    @Secured
    public MarcacaoAjusteAdicaoInicioFim getMarcacaoAjusteAdicaoInicioFim() {
        ensureDebugEnvironment();
        return MarcacaoAjusteAdicaoInicioFim.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacao-ajuste-adicao")
    @Secured
    public MarcacaoAjusteAdicao getMarcacaoAjusteAdicao() {
        ensureDebugEnvironment();
        return MarcacaoAjusteAdicao.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacao-ajuste-ativacao-inativacao")
    @Secured
    public MarcacaoAjusteAtivacaoInativacao getMarcacaoAjusteAtivacaoInativacao() {
        ensureDebugEnvironment();
        return MarcacaoAjusteAtivacaoInativacao.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacao-ajuste-edicao")
    @Secured
    public MarcacaoAjusteEdicao getMarcacaoAjusteEdicao() {
        ensureDebugEnvironment();
        return MarcacaoAjusteEdicao.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacoes-ajustes-historico-list")
    @Secured
    public List<MarcacaoAjusteHistorico> getMarcacaoAjusteHistorio() {
        ensureDebugEnvironment();
        final List<MarcacaoAjusteHistorico> marcacaoAjustes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            marcacaoAjustes.add(MarcacaoAjusteHistorico.createDummy());
        }
        return marcacaoAjustes;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/marcacoes-inconsistentes-list")
    @Secured
    public List<MarcacaoInconsistenciaExibicao> getMarcacoesInconsistentes() {
        ensureDebugEnvironment();
        final List<MarcacaoInconsistenciaExibicao> marcacaoInconsistentes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            marcacaoInconsistentes.add(MarcacaoInconsistenciaExibicao.createDummy());
        }
        return marcacaoInconsistentes;
    }
}
