package br.com.zalf.prolog.webservice.gente.ranking;

import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.gente.ranking.ItemPosicao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

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