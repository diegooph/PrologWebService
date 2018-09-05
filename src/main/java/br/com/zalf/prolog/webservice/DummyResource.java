package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.Visao;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 15/08/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/dummies")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DummyResource {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/cargo")
    @Secured
    public Cargo getCargo() {
        ensureDebugEnvironment();
        return Cargo.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/cargo-list")
    @Secured
    public List<Cargo> getCargoList() {
        ensureDebugEnvironment();
        final List<Cargo> cargos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cargos.add(Cargo.createDummy());
        }
        return cargos;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/visao")
    @Secured
    public Visao getVisao() {
        ensureDebugEnvironment();
        return Visao.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/tipo-intervalo")
    @Secured
    public TipoIntervalo getTipoIntervalo() {
        ensureDebugEnvironment();
        return TipoIntervalo.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/tipo-intervalo-list")
    @Secured
    public List<TipoIntervalo> getTipoIntervaloList() {
        ensureDebugEnvironment();
        final List<TipoIntervalo> tipos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tipos.add(TipoIntervalo.createDummy());
        }
        return tipos;
    }

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

    private void ensureDebugEnvironment() {
        if (!BuildConfig.DEBUG) {
            throw new IllegalStateException("Esse resource só pode ser utilizado em ambientes de testes");
        }
    }
}