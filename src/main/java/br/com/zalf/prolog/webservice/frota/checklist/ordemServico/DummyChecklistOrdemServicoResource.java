package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OLD.ConsertoMultiplosItensOs;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.resolucao.FechamentoItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.resolucao.FechamentoOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem.OrdemServicoListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem.OrdemServicoPlacaListagem;
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
@Path("/dummies")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DummyChecklistOrdemServicoResource {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-listagem-list")
    @Secured
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
    @Secured
    public List<OrdemServicoPlacaListagem> getOrdemServicoPlacaListagem() {
        final List<OrdemServicoPlacaListagem> ordens = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ordens.add(OrdemServicoPlacaListagem.createDummy());
        }
        return ordens;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-fechamento")
    @Secured
    public FechamentoOrdemServico getFechamentoOrdemServico() {
        return FechamentoOrdemServico.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-fechamento-itens")
    @Secured
    public FechamentoItemOrdemServico getFechamentoItemOrdemServico() {
        return FechamentoItemOrdemServico.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/checklist-ordem-servico-conserto-multiplos-itens")
    @Secured
    public ConsertoMultiplosItensOs getConsertoMultiplosItens() {
        return ConsertoMultiplosItensOs.createDummy();
    }
}