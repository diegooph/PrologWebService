package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zart on 19/08/2017.
 */
@Path("/intervalos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ControleIntervaloResource {

    private ControleIntervaloService service = new ControleIntervaloService();

    @GET
    @Secured(permissions = {Pilares.Gente.Intervalo.MARCAR_INTERVALO, Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
    Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO})
    @Path("/{cpf}/completos")
    public List<TipoIntervalo> getTiposIntervalosCompletos(@PathParam("cpf") Long cpf) {
        return service.getTiposIntervalos(cpf, true);
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Intervalo.MARCAR_INTERVALO, Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO})
    @Path("/{cpf}/resumidos")
    public List<TipoIntervalo> getTiposIntervalosResumidos(@PathParam("cpf") Long cpf) {
        return service.getTiposIntervalos(cpf, false);
    }

    @GET
    @Secured(permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/abertos/{cpf}/{codTipoIntervalo}")
    public Intervalo getIntervaloAberto(@PathParam("cpf") Long cpf, @PathParam("codTipoIntervalo") Long codTipoInvervalo) throws Exception {
        TipoIntervalo tipoIntervalo = new TipoIntervalo();
        tipoIntervalo.setCodigo(codTipoInvervalo);
        return service.getIntervaloAberto(cpf ,tipoIntervalo);
    }

    @POST
    @Secured(permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/{codUnidade}/{cpf}/{codTipoIntervalo}")
    public AbstractResponse iniciaIntervalo(@PathParam("codUnidade") Long codUnidade, @PathParam("cpf") Long cpf,
                                            @PathParam("codTipoIntervalo") Long codTipo) {
        Long codIntervalo = service.iniciaIntervalo(codUnidade, cpf, codTipo);
        if(codIntervalo != null){
            return ResponseWithCod.Ok("Intervalo iniciado com sucesso", codIntervalo);
        }else{
            return Response.Error("Erro ao iniciar o intervalo");
        }
    }

    @PUT
    @Secured(permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/{codUnidade}")
    public Response insereFinalizacaoIntervalo(Intervalo intervalo, @PathParam("codUnidade") Long codUnidade) {
        if(service.insereFinalizacaoIntervalo(intervalo, codUnidade)){
            return Response.Ok("Intervalo finalizado com sucesso");
        }else {
            return Response.Error("Erro ao finalizar o intervalo");
        }
    }

    @GET
    @Path("/{cpf}")
    @Secured(permissions = {Pilares.Gente.Intervalo.MARCAR_INTERVALO, Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO, Pilares.Gente.Intervalo.EDITAR_MARCACAO,
            Pilares.Gente.Intervalo.VALIDAR_INVALIDAR_MARCACAO, Pilares.Gente.Intervalo.VISUALIZAR_TODAS_MARCACOES})
    public List<Intervalo> getIntervalosColaborador(@PathParam("cpf") Long cpf) {
        return service.getIntervalosColaborador(cpf);
    }

}
