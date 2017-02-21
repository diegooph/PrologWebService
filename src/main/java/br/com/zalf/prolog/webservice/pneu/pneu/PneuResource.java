package br.com.zalf.prolog.webservice.pneu.pneu;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.commons.veiculo.Marca;
import br.com.zalf.prolog.commons.veiculo.Modelo;
import br.com.zalf.prolog.commons.veiculo.Veiculo;
import br.com.zalf.prolog.frota.pneu.Pneu;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/pneus")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class PneuResource{

	private PneuService service = new PneuService();

	@POST
	@Secured(permissions = Pilares.Frota.Cadastro.PNEU)
	@Path("/{codUnidade}")
	public Response insert(Pneu pneu, @PathParam("codUnidade") Long codUnidade){
		if (service.insert(pneu, codUnidade)) {
			return Response.Ok("Pneu inserido com sucesso.");
		}else{
			return Response.Error("Erro ao inserir o pneu");
		}
	}
	
	@PUT
	@Secured(permissions = Pilares.Frota.Cadastro.PNEU)
	@Path("/{codUnidade}/{codPneuOriginal}")
	public Response update (Pneu pneu, @PathParam("codUnidade") Long codUnidade, @PathParam("codPneuOriginal") Long codOriginal){
		if (service.update(pneu, codUnidade, codOriginal)) {
			return Response.Ok("Pneu atualizado com sucesso.");
		}else{
			return Response.Error("Erro ao atualizar o pneu.");
		}
	}
	
	@POST
	@Secured(permissions = { Pilares.Frota.Cadastro.PNEU, Pilares.Frota.Alteracao.PNEU })
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
	public List<Pneu.Dimensao> getDimensoes(){
		return service.getDimensoes();
	}
	
	@POST
	@Secured(permissions = Pilares.Frota.Pneu.VINCULAR_VEICULO)
	@Path("/vincular")
	public Response vinculaPneuVeiculo(Veiculo veiculo){
		if (service.vinculaPneuVeiculo(veiculo)) {
			return Response.Ok("Pneus vinculados com sucesso.");
		}else{
			return Response.Error("Erro ao víncular os pneus ao veículo");
		}
	}

}



