package br.com.zalf.prolog.webservice.gente.controlejornada;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.*;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacaoDao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.NotAuthorizedException;
import java.util.List;
import java.util.Optional;

/**
 * Created on 08/11/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ControleJornadaService {
    @NotNull
    private static final String TAG = ControleJornadaService.class.getSimpleName();
    @NotNull
    private final ControleJornadaDao dao = Injection.provideControleJornadaDao();

    @SuppressWarnings("Duplicates")
    @NotNull
    public ResponseIntervalo insertMarcacaoIntervalo(
            @NotNull final String tokenMarcacao,
            final long versaoDadosIntervalo,
            @NotNull final IntervaloMarcacao intervaloMarcacao,
            @Nullable final Integer versaoAppMomentoSincronizacao) throws ProLogException {

        ensureValidToken(tokenMarcacao);

        // Devemos salvar no objeto o parâmetro de versão capturado no Header da requisição.
        intervaloMarcacao.setVersaoAppMomentoSincronizacao(versaoAppMomentoSincronizacao);
        EstadoVersaoIntervalo estadoVersaoIntervalo = null;
        try {
            @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"}) final Long versaoDadosBanco = dao.getDadosMarcacaoUnidade(intervaloMarcacao.getCodUnidade())
                    .get()
                    .getVersaoDadosBanco();
            estadoVersaoIntervalo = versaoDadosIntervalo < versaoDadosBanco
                    ? EstadoVersaoIntervalo.VERSAO_DESATUALIZADA
                    : EstadoVersaoIntervalo.VERSAO_ATUALIZADA;
            final Long codIntervalo = dao.insertMarcacaoIntervalo(intervaloMarcacao);
            return ResponseIntervalo.ok(codIntervalo, "Intervalo inserido com sucesso", estadoVersaoIntervalo);
        } catch (final Throwable t) {
            Log.e(TAG, String.format(
                    "Erro ao inserir ou atualizar um intervalo.\n" +
                            "versaoDadosIntervalo: %d", versaoDadosIntervalo),
                    t);
            return ResponseIntervalo.error("Erro ao inserir intervalo", estadoVersaoIntervalo);
        }
    }

    @NotNull
    public List<Intervalo> getMarcacoesColaborador(@NotNull final String tokenMarcacao,
                                                   final Long codUnidade,
                                                   final Long cpf,
                                                   final String codTipo,
                                                   final long limit,
                                                   final long offset) throws ProLogException {
        ensureValidToken(tokenMarcacao);

        try {
            return dao.getMarcacoesIntervaloColaborador(codUnidade, cpf, codTipo, limit, offset);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar marcações de um colaborador.\n" +
                    "cpf: %s \n" +
                    "codTipo: %s \n" +
                    "limit: %d \n" +
                    "offset: %d", cpf, codTipo, limit, offset), t);
            throw Injection.provideProLogExceptionHandler().map(t, "Erro ao buscar marcações");
        }
    }

    @NotNull
    public List<MarcacaoListagem> getMarcacoesColaboradorPorData(@NotNull final Long codUnidade,
                                                                 @Nullable final Long cpf,
                                                                 @Nullable final Long codTipo,
                                                                 @NotNull final String dataInicial,
                                                                 @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getMarcacoesColaboradorPorData(
                    codUnidade,
                    cpf,
                    codTipo,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable t) {
            final String errorMessage = String.format("Erro ao buscar marcações de um colaborador.\n" +
                    "cpf: %d\n" +
                    "codTipo: %d\n" +
                    "dataInicial: %s\n" +
                    "dataFinal: %s", cpf, codTipo, dataInicial, dataFinal);
            Log.e(TAG, errorMessage, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar marcação(ões), tente novamente");
        }
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    public IntervaloOfflineSupport getIntervaloOfflineSupport(final Long versaoDadosApp,
                                                              final Long codUnidade,
                                                              final ColaboradorService colaboradorService) throws
            ProLogException {
        try {
            final List<Colaborador> colaboradores = colaboradorService.getColaboradoresComAcessoFuncaoByUnidade(
                    codUnidade,
                    Pilares.Gente.Intervalo.MARCAR_INTERVALO);
            final TipoMarcacaoDao tipoMarcacaoDao = Injection.provideTipoMarcacaoDao();
            final List<TipoMarcacao> tiposIntervalo = tipoMarcacaoDao.getTiposMarcacoes(codUnidade, true, true);
            final Optional<DadosMarcacaoUnidade> dadosMarcacaoUnidade = dao.getDadosMarcacaoUnidade(codUnidade);
            final EstadoVersaoIntervalo estadoVersaoIntervalo;

            // Isso é algo importante para se destacar: se ao buscarmos a versão dos dados de intervalo para uma unidade
            // e não existir nada, assumimos que a unidade também não possui nenhum colaborador com acesso a essa
            // funcionalidade, o que faz sentido. Além disso, poupamos uma nova requisição ao banco, agilizando o login.
            // Porém, para isso funcionar bem, o ProLog deve garantir que se existe alguém de uma unidade com
            // permissão de
            // marcação de intervalo, DEVE existir para essa unidade um valor de versão dos dados.
            if (!dadosMarcacaoUnidade.isPresent()) {
                estadoVersaoIntervalo = EstadoVersaoIntervalo.UNIDADE_SEM_USO_INTERVALO;
            } else {
                // Se a unidade tem uma versão dos dados de intervalo no banco, nós precisamos comparar com a versão que
                // o App enviou.
                final Long versaoDadosWs = dadosMarcacaoUnidade.get().getVersaoDadosBanco();
                if (versaoDadosApp != null && versaoDadosApp.equals(versaoDadosWs)) {
                    // Se a versão está atualizada não precisamos setar mais nada no IntervaloOfflineSupport.
                    estadoVersaoIntervalo = EstadoVersaoIntervalo.VERSAO_ATUALIZADA;
                } else {
                    if (versaoDadosApp != null && versaoDadosApp > versaoDadosWs) {
                        // Isso nunca deveria acontecer! Porém, para não impedirmos o login do usuário, vamos retornar
                        // como se sua versão estivesse desatualizada e mandar os dados que temos.
                        Log.e(TAG, "Erro versão dados intervalo",
                                new IllegalStateException("Versão dos dados do app (" + versaoDadosApp + ") não pode " +
                                        "ser " +
                                        "maior do que a versão dos dados no banco(" + versaoDadosWs + ")" +
                                        "!"));
                    }
                    estadoVersaoIntervalo = EstadoVersaoIntervalo.VERSAO_DESATUALIZADA;
                }
            }
            // Criamos o objeto.
            final IntervaloOfflineSupport intervaloOfflineSupport = new IntervaloOfflineSupport(estadoVersaoIntervalo);
            intervaloOfflineSupport.setColaboradores(colaboradores);
            intervaloOfflineSupport.setTiposIntervalo(tiposIntervalo);
            intervaloOfflineSupport.setEstadoVersaoIntervalo(estadoVersaoIntervalo);
            dadosMarcacaoUnidade.ifPresent(d -> {
                intervaloOfflineSupport.setVersaoDadosIntervalo(d.getVersaoDadosBanco());
                intervaloOfflineSupport.setTokenSincronizacaoMarcacao(d.getTokenSincronizacaoMarcacao());
            });
            return intervaloOfflineSupport;
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar o IntervaloOfflineSupport.\n" +
                    "codUnidade: %d \n" +
                    "versaoDadosApp: %d \n", codUnidade, versaoDadosApp), t);
            throw Injection.provideProLogExceptionHandler().map(
                    t,
                    "Erro ao buscar informações, tente novamente");
        }
    }

    @NotNull
    public DadosMarcacaoUnidade getDadosMarcacaoUnidade(@NotNull final Long codUnidade)
            throws ProLogException {
        try {
            // Se não tiver presente, deixamos estourar o erro.
            //noinspection ConstantConditions
            return dao.getDadosMarcacaoUnidade(codUnidade).get();
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar dados de marcação para a unidade: %d", codUnidade), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro! Unidade não configurada para controle de jornada");
        }
    }

    public boolean isMarcacaoInicioFinalizada(@NotNull final String tokenMarcacao,
                                              @NotNull final Long codMarcacao) throws ProLogException {
        ensureValidToken(tokenMarcacao);
        try {
            return dao.isMarcacaoInicioFinalizada(codMarcacao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar se marcação está finalizada: %d", codMarcacao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar se marcação está finalizada.");
        }
    }

    private void ensureValidToken(@NotNull final String tokenMarcacao) throws ProLogException {
        try {
            if (!dao.verifyIfTokenMarcacaoExists(tokenMarcacao)) {
                throw new NotAuthorizedException("Token não existe no banco de dados: " + tokenMarcacao);
            }
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao verificar se o tokenMarcacao existe: %s", tokenMarcacao), t);
            if (t instanceof NotAuthorizedException) {
                throw (NotAuthorizedException) t;
            }
            throw Injection.provideProLogExceptionHandler().map(t, "Erro ao verificar token");
        }
    }

    @Nullable
    IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(@NotNull final String tokenMarcacao,
                                                        @NotNull final Long codUnidade,
                                                        @NotNull final Long cpfColaborador,
                                                        @NotNull final Long codTipoIntervalo) throws ProLogException {
        ensureValidToken(tokenMarcacao);

        try {
            return dao.getUltimaMarcacaoInicioNaoFechada(codUnidade, cpfColaborador, codTipoIntervalo);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao marcação em andamento de um colaborador\n" +
                    "cpfColaborador: %d \n" +
                    "codTipoIntervalo: %d", cpfColaborador, codTipoIntervalo), t);
            throw Injection.provideProLogExceptionHandler().map(
                    t,
                    "Erro ao marcação em andamento de um colaborador");
        }
    }
}