package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloResource;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path("/checklists")
@DebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChecklistResource {

    private ChecklistService service = new ChecklistService();

    @POST
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    public AbstractResponse insert(@HeaderParam("Authorization") @Required final String userToken,
                                   @Required final Checklist checklist) throws ProLogException {
        final Long codChecklist = service.insert(userToken, checklist);
        if (codChecklist != null) {
            return ResponseWithCod.ok("Checklist inserido com sucesso", codChecklist);
        } else {
            return Response.error("Erro ao inserir checklist");
        }
    }

    @GET
    @Path("{codigo}")
    @Secured(permissions = {Pilares.Frota.Checklist.VISUALIZAR_TODOS, Pilares.Frota.Checklist.REALIZAR})
    public Checklist getByCod(@PathParam("codigo") Long codigo, @HeaderParam("Authorization") String userToken) {
        return service.getByCod(codigo, userToken);
    }

    @GET
    @Path("/colaboradores/{cpf}/resumidos")
    @Secured(permissions = {Pilares.Frota.Checklist.VISUALIZAR_TODOS, Pilares.Frota.Checklist.REALIZAR})
    public List<Checklist> getByColaboradorResumidos(
            @PathParam("cpf") Long cpf,
            @QueryParam("dataInicial") Long dataInicial,
            @QueryParam("dataFinal") Long dataFinal,
            @QueryParam("limit") int limit,
            @QueryParam("offset") long offset,
            @HeaderParam("Authorization") String userToken) {
        return service.getByColaborador(cpf, dataInicial, dataFinal, limit, offset, true, userToken);
    }

    @GET
    @Path("{codUnidade}/resumidos")
    @Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
    public List<Checklist> getAllResumido(
            @PathParam("codUnidade") Long codUnidade,
            @QueryParam("codEquipe") Long codEquipe,
            @QueryParam("codTipoVeiculo") Long codTipoVeiculo,
            @QueryParam("placaVeiculo") String placaVeiculo,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal,
            @QueryParam("limit") int limit,
            @QueryParam("offset") long offset,
            @HeaderParam("Authorization") String userToken) {
        return service.getAll(
                codUnidade,
                codEquipe,
                codTipoVeiculo,
                placaVeiculo,
                dataInicial,
                dataFinal,
                limit,
                offset,
                true,
                userToken);
    }

    @GET
    @Path("{codUnidade}/completos")
    @Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
    public List<Checklist> getAllCompletos(
            @PathParam("codUnidade") Long codUnidade,
            @QueryParam("codEquipe") Long codEquipe,
            @QueryParam("codTipoVeiculo") Long codTipoVeiculo,
            @QueryParam("placaVeiculo") String placaVeiculo,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal,
            @QueryParam("limit") int limit,
            @QueryParam("offset") long offset,
            @HeaderParam("Authorization") String userToken) {
        return service.getAll(
                codUnidade,
                codEquipe,
                codTipoVeiculo,
                placaVeiculo,
                dataInicial,
                dataFinal,
                limit,
                offset,
                false,
                userToken);
    }

    @GET
    @Path("/farois/{codUnidade}")
    @Secured(permissions = Pilares.Frota.FarolStatusPlacas.VISUALIZAR)
    public DeprecatedFarolChecklist getFarolChecklist(@PathParam("codUnidade") Long codUnidade,
                                                      @QueryParam("dataInicial") String dataInicial,
                                                      @QueryParam("dataFinal") String dataFinal,
                                                      @QueryParam("itensCriticosRetroativos") boolean itensCriticosRetroativos,
                                                      @HeaderParam("Authorization") String userToken) throws ProLogException {
        return service.getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos, userToken);
    }

    @GET
    @Path("/farois/{codUnidade}/hoje")
    @Secured(permissions = Pilares.Frota.FarolStatusPlacas.VISUALIZAR)
    public DeprecatedFarolChecklist getFarolChecklist(@PathParam("codUnidade") Long codUnidade,
                                                      @QueryParam("itensCriticosRetroativos") boolean itensCriticosRetroativos,
                                                      @HeaderParam("Authorization") String userToken) throws ProLogException {
        return service.getFarolChecklist(codUnidade, itensCriticosRetroativos, userToken);
    }

    @GET
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    @Path("/modeloPlacas/{codUnidade}/{codFuncaoColaborador}")
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("codFuncaoColaborador") Long codFuncao,
            @HeaderParam("Authorization") String userToken) {
        return service.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao, userToken);
    }

    @GET
    @Path("/novo/{codUnidade}/{codModelo}/{placa}/saida")
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    public NovoChecklistHolder getNovoChecklistSaida(
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("codModelo") Long codModelo,
            @PathParam("placa") String placa,
            @HeaderParam("Authorization") String userToken) {
        return service.getNovoChecklistHolder(codUnidade, codModelo, placa, Checklist.TIPO_SAIDA, userToken);
    }

    @GET
    @Path("/novo/{codUnidade}/{codModelo}/{placa}/retorno")
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    public NovoChecklistHolder getNovoChecklistRetorno(
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("codModelo") Long codModelo,
            @PathParam("placa") String placa,
            @HeaderParam("Authorization") String userToken) {
        return service.getNovoChecklistHolder(codUnidade, codModelo, placa, Checklist.TIPO_RETORNO, userToken);
    }

    /**
     * @deprecated at 09/03/2018. Use {@link ChecklistModeloResource} instead.
     */
    @GET
    @Path("/urlImagens/{codUnidade}/{codFuncao}")
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    @Deprecated
    public List<String> getUrlImagensPerguntas(@PathParam("codUnidade") Long codUnidade,
                                               @PathParam("codFuncao") Long codFuncao) throws ProLogException {
        return new ChecklistModeloService().getUrlImagensPerguntas(codUnidade, codFuncao);
    }

    /**
     * @deprecated em 17/10/2017.
     * <p>
     * No Android não é mais utilizado esse método, utiliza-se o com path base diferente (checklist). Porém, ele ainda
     * é utilizado na Web para buscar os checklists. Após a troca para utilizar
     * {@link #getAllResumido(Long, Long, Long, String, long, long, int, long, String)}, este método pode ser removido.
     */
    @GET
    @Path("{codUnidade}/{equipe}/{placa}")
    @Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
    @Deprecated
    public List<Checklist> DEPRECATED_GET_ALL(@PathParam("codUnidade") Long codUnidade,
                                              @PathParam("equipe") String equipe,
                                              @PathParam("placa") String placa,
                                              @QueryParam("dataInicial") long dataInicial,
                                              @QueryParam("dataFinal") long dataFinal,
                                              @QueryParam("limit") int limit,
                                              @QueryParam("offset") long offset,
                                              @HeaderParam("Authorization") String userToken) {
        return service.getAll(
                codUnidade,
                null,
                null,
                placa.equals("%") ? null : placa,
                dataInicial,
                dataFinal,
                limit,
                offset,
                false,
                userToken);
    }
}