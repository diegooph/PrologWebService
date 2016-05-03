package br.com.zalf.prolog.webservice.produtividade;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.produtividade.ItemProdutividade;
import br.com.zalf.prolog.models.util.DateUtils;

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
			@FormParam("cpf") Long cpf, 
			@FormParam("token") String token) {
		return service.getProdutividadeByPeriodo(DateUtils.toLocalDate(new Date(dataInicial)),
				DateUtils.toLocalDate(new Date(dataFinal)), cpf, token);

	}

}



