package br.com.empresa.oprojeto.webservice.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.empresa.oprojeto.models.produtividade.ItemProdutividade;
import br.com.empresa.oprojeto.models.util.DateUtils;
import br.com.empresa.oprojeto.webservice.services.ProdutividadeService;

@Path("/produtividade")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ProdutividadeResource{
	private ProdutividadeService service = new ProdutividadeService();

	@POST
	@Path("/byPeriodo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<ItemProdutividade> getProdutividadeByPeriodo(
			@FormParam("dataInicial") long dataInicial, 
			@FormParam("dataFinal") long dataFinal,
			@FormParam("cpf") long cpf){
		System.out.println("PERIODO");
		return service.getProdutividadeByPeriodo(DateUtils.toLocalDate(new Date(dataInicial)),
				DateUtils.toLocalDate(new Date(dataFinal)), cpf);

	}

}



