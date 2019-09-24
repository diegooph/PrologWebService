package br.com.zalf.prolog.webservice.gente.controlejornada;

import br.com.zalf.prolog.webservice.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.commons.util.*;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 08/11/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@DebugLog
@Path("/controle-jornada")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ControleJornadaResource {
    @NotNull
    private final ControleJornadaService service = new ControleJornadaService();

    /**
     * O motivo deste método não necessitar nem da permissão de marcacão de intervalo, é que se um colaborador que antes
     * tinha permissão passar a não ter mais, ele não poderia sincronizar possíveis intervalos que tenha no celular.
     * Por esse motivo, não pedimos permissão alguma. Para permitir que mesmo colaboradores que estejam inativos
     * também sincronizem seus intervalos setamos o considerOnlyActiveUsers para {@code false}.
     */
    @POST
    @UsedBy(platforms = Platform.ANDROID)
    public ResponseIntervalo insertIntervalo(
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_TOKEN_MARCACAO) String tokenMarcacao,
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO) long versaoDadosIntervalo,
            @HeaderParam(ProLogCustomHeaders.AppVersionAndroid.PROLOG_APP_VERSION) Integer versaoApp,
            IntervaloMarcacao intervaloMarcacao) throws ProLogException {
        return service.insertMarcacaoIntervalo(tokenMarcacao, versaoDadosIntervalo, intervaloMarcacao, versaoApp);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/marcacao-em-andamento")
    public IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_TOKEN_MARCACAO) String tokenMarcacao,
            @QueryParam("codUnidade") @Required Long codUnidade,
            @QueryParam("cpf") @Required Long cpf,
            @QueryParam("codTipoIntervalo") @Required Long codTipoInvervalo) throws ProLogException {
        return service.getUltimaMarcacaoInicioNaoFechada(tokenMarcacao, codUnidade, cpf, codTipoInvervalo);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/marcacoes")
    public List<Intervalo> getMarcacoesColaborador(
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_TOKEN_MARCACAO) String tokenMarcacao,
            @QueryParam("codUnidade") @Required Long codUnidade,
            @QueryParam("cpf") @Required Long cpf,
            @QueryParam("codTipoIntervalo") @Required String codTipo,
            @QueryParam("limit") @Required long limit,
            @QueryParam("offset") @Required long offset) throws ProLogException {
        return service.getMarcacoesColaborador(tokenMarcacao, codUnidade, cpf, codTipo, limit, offset);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {
            Pilares.Gente.Intervalo.AJUSTE_MARCACOES,
            Pilares.Gente.Intervalo.VISUALIZAR_TODAS_MARCACOES})
    @Path("/marcacoes-por-data")
    public List<MarcacaoListagem> getMarcacoesColaboradorPorData(
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("cpf") @Optional final Long cpf,
            @QueryParam("codTipoIntervalo") @Optional final Long codTipo,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws ProLogException {
        return service.getMarcacoesColaboradorPorData(codUnidade, cpf, codTipo, dataInicial, dataFinal);
    }

    /**
     * Essa busca só é feita no app caso exista algum usuário logado, então podemos deixar usando o @Secured como
     * BEARER e não precisa autenticar pelo Token da Marcação.
     */
    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = AuthType.BEARER, permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/offline-support")
    public IntervaloOfflineSupport getIntervaloOfflineSupport(
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO) long versaoDadosIntervalo,
            @QueryParam("codUnidade") Long codUnidade) throws ProLogException {
        return service.getIntervaloOfflineSupport(versaoDadosIntervalo, codUnidade, new ColaboradorService());
    }


    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/dados-marcacao")
    public DadosMarcacaoUnidade getDadosMarcacaoUnidade(@QueryParam("codUnidade") @Required Long codUnidade)
            throws ProLogException {
        return service.getDadosMarcacaoUnidade(codUnidade);
    }
}