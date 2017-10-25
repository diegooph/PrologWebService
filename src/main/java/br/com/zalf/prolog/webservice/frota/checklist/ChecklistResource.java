package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
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
    public AbstractResponse insert(Checklist checklist, @HeaderParam("Authorization") String userToken) {
        checklist.setData(new Date(System.currentTimeMillis()));
        final Long codChecklist = service.insert(checklist, userToken);
        if (codChecklist != null) {
            return ResponseWithCod.ok("Checklist inserido com sucesso", codChecklist);
        } else {
            return Response.error("Erro ao inserir checklist");
        }
    }

    @GET
    @Path("/urlImagens/{codUnidade}/{codFuncao}")
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    public List<String> getUrlImagensPerguntas(@PathParam("codUnidade") Long codUnidade,
                                               @PathParam("codFuncao") Long codFuncao){
        return service.getUrlImagensPerguntas(codUnidade, codFuncao);
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
    @Path("/farois/{codUnidade}")
    @Secured(permissions = Pilares.Frota.FarolStatusPlacas.VISUALIZAR)
    public Object getFarolChecklist(@PathParam("codUnidade") Long codUnidade,
                                    @QueryParam("dataInicial") long dataInicial,
                                    @QueryParam("dataFinal") long dataFinal,
                                    @QueryParam("itensCriticosRetroativos") boolean itensCriticosRetroativos,
                                    @HeaderParam("Authorization") String userToken) {
        return service.getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos, userToken);
    }

    @GET
    @Path("/farois/{codUnidade}/hoje")
    @Secured(permissions = Pilares.Frota.FarolStatusPlacas.VISUALIZAR)
    public Object getFarolChecklist(@PathParam("codUnidade") Long codUnidade,
                                    @QueryParam("itensCriticosRetroativos") boolean itensCriticosRetroativos,
                                    @HeaderParam("Authorization") String userToken) {
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
            @HeaderParam("Authorization") String userToken){
        return service.getNovoChecklistHolder(codUnidade, codModelo, placa, Checklist.TIPO_SAIDA, userToken);
    }

    @GET
    @Path("/novo/{codUnidade}/{codModelo}/{placa}/retorno")
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    public NovoChecklistHolder getNovoChecklistRetorno(
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("codModelo") Long codModelo,
            @PathParam("placa") String placa,
            @HeaderParam("Authorization") String userToken){
        return service.getNovoChecklistHolder(codUnidade, codModelo, placa, Checklist.TIPO_RETORNO, userToken);
    }

    /**
     * @deprecated em 17/10/2017.
     *
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

    /**
     * @deprecated em 17/10/2017.
     *
     * No Android não é mais utilizado esse método, utiliza-se o com path base diferente (checklist). Porém, ele ainda
     * é utilizado na Web para buscar os checklists. Após a troca para utilizar
     * {@link #getByColaboradorResumidos(Long, Long, Long, int, long, String)}, este método pode ser removido.
     */
    @GET
    @Path("/colaborador/{cpf}")
    @Secured(permissions = {Pilares.Frota.Checklist.VISUALIZAR_TODOS, Pilares.Frota.Checklist.REALIZAR})
    @Deprecated
    public List<Checklist> DEPRECATED_GET_BY_COLABORADOR(@PathParam("cpf") Long cpf,
                                                         @QueryParam("limit") int limit,
                                                         @QueryParam("offset") long offset,
                                                         @HeaderParam("Authorization") String userToken) {
        return service.getByColaborador(cpf, null, null, limit, offset, false, userToken);
    }

    /**
     * @deprecated em 17/10/2017.
     *
     * Este método esteve em produção no Android nas versões v1.1.26 e v1.1.26-h1. Após essas versões não estarem mais
     * ativas e sem usuários, pode ser removido.
     */
    @GET
    @Path("{codUnidade}/{equipe}/{placa}/resumidos")
    @Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
    public List<Checklist> DEPRECATED_GET_ALL_RESUMIDO(@PathParam("codUnidade") Long codUnidade,
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
                true,
                userToken);

    }
}