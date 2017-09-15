package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.colaborador.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.colaborador.Unidade;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 19/08/2017.
 */
@Path("/intervalos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@DebugLog
public class ControleIntervaloResource {

    private final ControleIntervaloService service = new ControleIntervaloService();

    /**
     * O motivo deste método não necessitar nem da permissão de marcacão de intervalo, é que se um colaborador que antes
     * tinha permissão passar a não ter mais, ele não poderia sincronizar possíveis intervalos que tenha no celular.
     * Por esse motivo, não pedimos permissão alguma. Para permitir que mesmo colaboradores que estejam inativos
     * também sincronizem seus intervalos setamos o considerOnlyActiveUsers para {@code false}.
     */
    @POST
    @Secured(authType = AuthType.BASIC, considerOnlyActiveUsers = false)
    public ResponseIntervalo insertIntervalo(
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO) long versaoDadosIntervalo,
            Intervalo intervalo) {

        return service.insertOrUpdateIntervalo(versaoDadosIntervalo, intervalo);
    }

    @GET
    @Secured(permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/{codUnidade}/offline-support")
    public IntervaloOfflineSupport getIntervaloOfflineSupport(
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO) long versaoDadosIntervalo,
            @PathParam("codUnidade") Long codUnidade) {
        return service.getIntervaloOfflineSupport(versaoDadosIntervalo, codUnidade, new ColaboradorService());
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Intervalo.MARCAR_INTERVALO, Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
    Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO})
    @Path("/{codUnidade}/completos")
    public List<TipoIntervalo> getTiposIntervalosCompletos(@PathParam("codUnidade") Long codUnidade) {
        return service.getTiposIntervalos(codUnidade, true);
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Intervalo.MARCAR_INTERVALO, Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO})
    @Path("/{codUnidade}/resumidos")
    public List<TipoIntervalo> getTiposIntervalosResumidos(@PathParam("codUnidade") Long codUnidade) {
        return service.getTiposIntervalos(codUnidade, false);
    }

    @GET
    @Secured(permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/abertos/{cpf}/{codTipoIntervalo}")
    public Intervalo getIntervaloAberto(@PathParam("cpf") Long cpf, @PathParam("codTipoIntervalo") Long codTipoInvervalo) throws Exception {
        TipoIntervalo tipoIntervalo = new TipoIntervalo();
        tipoIntervalo.setCodigo(codTipoInvervalo);
        return service.getIntervaloAberto(cpf ,tipoIntervalo);
    }

    @GET
    @Path("/{cpf}/{codTipoIntervalo}")
    @Secured(permissions = {Pilares.Gente.Intervalo.MARCAR_INTERVALO, Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO, Pilares.Gente.Intervalo.EDITAR_MARCACAO,
            Pilares.Gente.Intervalo.VALIDAR_INVALIDAR_MARCACAO, Pilares.Gente.Intervalo.VISUALIZAR_TODAS_MARCACOES})
    public List<Intervalo> getIntervalosColaborador(@PathParam("cpf") Long cpf, @PathParam("codTipoIntervalo") String codTipo,
                                                    @QueryParam("limit") long limit, @QueryParam("offset") long offset) {
        return service.getIntervalosColaborador(cpf, codTipo, limit, offset);
    }

    @POST
    @Secured(permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/{codUnidade}/{cpf}/{codTipoIntervalo}")
    public AbstractResponse DEPRECATED_INICIA_INTERVALO(@PathParam("codUnidade") Long codUnidade, @PathParam("cpf") Long cpf,
                                            @PathParam("codTipoIntervalo") Long codTipo) {
        Intervalo intervalo = new Intervalo();
        Colaborador colaborador = new Colaborador();
        Unidade unidade = new Unidade();
        unidade.setCodigo(codUnidade);
        colaborador.setCpf(cpf);
        colaborador.setUnidade(unidade);
        intervalo.setColaborador(colaborador);
        intervalo.setFonteDataHoraInicio(FonteDataHora.SERVIDOR);
        intervalo.setDataHoraInicio(new Date(System.currentTimeMillis()));
        if (service.insertOrUpdateIntervalo(intervalo)) {
            return Response.ok("Intervalo finalizado com sucesso");
        }else {
            return Response.error("Erro ao finalizar o intervalo");
        }
    }

    @PUT
    @Secured(permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/{codUnidade}")
    @Deprecated
    public Response DEPRECATED_INSERE_FINALIZACAO_INTERVALO(Intervalo intervalo, @PathParam("codUnidade") Long codUnidade) {
        intervalo.setFonteDataHoraFim(FonteDataHora.SERVIDOR);
        intervalo.setDataHoraFim(new Date(System.currentTimeMillis()));
        intervalo.getColaborador().getUnidade().setCodigo(codUnidade);
        if (service.insertOrUpdateIntervalo(intervalo)) {
            return Response.ok("Intervalo finalizado com sucesso");
        } else {
            return Response.error("Erro ao finalizar o intervalo");
        }
    }
}