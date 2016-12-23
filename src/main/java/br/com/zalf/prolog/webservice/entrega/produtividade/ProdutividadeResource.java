package br.com.zalf.prolog.webservice.entrega.produtividade;

import br.com.zalf.prolog.entrega.produtividade.HolderColaboradorProdutividade;
import br.com.zalf.prolog.entrega.produtividade.ItemProdutividade;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.Android;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/produtividades")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ProdutividadeResource{

	private ProdutividadeService service = new ProdutividadeService();

	@GET
	@Secured
	@Path("/colaboradores/{ano}/{mes}/{cpf}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<ItemProdutividade> getProdutividadeByPeriodo(@PathParam("ano") int ano,
															 @PathParam("mes") int mes,
															 @PathParam("cpf") Long cpf) {
		return service.getProdutividadeByPeriodo(ano, mes, cpf);
	}

	@GET
	@Android
	@Secured
	@Path("consolidados/{codUnidade}/{equipe}/{codFuncao}")
	public List<HolderColaboradorProdutividade> getConsolidadoProdutividade(@PathParam("codUnidade") Long codUnidade,
																			@PathParam("equipe") String equipe,
																			@PathParam("codFuncao") String codFuncao,
																			@QueryParam("dataInicial") long dataInicial,
																			@QueryParam("dataFinal") long dataFinal){
		return service.getConsolidadoProdutividade(codUnidade, equipe, codFuncao, dataInicial, dataFinal);
	}
}



