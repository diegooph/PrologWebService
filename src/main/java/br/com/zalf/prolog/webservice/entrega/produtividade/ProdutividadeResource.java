package br.com.zalf.prolog.webservice.entrega.produtividade;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/produtividades")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ProdutividadeResource{

	private ProdutividadeService service = new ProdutividadeService();

	@GET
	@Secured(permissions = {Pilares.Entrega.Produtividade.INDIVIDUAL, Pilares.Entrega.Relatorios.PRODUTIVIDADE})
	@Path("/colaboradores/{cpf}/{ano}/{mes}")
	public List<ItemProdutividade> getProdutividadeColaborador(@PathParam("cpf") Long cpf,
															   @PathParam("ano") int ano,
															   @PathParam("mes") int mes) {
		return service.getProdutividadeByPeriodo(ano, mes, cpf);
	}

	@GET
	@Secured(permissions = Pilares.Entrega.Produtividade.CONSOLIDADO)
	@Path("consolidados/{codUnidade}/{equipe}/{codFuncao}")
	@UsedBy(platforms = Platform.ANDROID)
	public List<HolderColaboradorProdutividade> getConsolidadoProdutividade(@PathParam("codUnidade") Long codUnidade,
																			@PathParam("equipe") String equipe,
																			@PathParam("codFuncao") String codFuncao,
																			@QueryParam("dataInicial") long dataInicial,
																			@QueryParam("dataFinal") long dataFinal) {
		return service.getConsolidadoProdutividade(codUnidade, equipe, codFuncao, dataInicial, dataFinal);
	}

	@GET
	@Secured
	@Path("/periodos/{ano}/{mes}")
	public PeriodoProdutividade getPeriodoProdutividade(@PathParam("ano") int ano,
														@PathParam("mes") int mes,
														@QueryParam("codUnidade") Long codUnidade,
														@QueryParam("cpf") Long cpf) {
		return service.getPeriodoProdutividade(ano, mes, codUnidade, cpf);
	}
}



