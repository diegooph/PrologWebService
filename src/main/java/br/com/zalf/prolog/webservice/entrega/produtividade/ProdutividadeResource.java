package br.com.zalf.prolog.webservice.entrega.produtividade;

import java.util.Date;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.produtividade.HolderColaboradorProdutividade;
import br.com.zalf.prolog.models.produtividade.ItemProdutividade;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.Android;
import br.com.zalf.prolog.webservice.util.L;

@Path("/produtividade")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ProdutividadeResource{
	private ProdutividadeService service = new ProdutividadeService();

	@POST
	@Path("/byPeriodo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<ItemProdutividade> getProdutividadeByPeriodo(@FormParam("dataInicial") long dataInicial,
															 @FormParam("dataFinal") long dataFinal,
															 @FormParam("cpf") Long cpf,
															 @FormParam("token") String token) {
		return service.getProdutividadeByPeriodo(DateUtils.toLocalDate(new Date(dataInicial)),
				DateUtils.toLocalDate(new Date(dataFinal)), cpf, token);
	}

	@GET
	@Android
	@Secured
	@Path("consolidado/{codUnidade}/{codEquipe}/{codFuncao}")
	public List<HolderColaboradorProdutividade> getConsolidadoProdutividade(@PathParam("codUnidade") Long codUnidade,
																			@PathParam("codEquipe") String equipe,
																			@PathParam("codFuncao") String codFuncao,
																			@QueryParam("dataInicial") long dataInicial,
																			@QueryParam("dataFinal") long dataFinal){
		return service.getConsolidadoProdutividade(codUnidade, equipe, codFuncao, dataInicial, dataFinal);
	}
}



