package br.com.zalf.prolog.webservice.frota.checklist.ordemservico;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.config.BuildConfig;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoAbertaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoFechadaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.QtdItensPlacaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 19/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Secured
@Path("/v2/dummies")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DummyChecklistOrdemServicoResource {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-abertas-listagem-list")
    public List<OrdemServicoListagem> getOrdemServicoAbertaListagem() {
        ensureDebugEnviroment();
        final List<OrdemServicoListagem> ordens = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ordens.add(OrdemServicoAbertaListagem.createDummy());
        }
        return ordens;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-fechadas-listagem-list")
    public List<OrdemServicoListagem> getOrdemServicoFechadaListagem() {
        ensureDebugEnviroment();
        final List<OrdemServicoListagem> ordens = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ordens.add(OrdemServicoFechadaListagem.createDummy());
        }
        return ordens;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-listagem-placa-list")
    public List<QtdItensPlacaListagem> getOrdemServicoPlacaListagem() {
        ensureDebugEnviroment();
        final List<QtdItensPlacaListagem> ordens = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ordens.add(QtdItensPlacaListagem.createDummy());
        }
        return ordens;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-holder-resolucao")
    public HolderResolucaoOrdemServico getHolderResolucaoOrdemServico() {
        ensureDebugEnviroment();
        return HolderResolucaoOrdemServico.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-holder-resolucao-itens")
    public HolderResolucaoItensOrdemServico getHolderResolucaoItensOrdemServico() {
        ensureDebugEnviroment();
        return HolderResolucaoItensOrdemServico.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-resolver-multiplos-itens")
    public ResolverMultiplosItensOs getResolverMultiplosItensOs() {
        ensureDebugEnviroment();
        return ResolverMultiplosItensOs.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-resolver-item")
    public ResolverItemOrdemServico getResolverItemOrdemServico() {
        ensureDebugEnviroment();
        return ResolverItemOrdemServico.createDummy();
    }

    private void ensureDebugEnviroment() {
        if (!BuildConfig.DEBUG) {
            throw new IllegalStateException("Esse resource só pode ser utilizado em ambientes de testes");
        }
    }
}