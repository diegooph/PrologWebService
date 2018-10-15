package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.*;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Zart on 19/08/2017.
 */
public class ControleIntervaloService {
    @NotNull
    private static final String TAG = ControleIntervaloService.class.getSimpleName();
    @NotNull
    private ControleIntervaloDao dao = Injection.provideControleIntervaloDao();

    @NotNull
    public ResponseIntervalo insertMarcacaoIntervalo(final long versaoDadosIntervalo,
                                                     @NotNull final IntervaloMarcacao intervaloMarcacao) {
        EstadoVersaoIntervalo estadoVersaoIntervalo = null;
        try {
            @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
            final Long versaoDadosBanco = dao.getVersaoDadosIntervaloByUnidade(intervaloMarcacao.getCodUnidade()).get();
            estadoVersaoIntervalo = versaoDadosIntervalo < versaoDadosBanco
                    ? EstadoVersaoIntervalo.VERSAO_DESATUALIZADA
                    : EstadoVersaoIntervalo.VERSAO_ATUALIZADA;
            final Long codIntervalo = dao.insertMarcacaoIntervalo(intervaloMarcacao);
            return ResponseIntervalo.ok(codIntervalo, "Intervalo inserido com sucesso", estadoVersaoIntervalo);
        } catch (Throwable e) {
            Log.e(TAG, String.format(
                    "Erro ao inserir ou atualizar um intervalo. \n" +
                    "versaoDadosIntervalo: %d", versaoDadosIntervalo),
                    e);
            return ResponseIntervalo.error("Erro ao inserir intervalo", estadoVersaoIntervalo);
        }
    }

    @Nullable
    IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(@NotNull final Long codUnidade,
                                                        @NotNull final Long cpfColaborador,
                                                        @NotNull final Long codTipoIntervalo) throws Exception {
        try {
            return dao.getUltimaMarcacaoInicioNaoFechada(codUnidade, cpfColaborador, codTipoIntervalo);
        } catch (Exception e) {
            Log.e(TAG, String.format("Erro ao buscar os intervalos em abertos de um colaborador. \n" +
                    "cpfColaborador: %d \n" +
                    "codTipoIntervalo: %d", cpfColaborador, codTipoIntervalo), e);
            throw e;
        }
    }

    @NotNull
    List<Intervalo> getMarcacoesIntervaloColaborador(@NotNull final Long codUnidade,
                                                     @NotNull final Long cpfColaborador,
                                                     @NotNull final String codTipoIntevalo,
                                                     final long limit,
                                                     final long offset) {
        try {
            return dao.getMarcacoesIntervaloColaborador(codUnidade, cpfColaborador, codTipoIntevalo, limit, offset);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os intervalos de um colaborador. \n" +
                    "cpfColaborador: %s \n" +
                    "codTipoIntevalo: %s \n" +
                    "limit: %d \n" +
                    "offset: %d", cpfColaborador, codTipoIntevalo, limit, offset), e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public List<TipoMarcacao> getTiposIntervalos(@NotNull final Long codUnidade,
                                                 final boolean apenasAtivos,
                                                 final boolean withCargos) {
        try {
            return dao.getTiposIntervalosByUnidade(codUnidade, apenasAtivos, withCargos);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os tipos de intervalos. \n" +
                    "codUnidade: %d \n" +
                    "withCargos: %b", codUnidade, withCargos), e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    TipoMarcacao getTipoIntervalo(@NotNull final Long codUnidade,
                                  @NotNull final Long codTipoIntervalo) {
        try {
            return dao.getTipoIntervalo(codUnidade, codTipoIntervalo);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar tipo de intervalo. \n" +
                    "codUnidade: %d \n" +
                    "codTipoIntervalo: %d", codUnidade, codTipoIntervalo), e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    AbstractResponse insertTipoIntervalo(@NotNull final TipoMarcacao tipoIntervalo) {
        try {
            return ResponseWithCod.ok(
                    "Tipo de intervalo inserido com sucesso",
                    dao.insertTipoIntervalo(tipoIntervalo, Injection.provideDadosIntervaloChangedListener()));
        } catch (Throwable e){
            Log.e(TAG, "Erro ao inserir o tipo de intervalo", e);
            return Response.error("Erro ao inserir o tipo de intervalo");
        }
    }

    boolean updateTipoIntervalo(@NotNull final TipoMarcacao tipoIntervalo) {
        try {
            dao.updateTipoIntervalo(tipoIntervalo, Injection.provideDadosIntervaloChangedListener());
            return true;
        } catch (Throwable e) {
            Log.e(TAG, "Erro ao atualizar o tipo de intervalo", e);
            return false;
        }
    }

    void updateStatusAtivo(@NotNull final Long codUnidade,
                           @NotNull final Long codTipoIntervalo,
                           @NotNull final TipoMarcacao tipoIntervalo) throws ProLogException {
        try {
            dao.updateStatusAtivoTipoIntervalo(
                    codUnidade,
                    codTipoIntervalo,
                    tipoIntervalo,
                    Injection.provideDadosIntervaloChangedListener());
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao inativar o tipo de intervalo. \n" +
                    "codUnidade: %d \n" +
                    "codTipoIntervalo: %d", codUnidade, codTipoIntervalo), e);
            final String errorMessage = "Erro ao "
                    + (tipoIntervalo.isAtivo() ? "ativar" : "inativar")
                    + " tipo de marcação, tente novamente";
            throw Injection.provideProLogExceptionHandler().map(e, errorMessage);
        }
    }

    @NotNull
    public IntervaloOfflineSupport getIntervaloOfflineSupport(@NotNull final ColaboradorService colaboradorService,
                                                              @NotNull final Long codUnidade,
                                                              @Nullable final Long versaoDadosApp) {
        try {
            EstadoVersaoIntervalo estadoVersaoIntervalo;
            final Optional<Long> versaoDadosBanco = dao.getVersaoDadosIntervaloByUnidade(codUnidade);

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
            final IntervaloOfflineSupport intervaloOfflineSupport = new IntervaloOfflineSupport(estadoVersaoIntervalo);
            final List<Colaborador> colaboradores = colaboradorService.getColaboradoresComAcessoFuncaoByUnidade(
                    codUnidade,
                    Pilares.Gente.Intervalo.MARCAR_INTERVALO);
            intervaloOfflineSupport.setColaboradores(colaboradores);
            final List<TipoMarcacao> tiposIntervalo =
                    dao.getTiposIntervalosByUnidade(codUnidade,  true, true);
            intervaloOfflineSupport.setTiposIntervalo(tiposIntervalo);
            intervaloOfflineSupport.setEstadoVersaoIntervalo(estadoVersaoIntervalo);
            versaoDadosBanco.ifPresent(intervaloOfflineSupport::setVersaoDadosIntervalo);
            return intervaloOfflineSupport;
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o IntervaloOfflineSupport. \n" +
                    "codUnidade: %d \n" +
                    "versaoDadosApp: %d \n", codUnidade, versaoDadosApp), e);
            throw new RuntimeException("Erro ao criar IntervaoOfflineSupport");
        }
    }

    @Deprecated
    public Long iniciaIntervalo(Long codUnidade, Long cpf, Long codTipo) {
        try {
            return new DeprecatedControleIntervaloDaoImpl().iniciaIntervalo(codUnidade, cpf, codTipo);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao iniciar o intervalo. \n" +
                    "codUnidade: %d \n" +
                    "cpf: %d \n" +
                    "codTipo: %d", codUnidade, cpf, codTipo), e);
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public boolean insereFinalizacaoIntervalo(Intervalo intervalo, Long codUnidade) {
        try {
            return new DeprecatedControleIntervaloDaoImpl().insereFinalizacaoIntervalo(intervalo, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao inserir uma finalização de intevalo. \n" +
                    "codUnidade: %d", codUnidade), e);
            return false;
        }
    }
}