package br.com.zalf.prolog.webservice.gente.controlejornada.OLD;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.*;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * @deprecated at 09/03/2018. Use {@link DeprecatedControleIntervaloResource_2} instead.
 */
@Path("/intervalos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@ConsoleDebugLog
@Deprecated
public class DeprecatedControleIntervaloResource_1 {

    private final DeprecatedControleIntervaloService_2 service = new DeprecatedControleIntervaloService_2();

    /**
     * O motivo deste método não necessitar nem da permissão de marcacão de intervalo, é que se um colaborador que antes
     * tinha permissão passar a não ter mais, ele não poderia sincronizar possíveis intervalos que tenha no celular.
     * Por esse motivo, não pedimos permissão alguma. Para permitir que mesmo colaboradores que estejam inativos
     * também sincronizem seus intervalos setamos o considerOnlyActiveUsers para {@code false}.
     */
    @POST
    @UsedBy(platforms = Platform.ANDROID)
//    @Secured(authTypes = AuthType.BASIC, considerOnlyActiveUsers = false)
    public ResponseIntervalo insertIntervalo(
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO) final long versaoDadosIntervalo,
            final Intervalo intervalo) {

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
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO) final long versaoDadosIntervalo,
            @PathParam("codUnidade") final Long codUnidade) {
        return service.getIntervaloOfflineSupport(versaoDadosIntervalo, codUnidade, new ColaboradorService());
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = {AuthType.BEARER, AuthType.BASIC}, permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/abertos/{cpf}/{codTipoIntervalo}")
    public Intervalo getIntervaloAberto(@PathParam("cpf") final Long cpf,
                                        @PathParam("codTipoIntervalo") final Long codTipoInvervalo) throws Throwable {
        final Long codUnidade = new ColaboradorService().getCodUnidadeByCpf(cpf);
        return toIntervalo(service.getUltimaMarcacaoInicioNaoFechada(codUnidade, cpf, codTipoInvervalo));
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = {AuthType.BEARER, AuthType.BASIC}, permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.AJUSTE_MARCACOES,
            Pilares.Gente.Intervalo.VISUALIZAR_TODAS_MARCACOES})
    @Path("/{cpf}/{codTipoIntervalo}")
    public List<Intervalo> getIntervalosColaborador(@PathParam("cpf") final Long cpf,
                                                    @PathParam("codTipoIntervalo") final String codTipo,
                                                    @QueryParam("limit") final long limit,
                                                    @QueryParam("offset") final long offset) {
        final Long codUnidade = new ColaboradorService().getCodUnidadeByCpf(cpf);
        return service.getMarcacoesIntervaloColaborador(codUnidade, cpf, codTipo, limit, offset);
    }

    /**
     * @deprecated at 08/09/17
     */
    @POST
    @Secured(permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/{codUnidade}/{cpf}/{codTipoIntervalo}")
    public AbstractResponse DEPRECATED_INICIA_INTERVALO(@PathParam("codUnidade") final Long codUnidade,
                                                        @PathParam("cpf") final Long cpf,
                                                        @PathParam("codTipoIntervalo") final Long codTipo) {
        final Long codIntervalo = service.iniciaIntervalo(codUnidade, cpf, codTipo);
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
    public Response DEPRECATED_INSERE_FINALIZACAO_INTERVALO(final Intervalo intervalo, @PathParam("codUnidade") final Long
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
            marcacao.setTipoMarcacaoIntervalo(TipoInicioFim.MARCACAO_INICIO);
        } else {
            marcacao.setDataHoraMaracao(intervalo.getDataHoraFim());
            marcacao.setFonteDataHora(intervalo.getFonteDataHoraFim());
            marcacao.setLocalizacaoMarcacao(intervalo.getLocalizacaoFim());
            marcacao.setJustificativaTempoRecomendado(intervalo.getJustificativaTempoRecomendado());
            marcacao.setJustificativaEstouro(intervalo.getJustificativaEstouro());
            marcacao.setTipoMarcacaoIntervalo(TipoInicioFim.MARCACAO_FIM);
        }
        return marcacao;
    }

    /**
     * Esse método só existe por questões de compatibilidade entre a antiga e nova estrutura dos intervalos.
     */
    @Nullable
    private Intervalo toIntervalo(@Nullable final IntervaloMarcacao marcacao) throws SQLException {
        if (marcacao == null) {
            return null;
        }

        final Intervalo intervalo = new Intervalo();
        intervalo.setCodigo(marcacao.getCodigo());

        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(marcacao.getCpfColaborador());
        intervalo.setColaborador(colaborador);

        final TipoMarcacao tipoIntervalo = new TipoMarcacao();
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