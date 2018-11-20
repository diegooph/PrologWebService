package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem.OrdemServicoListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem.QtdItensPlacaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.resolucao.HolderResolucaoOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.resolucao.ResolverMultiplosItensOs;
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
@Path("/dummies")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DummyChecklistOrdemServicoResource {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-listagem-list")
    public List<OrdemServicoListagem> getOrdemServicoListagem() {
        final List<OrdemServicoListagem> ordens = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ordens.add(OrdemServicoListagem.createDummy());
        }
        return ordens;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-listagem-placa-list")
    public List<QtdItensPlacaListagem> getOrdemServicoPlacaListagem() {
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
        return HolderResolucaoOrdemServico.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-holder-resolucao-itens")
    public HolderResolucaoItensOrdemServico getHolderResolucaoItensOrdemServico() {
        return HolderResolucaoItensOrdemServico.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-resolver-multiplos-itens")
    public ResolverMultiplosItensOs getResolverMultiplosItensOs() {
        return ResolverMultiplosItensOs.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-resolver-item")
    public ResolverItemOrdemServico getResolverItemOrdemServico() {
        return ResolverItemOrdemServico.createDummy();
    }
}