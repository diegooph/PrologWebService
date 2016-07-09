package br.com.zalf.prolog.webservice.gente.ranking;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.ranking.ItemPosicao;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.auth.Secured;

@Path("/ranking")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RankingResource {
	private RankingService service = new RankingService();

	@GET
	@Path("/getRanking/{codUnidade}/{equipe}")
	@Secured
	public List<ItemPosicao> getRanking(
			@QueryParam("dataInicial") long dataInicial, 
			@QueryParam("dataFinal") long dataFinal, 
			@PathParam("equipe") String equipe,
			@PathParam("codUnidade") Long codUnidade) throws SQLException {
		return service.getRanking(DateUtils.toLocalDate(new Date(dataInicial)),
				DateUtils.toLocalDate(new Date(dataFinal)), equipe, codUnidade);
	}
}