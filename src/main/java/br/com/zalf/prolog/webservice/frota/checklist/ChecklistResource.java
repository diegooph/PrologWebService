package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.ChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.model.FiltroRegionalUnidadeChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.alteracao_logica.ChecklistsAlteracaoAcaoData;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistUploadMidiaRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.SuccessResponseChecklistUploadMidia;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloResource;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.mudancaestrutura.ChecklistMigracaoEstruturaSuporte;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static br.com.zalf.prolog.webservice.commons.util.PrologCustomHeaders.AppVersionAndroid.PROLOG_APP_VERSION;

@Path("/checklists")
@ConsoleDebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ChecklistResource {
    @NotNull
    private final ChecklistService service = new ChecklistService();

    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    public AbstractResponse insert(@HeaderParam("Authorization") @Required final String userToken,
                                   @HeaderParam(PROLOG_APP_VERSION) final Integer versaoApp,
                                   @Required final String checklistJson) throws ProLogException {
        final ChecklistInsercao checklistNew;
        // Convertemos o JSON dependendo da versão do App.
        if (ChecklistMigracaoEstruturaSuporte.isAppNovaEstruturaChecklist(versaoApp)) {
            checklistNew = GsonUtils.getGson().fromJson(checklistJson, ChecklistInsercao.class);
        } else {
            final Checklist checklistOld = GsonUtils.getGson().fromJson(checklistJson, Checklist.class);
            final LocalDateTime agora = Now.getLocalDateTimeUtc();
            checklistNew = ChecklistMigracaoEstruturaSuporte.toChecklistInsercao(checklistOld, agora, versaoApp);
            checklistNew.setChecklistAntigo(checklistOld);
        }
        final Long codChecklist = service.insert(userToken, checklistNew);
        //noinspection ConstantConditions
        if (codChecklist != null) {
            return ResponseWithCod.ok("Checklist inserido com sucesso", codChecklist);
        } else {
            return Response.error("Erro ao inserir checklist");
        }
    }

    @PUT
    @Path("/alteracao-logica")
    @Secured(permissions = {Pilares.Frota.Checklist.DELETAR})
    public Response deleteLogicoChecklistsAndOs(
            @NotNull @Valid final ChecklistsAlteracaoAcaoData checklistsParaAlteracao) {
        final Long codigoColaborador = this.colaboradorAutenticadoProvider.get().getCodigo();
        return service.deleteLogicoChecklistsAndOs(checklistsParaAlteracao, codigoColaborador);
    }

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/realizacao/midia-upload")
    public SuccessResponseChecklistUploadMidia uploadMidiaRealizacaoChecklist(
            @FormDataParam("upload") @Required final InputStream fileInputStream,
            @FormDataParam("upload") @Required final FormDataContentDisposition fileDetail,
            @FormDataParam("midiaJson") @Required final FormDataBodyPart jsonPart) {
        jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        final ChecklistUploadMidiaRealizacao midia = jsonPart.getValueAs(ChecklistUploadMidiaRealizacao.class);
        return service.uploadMidiaRealizacaoChecklist(fileInputStream, fileDetail, midia);
    }

    @GET
    @Path("/filtros-tipos-veiculos")
    @Secured(permissions = {
            Pilares.Frota.Checklist.VISUALIZAR_TODOS,
            Pilares.Frota.Checklist.REALIZAR,
            Pilares.Frota.Relatorios.CHECKLIST})
    @UsedBy(platforms = {Platform.WEBSITE, Platform.ANDROID})
    public List<TipoVeiculo> getTiposVeiculosFiltroChecklist(
            @HeaderParam("Authorization") @Required final String userToken,
            @QueryParam("codEmpresa") @Required final Long codEmpresa) throws ProLogException {
        return service.getTiposVeiculosFiltroChecklist(userToken, codEmpresa);
    }

    @GET
    @Path("{codigo}")
    @Secured(permissions = {Pilares.Frota.Checklist.VISUALIZAR_TODOS, Pilares.Frota.Checklist.REALIZAR})
    public Checklist getByCod(@PathParam("codigo") final Long codigo,
                              @HeaderParam("Authorization") final String userToken) {
        return service.getByCod(codigo, userToken);
    }

    @GET
    @Path("/listagem/colaborador")
    @Secured(permissions = {Pilares.Frota.Checklist.VISUALIZAR_TODOS, Pilares.Frota.Checklist.REALIZAR})
    public List<ChecklistListagem> getListagemByColaborador(@HeaderParam("Authorization") final String userToken,
                                                            @QueryParam("codColaborador") final Long codColaborador,
                                                            @QueryParam("dataInicial") final String dataInicial,
                                                            @QueryParam("dataFinal") final String dataFinal,
                                                            @QueryParam("limit") final int limit,
                                                            @QueryParam("offset") final long offset) {
        return service.getListagemByColaborador(userToken, codColaborador, dataInicial, dataFinal, limit, offset);
    }

    @GET
    @Path("/listagem")
    @Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
    public List<ChecklistListagem> getListagem(
            @HeaderParam("Authorization") final String userToken,
            @QueryParam("codUnidade") final Long codUnidade,
            @QueryParam("codEquipe") final Long codEquipe,
            @QueryParam("codTipoVeiculo") final Long codTipoVeiculo,
            @QueryParam("codVeiculo") final Long codVeiculo,
            @QueryParam("dataInicial") final String dataInicial,
            @QueryParam("dataFinal") final String dataFinal,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final long offset) {
        return service.getListagem(
                userToken,
                codUnidade,
                codEquipe,
                codTipoVeiculo,
                codVeiculo,
                dataInicial,
                dataFinal,
                limit,
                offset);
    }

    @GET
    @Path("/farois/{codUnidade}")
    @Secured(permissions = Pilares.Frota.FarolStatusPlacas.VISUALIZAR)
    public DeprecatedFarolChecklist getFarolChecklist(@PathParam("codUnidade") final Long codUnidade,
                                                      @QueryParam("dataInicial") final String dataInicial,
                                                      @QueryParam("dataFinal") final String dataFinal,
                                                      @QueryParam("itensCriticosRetroativos") final boolean itensCriticosRetroativos,
                                                      @HeaderParam("Authorization") final String userToken) throws ProLogException {
        return service.getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos, userToken);
    }

    @GET
    @Path("/farois/{codUnidade}/hoje")
    @Secured(permissions = Pilares.Frota.FarolStatusPlacas.VISUALIZAR)
    public DeprecatedFarolChecklist getFarolChecklist(@PathParam("codUnidade") final Long codUnidade,
                                                      @QueryParam("itensCriticosRetroativos") final boolean itensCriticosRetroativos,
                                                      @HeaderParam("Authorization") final String userToken) throws ProLogException {
        return service.getFarolChecklist(codUnidade, itensCriticosRetroativos, userToken);
    }

    @GET
    @Path("/novo/filtros-regionais-unidades")
    public FiltroRegionalUnidadeChecklist getRegionaisUnidadesSelecao(
            @QueryParam("codColaborador") @Required final Long codColaborador) {
        return service.getRegionaisUnidadesSelecao(codColaborador);
    }

    /**
     * @deprecated at 2020-06-08.
     * Use {@link ChecklistResource#getListagemByColaborador(String, Long, String, String, int, long)} instead.
     */
    @GET
    @Path("/colaboradores/{cpf}/resumidos")
    @Secured(permissions = {Pilares.Frota.Checklist.VISUALIZAR_TODOS, Pilares.Frota.Checklist.REALIZAR})
    @Deprecated
    public List<Checklist> getByColaboradorResumidos(
            @PathParam("cpf") final Long cpf,
            @QueryParam("dataInicial") final Long dataInicial,
            @QueryParam("dataFinal") final Long dataFinal,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final long offset,
            @HeaderParam("Authorization") final String userToken) {
        return service.getByColaborador(cpf, dataInicial, dataFinal, limit, offset, true, userToken);
    }

    /**
     * @deprecated at 2020-06-08.
     * Use {@link ChecklistResource#getListagem(String, Long, Long, Long, String, String, String, int, long)} instead.
     */
    @GET
    @Path("{codUnidade}/resumidos")
    @Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
    @Deprecated
    public List<Checklist> getAllResumido(
            @PathParam("codUnidade") final Long codUnidade,
            @QueryParam("codEquipe") final Long codEquipe,
            @QueryParam("codTipoVeiculo") final Long codTipoVeiculo,
            @QueryParam("placaVeiculo") final String placaVeiculo,
            @QueryParam("dataInicial") final long dataInicial,
            @QueryParam("dataFinal") final long dataFinal,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final long offset,
            @HeaderParam("Authorization") final String userToken) {
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

    /**
     * @deprecated at 2020-06-08. Nesta data não existe resource que retorne os checklists completos.
     */
    @GET
    @Path("{codUnidade}/completos")
    @Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
    @Deprecated
    public List<Checklist> getAllCompletos(
            @PathParam("codUnidade") final Long codUnidade,
            @QueryParam("codEquipe") final Long codEquipe,
            @QueryParam("codTipoVeiculo") final Long codTipoVeiculo,
            @QueryParam("placaVeiculo") final String placaVeiculo,
            @QueryParam("dataInicial") final long dataInicial,
            @QueryParam("dataFinal") final long dataFinal,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final long offset,
            @HeaderParam("Authorization") final String userToken) {
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

    /**
     * @deprecated at 2019-10-09. Use
     * {@link ChecklistModeloResource#getModeloChecklistRealizacao(Long, Long, String, String, String)} instead.
     */
    @GET
    @Path("/novo/{codUnidadeModelo}/{codModelo}/{placa}/saida")
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    @Deprecated
    public NovoChecklistHolder getNovoChecklistSaida(
            @PathParam("codUnidadeModelo") final Long codUnidadeModelo,
            @PathParam("codModelo") final Long codModelo,
            @PathParam("placa") final String placa,
            @HeaderParam("Authorization") final String userToken) {
        // Esse método já está redirecionando para o novo Service.
        return ChecklistMigracaoEstruturaSuporte.toEstruturaAntigaRealizacaoModelo(
                new ChecklistModeloService().getModeloChecklistRealizacao(
                        codModelo,
                        ChecklistMigracaoEstruturaSuporte.getCodVeiculoByPlaca(placa),
                        placa,
                        TipoChecklist.SAIDA.asString(),
                        userToken));
    }

    /**
     * @deprecated at 2019-10-09. Use
     * {@link ChecklistModeloResource#getModeloChecklistRealizacao(Long, Long, String, String, String)} instead.
     */
    @GET
    @Path("/novo/{codUnidadeModelo}/{codModelo}/{placa}/retorno")
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    @Deprecated
    public NovoChecklistHolder getNovoChecklistRetorno(
            @PathParam("codUnidadeModelo") final Long codUnidadeModelo,
            @PathParam("codModelo") final Long codModelo,
            @PathParam("placa") final String placa,
            @HeaderParam("Authorization") final String userToken) {
        // Esse método já está redirecionando para o novo Service.
        return ChecklistMigracaoEstruturaSuporte.toEstruturaAntigaRealizacaoModelo(
                new ChecklistModeloService().getModeloChecklistRealizacao(
                        codModelo,
                        ChecklistMigracaoEstruturaSuporte.getCodVeiculoByPlaca(placa),
                        placa,
                        TipoChecklist.RETORNO.asString(),
                        userToken));
    }

    /**
     * @deprecated at 2019-08-18. Use {@link ChecklistModeloResource#getModelosSelecaoRealizacao(Long, Long, String)}
     * instead.
     */
    @GET
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    @Path("/modeloPlacas/{codUnidade}/{codFuncaoColaborador}")
    @Deprecated
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(
            @PathParam("codUnidade") final Long codUnidade,
            @PathParam("codFuncaoColaborador") final Long codCargo,
            @HeaderParam("Authorization") final String userToken) {
        // Esse método já está redirecionando para o novo Service.
        return ChecklistMigracaoEstruturaSuporte.toEstruturaAntigaSelecaoModelo(
                new ChecklistModeloService().getModelosSelecaoRealizacao(codUnidade, codCargo, userToken));
    }

    /**
     * @deprecated at 09/03/2018. Use {@link ChecklistModeloResource} instead.
     */
    @GET
    @Path("/urlImagens/{codUnidade}/{codFuncao}")
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    @Deprecated
    public List<String> getUrlImagensPerguntas(@PathParam("codUnidade") final Long codUnidade,
                                               @PathParam("codFuncao") final Long codFuncao) throws ProLogException {
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
    public List<Checklist> DEPRECATED_GET_ALL(@PathParam("codUnidade") final Long codUnidade,
                                              @PathParam("equipe") final String equipe,
                                              @PathParam("placa") final String placa,
                                              @QueryParam("dataInicial") final long dataInicial,
                                              @QueryParam("dataFinal") final long dataFinal,
                                              @QueryParam("limit") final int limit,
                                              @QueryParam("offset") final long offset,
                                              @HeaderParam("Authorization") final String userToken) {
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