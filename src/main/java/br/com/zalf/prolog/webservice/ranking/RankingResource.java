package br.com.zalf.prolog.webservice.ranking;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.ranking.ItemPosicao;
import br.com.zalf.prolog.models.util.DateUtils;

@Path("/ranking")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RankingResource {
	private RankingService service = new RankingService();

	@POST
	@Path("/getRanking")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<ItemPosicao> getRanking(
			@FormParam("dataInicial") long dataInicial, 
			@FormParam("dataFinal") long dataFinal, 
			@FormParam("equipe") String equipe,
			@FormParam("codUnidade") Long codUnidade,
			@FormParam("cpf") Long cpf,
			@FormParam("token") String token) throws SQLException {
		return service.getRanking(DateUtils.toLocalDate(new Date(dataInicial)),
				DateUtils.toLocalDate(new Date(dataFinal)), equipe, codUnidade, cpf, token);
	}
}