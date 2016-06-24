package br.com.zalf.prolog.webservice.pneu.pneu;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Marca;
import br.com.zalf.prolog.models.Modelo;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.pneu.Pneu;
import br.com.zalf.prolog.models.pneu.Pneu.Dimensao;
import br.com.zalf.prolog.webservice.auth.Secured;

@Path("/pneus")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class PneuResource{
	private PneuService service = new PneuService();

	@POST
	@Secured
	@Path("/{codUnidade}")
	public Response insert(Pneu pneu,@PathParam("codUnidade") Long codUnidade){
		if (service.insert(pneu, codUnidade)) {
			return Response.Ok("Pneu inserido com sucesso.");
		}else{
			return Response.Error("Erro ao inserir o pneu");
		}
	}
	
	@PUT
	@Secured
	@Path("/{codUnidade}/{codPneuOriginal}")
	public Response update (Pneu pneu, @PathParam("codUnidade") Long codUnidade, @PathParam("codPneuOriginal") Long codOriginal){
		if (service.update(pneu, codUnidade, codOriginal)) {
			return Response.Ok("Pneu atualizado com sucesso.");
		}else{
			return Response.Error("Erro ao atualizar o pneu.");
		}
	}
	
	@POST
	@Secured
	@Path("/modelo/{codEmpresa}/{codMarca}")
	public Response insertModeloPneu(Modelo modelo, @PathParam("codEmpresa") long codEmpresa, @PathParam("codMarca") long codMarca){
		if (service.insertModeloPneu(modelo, codEmpresa, codMarca)) {
			return Response.Ok("Modelo de pneu inserido com sucesso.");
		}else{
			return Response.Error("Erro ao inserir o modelo de pneu.");
		}
	}
	
	@GET
	@Secured
	@Path("/{codUnidade}/{status}")
	public List<Pneu> getPneuByCodUnidadeByStatus(@PathParam("codUnidade") Long codUnidade,@PathParam("status") String status, @HeaderParam("Authorization") String tokenHeader){
		 return service.getPneuByCodUnidadeByStatus(codUnidade, status);
	}
	
	@GET
	@Secured
	@Path("/marcaModelos/{codEmpresa}")
	public List<Marca> getMarcaModeloPneuByCodEmpresa(@PathParam("codEmpresa") Long codEmpresa){
		return service.getMarcaModeloPneuByCodEmpresa(codEmpresa);
	}
	
	@GET
	@Secured
	@Path("/dimensao")
	public List<Dimensao> getDimensoes(){
		return service.getDimensoes();
	}

}



