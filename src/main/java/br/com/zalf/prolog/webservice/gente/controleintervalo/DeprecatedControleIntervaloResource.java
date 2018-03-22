package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by Zart on 19/08/2017.
 *
 * @deprecated at 09/03/2018. Use {@link ControleIntervaloResource} instead.
 */
@Path("/intervalos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@DebugLog
@Deprecated
public class DeprecatedControleIntervaloResource {

    private final ControleIntervaloService service = new ControleIntervaloService();

    /**
     * O motivo deste método não necessitar nem da permissão de marcacão de intervalo, é que se um colaborador que antes
     * tinha permissão passar a não ter mais, ele não poderia sincronizar possíveis intervalos que tenha no celular.
     * Por esse motivo, não pedimos permissão alguma. Para permitir que mesmo colaboradores que estejam inativos
     * também sincronizem seus intervalos setamos o considerOnlyActiveUsers para {@code false}.
     */
    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = AuthType.BASIC, considerOnlyActiveUsers = false)
    public ResponseIntervalo insertIntervalo(
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO) long versaoDadosIntervalo,
            Intervalo intervalo) {

        return service.insertMarcacaoIntervalo(versaoDadosIntervalo, toIntervaloMarcacao(intervalo));
    }

    /**
     * Essa busca só é feita no app caso exista algum usuário logado, então podemos deixar o authType apenas como
     * BEARER.
     */
    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = AuthType.BEARER, permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/{codUnidade}/offline-support")
    public IntervaloOfflineSupport getIntervaloOfflineSupport(
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO) long versaoDadosIntervalo,
            @PathParam("codUnidade") Long codUnidade) {
        return service.getIntervaloOfflineSupport(versaoDadosIntervalo, codUnidade, new ColaboradorService());
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = {AuthType.BEARER, AuthType.BASIC}, permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/abertos/{cpf}/{codTipoIntervalo}")
    public Intervalo getIntervaloAberto(@PathParam("cpf") Long cpf,
                                        @PathParam("codTipoIntervalo") Long codTipoInvervalo) throws Exception {
        final Long codUnidade = new ColaboradorService().getCodUnidadeByCpf(cpf);
        return toIntervalo(service.getUltimaMarcacaoInicioNaoFechada(codUnidade, cpf, codTipoInvervalo));
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = {AuthType.BEARER, AuthType.BASIC}, permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.EDITAR_MARCACAO,
            Pilares.Gente.Intervalo.VALIDAR_INVALIDAR_MARCACAO,
            Pilares.Gente.Intervalo.VISUALIZAR_TODAS_MARCACOES})
    @Path("/{cpf}/{codTipoIntervalo}")
    public List<Intervalo> getIntervalosColaborador(@PathParam("cpf") Long cpf,
                                                    @PathParam("codTipoIntervalo") String codTipo,
                                                    @QueryParam("limit") long limit,
                                                    @QueryParam("offset") long offset) {
        final Long codUnidade = new ColaboradorService().getCodUnidadeByCpf(cpf);
        return service.getMarcacoesIntervaloColaborador(codUnidade, cpf, codTipo, limit, offset);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO})
    @Path("/tipos/{codUnidade}/completos")
    public List<TipoIntervalo> getTiposIntervalosCompletos(@PathParam("codUnidade") Long codUnidade) {
        return service.getTiposIntervalos(codUnidade, true);
    }

    @POST
    @Path("/tipos")
    public AbstractResponse insertTipoIntervalo(TipoIntervalo tipoIntervalo) {
        return service.insertTipoIntervalo(tipoIntervalo);
    }

    @PUT
    @Path("/tipos")
    public Response updateTipoInvervalo(TipoIntervalo tipoIntervalo) {
        if (service.updateTipoIntervalo(tipoIntervalo)) {
            return Response.ok("Tipo de intervalo editado com sucesso");
        } else {
            return Response.error("Erro ao editar o tipo de intervalo");
        }
    }

    @PUT
    @Path("/tipos/inativar/{codUnidade}/{codTipoIntervalo}")
    public Response inativarTipoIntervalo(@PathParam("codUnidade") Long codUnidade,
                                          @PathParam("codTipoIntervalo") Long codTipoIntervalo) {
        if (service.inativarTipoIntervalo(codUnidade, codTipoIntervalo)) {
            return Response.ok("Tipo de intervalo inativado com sucesso");
        } else {
            return Response.error("Erro ao inativar o tipo de intervalo");
        }
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO})
    @Path("/tipos/{codUnidade}/resumidos")
    public List<TipoIntervalo> getTiposIntervalosResumidos(@PathParam("codUnidade") Long codUnidade) {
        return service.getTiposIntervalos(codUnidade, false);
    }

    /**
     * @deprecated at 08/09/17
     */
    @POST
    @Secured(permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/{codUnidade}/{cpf}/{codTipoIntervalo}")
    public AbstractResponse DEPRECATED_INICIA_INTERVALO(@PathParam("codUnidade") Long codUnidade,
                                                        @PathParam("cpf") Long cpf,
                                                        @PathParam("codTipoIntervalo") Long codTipo) {
        Long codIntervalo = service.iniciaIntervalo(codUnidade, cpf, codTipo);
        if (codIntervalo != null) {
            return ResponseWithCod.ok("Intervalo iniciado com sucesso", codIntervalo);
        } else {
            return Response.error("Erro ao iniciar o intervalo");
        }
    }

    /**
     * @deprecated at 08/09/17
     */
    @PUT
    @Secured(permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/{codUnidade}")
    @Deprecated
    public Response DEPRECATED_INSERE_FINALIZACAO_INTERVALO(Intervalo intervalo, @PathParam("codUnidade") Long
            codUnidade) {
        if (service.insereFinalizacaoIntervalo(intervalo, codUnidade)) {
            return Response.ok("Intervalo finalizado com sucesso");
        } else {
            return Response.error("Erro ao finalizar o intervalo");
        }
    }

    /**
     * Esse método só existe por questões de compatibilidade entre a antiga e nova estrutura dos intervalos.
     */
    @NotNull
    private IntervaloMarcacao toIntervaloMarcacao(@NotNull final Intervalo intervalo) {
        final IntervaloMarcacao marcacao = new IntervaloMarcacao();
        marcacao.setCodigo(intervalo.getCodigo());
        marcacao.setCodUnidade(intervalo.getColaborador().getUnidade().getCodigo());
        marcacao.setCodTipoIntervalo(intervalo.getTipo().getCodigo());
        marcacao.setCpfColaborador(intervalo.getColaborador().getCpf());

        // Apenas teremos a marcação de início ou de fim, nunca ambas.
        if (intervalo.getDataHoraInicio() != null) {
            marcacao.setDataHoraMaracao(intervalo.getDataHoraInicio());
            marcacao.setFonteDataHora(intervalo.getFonteDataHoraInicio());
            marcacao.setLocalizacaoMarcacao(intervalo.getLocalizacaoInicio());
            marcacao.setTipoMarcacaoIntervalo(TipoMarcacaoIntervalo.MARCACAO_INICIO);
        } else {
            marcacao.setDataHoraMaracao(intervalo.getDataHoraFim());
            marcacao.setFonteDataHora(intervalo.getFonteDataHoraFim());
            marcacao.setLocalizacaoMarcacao(intervalo.getLocalizacaoFim());
            marcacao.setJustificativaTempoRecomendado(intervalo.getJustificativaTempoRecomendado());
            marcacao.setJustificativaEstouro(intervalo.getJustificativaEstouro());
            marcacao.setTipoMarcacaoIntervalo(TipoMarcacaoIntervalo.MARCACAO_FIM);
        }
        return marcacao;
    }

    /**
     * Esse método só existe por questões de compatibilidade entre a antiga e nova estrutura dos intervalos.
     */
    @NotNull
    private Intervalo toIntervalo(@NotNull final IntervaloMarcacao marcacao) throws SQLException {
        final Intervalo intervalo = new Intervalo();
        intervalo.setCodigo(marcacao.getCodigo());

        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(marcacao.getCpfColaborador());
        intervalo.setColaborador(colaborador);

        final TipoIntervalo tipoIntervalo = new TipoIntervalo();
        tipoIntervalo.setCodigo(marcacao.getCodTipoIntervalo());
        intervalo.setTipo(tipoIntervalo);

        intervalo.setDataHoraInicio(marcacao.getDataHoraMaracao());
        intervalo.setFonteDataHoraInicio(marcacao.getFonteDataHora());
        intervalo.setLocalizacaoInicio(marcacao.getLocalizacaoMarcacao());

        final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(marcacao.getCodUnidade());
        final LocalDateTime dataAtual = LocalDateTime.now(zoneId);
        intervalo.setTempoDecorrido(Duration.ofMillis(Math.abs(ChronoUnit.MILLIS.between(intervalo.getDataHoraInicio(), dataAtual))));

        return intervalo;
    }
}