package br.com.zalf.prolog.webservice.gente.controlejornada;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controlejornada.OLD.DeprecatedControleIntervaloDaoImpl_2;
import br.com.zalf.prolog.webservice.gente.controlejornada.OLD.DeprecatedControleIntervaloDao_2;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.*;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Created on 08/11/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
class ControleJornadaService {
    @NotNull
    private static final String TAG = ControleJornadaService.class.getSimpleName();
    @NotNull
    private DeprecatedControleIntervaloDao_2 daoAntiga = new DeprecatedControleIntervaloDaoImpl_2();
    @NotNull
    private final ControleJornadaDao dao = Injection.provideControleJornadaDao();

    @SuppressWarnings("Duplicates")
    @NotNull
    ResponseIntervalo insertMarcacaoIntervalo(final long versaoDadosIntervalo,
                                              @NotNull final IntervaloMarcacao intervaloMarcacao,
                                              @Nullable final Integer versaoAppMomentoSincronizacao) {
        // Devemos salvar no objeto o parâmetro de versão capturado no Header da requisição.
        intervaloMarcacao.setVersaoAppMomentoSincronizacao(versaoAppMomentoSincronizacao);
        EstadoVersaoIntervalo estadoVersaoIntervalo = null;
        try {
            @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
            final Long versaoDadosBanco =
                    daoAntiga.getVersaoDadosIntervaloByUnidade(intervaloMarcacao.getCodUnidade()).get();
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

    @Nullable
    IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(@NotNull final Long codUnidade,
                                                        @NotNull final Long cpfColaborador,
                                                        @NotNull final Long codTipoIntervalo) throws ProLogException {
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

    @NotNull
    public List<Intervalo> getMarcacoesIntervaloColaborador(Long codUnidade,
                                                            Long cpf,
                                                            String codTipo,
                                                            long limit,
                                                            long offset) throws ProLogException {
        try {
            return daoAntiga.getMarcacoesIntervaloColaborador(codUnidade, cpf, codTipo, limit, offset);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar marcações de um colaborador.\n" +
                    "cpf: %s \n" +
                    "codTipo: %s \n" +
                    "limit: %d \n" +
                    "offset: %d", cpf, codTipo, limit, offset), t);
            throw Injection.provideProLogExceptionHandler().map(t, "Erro ao buscar marcações");
        }
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    public IntervaloOfflineSupport getIntervaloOfflineSupport(Long versaoDadosApp,
                                                              Long codUnidade,
                                                              ColaboradorService colaboradorService) throws ProLogException {
        IntervaloOfflineSupport intervaloOfflineSupport = null;
        try {
            final List<Colaborador> colaboradores = colaboradorService.getColaboradoresComAcessoFuncaoByUnidade(
                    codUnidade,
                    Pilares.Gente.Intervalo.MARCAR_INTERVALO);
            final List<TipoMarcacao> tiposIntervalo = daoAntiga.getTiposIntervalosByUnidade(codUnidade,  true, true);
            final Optional<Long> versaoDadosBanco = daoAntiga.getVersaoDadosIntervaloByUnidade(codUnidade);
            EstadoVersaoIntervalo estadoVersaoIntervalo;

            // Isso é algo importante para se destacar: se ao buscarmos a versão dos dados de intervalo para uma unidade
            // e não existir nada, assumimos que a unidade também não possui nenhum colaborador com acesso a essa
            // funcionalidade, o que faz sentido. Além disso, poupamos uma nova requisição ao banco, agilizando o login.
            // Porém, para isso funcionar bem, o ProLog deve garantir que se existe alguém de uma unidade com permissão de
            // marcação de intervalo, DEVE existir para essa unidade um valor de versão dos dados.
            if (!versaoDadosBanco.isPresent()) {
                estadoVersaoIntervalo = EstadoVersaoIntervalo.UNIDADE_SEM_USO_INTERVALO;
            } else {
                // Se a unidade tem uma versão dos dados de intervalo no banco, nós precisamos comparar com a versão que
                // o App enviou.
                if (versaoDadosApp != null && versaoDadosApp.equals(versaoDadosBanco.get())) {
                    // Se a versão está atualizada não precisamos setar mais nada no IntervaloOfflineSupport.
                    estadoVersaoIntervalo = EstadoVersaoIntervalo.VERSAO_ATUALIZADA;
                } else {
                    if (versaoDadosApp != null && versaoDadosApp > versaoDadosBanco.get()) {
                        // Isso nunca deveria acontecer! Porém, para não impedirmos o login do usuário, vamos retornar
                        // como se sua versão estivesse desatualizada e mandar os dados que temos.
                        Log.e(TAG, "Erro versão dados intervalo",
                                new IllegalStateException("Versão dos dados do app (" + versaoDadosApp + ") não pode ser " +
                                        "maior do que a versão dos dados no banco(" + versaoDadosBanco.get() + ")!"));
                    }
                    estadoVersaoIntervalo = EstadoVersaoIntervalo.VERSAO_DESATUALIZADA;
                }
            }
            // Criamos o objeto.
            intervaloOfflineSupport = new IntervaloOfflineSupport(estadoVersaoIntervalo);
            intervaloOfflineSupport.setColaboradores(colaboradores);
            intervaloOfflineSupport.setTiposIntervalo(tiposIntervalo);
            intervaloOfflineSupport.setEstadoVersaoIntervalo(estadoVersaoIntervalo);
            versaoDadosBanco.ifPresent(intervaloOfflineSupport::setVersaoDadosIntervalo);
        } catch (final SQLException t) {
            Log.e(TAG, String.format("Erro ao buscar o IntervaloOfflineSupport.\n" +
                    "codUnidade: %d \n" +
                    "versaoDadosApp: %d \n", codUnidade, versaoDadosApp), t);
            throw Injection.provideProLogExceptionHandler().map(
                    t,
                    "Erro ao buscar informações, tente novamente");
        }

        return intervaloOfflineSupport;
    }
}