package br.com.zalf.prolog.webservice.gente.controlejornada.OLD;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.gente.controlejornada.ControleJornadaDao;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.*;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Zart on 19/08/2017.
 */
@Deprecated
public class DeprecatedControleIntervaloService_2 {

    private static final String TAG = DeprecatedControleIntervaloService_2.class.getSimpleName();
    private DeprecatedControleIntervaloDao_2 dao = new DeprecatedControleIntervaloDaoImpl_2();
    @NotNull
    private final ControleJornadaDao daoNova = Injection.provideControleJornadaDao();

    public IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(Long codUnidade, Long cpf, Long codTipoIntervalo) throws Throwable {
        try {
            return daoNova.getUltimaMarcacaoInicioNaoFechada(codUnidade, cpf, codTipoIntervalo);
        } catch (Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar os intervalos em abertos de um colaborador. \n" +
                    "cpf: %d", cpf), e);
            throw e;
        }
    }

    public ResponseIntervalo insertMarcacaoIntervalo(long versaoDadosIntervalo, IntervaloMarcacao intervaloMarcacao) {
        EstadoVersaoIntervalo estadoVersaoIntervalo = null;
        try {
            final Long codUnidade = intervaloMarcacao.getCodUnidade();
            // Temos certeza que existira no banco, se não existir, então melhor dar erro.
            @SuppressWarnings("ConstantConditions")
            final DadosMarcacaoUnidade versaoDados = daoNova.getDadosMarcacaoUnidade(codUnidade).get();
            if (versaoDadosIntervalo < versaoDados.getVersaoDadosBanco()) {
                estadoVersaoIntervalo = EstadoVersaoIntervalo.VERSAO_DESATUALIZADA;
            } else {
                estadoVersaoIntervalo = EstadoVersaoIntervalo.VERSAO_ATUALIZADA;
            }
            daoNova.insertMarcacaoIntervalo(intervaloMarcacao);
            return ResponseIntervalo.ok("Intervalo inserido com sucesso", estadoVersaoIntervalo);
        } catch (Throwable e) {
            Log.e(TAG, String.format("Erro ao inserir ou atualizar um intervalo. \n" +
                    "versaoDadosIntervalo: %d", versaoDadosIntervalo), e);
            return ResponseIntervalo.error("Erro ao inserir intervalo", estadoVersaoIntervalo);
        }
    }

    public List<Intervalo> getMarcacoesIntervaloColaborador(Long codUnidade, Long cpf, String codTipo, long limit, long offset) {
        try {
            return daoNova.getMarcacoesIntervaloColaborador(codUnidade, cpf, codTipo, limit, offset);
        } catch (Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar os intervalos de um colaborador. \n" +
                    "cpf: %s \n" +
                    "codTipo: %s \n" +
                    "limit: %d \n" +
                    "offset: %d", cpf, codTipo, limit, offset), e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public IntervaloOfflineSupport getIntervaloOfflineSupport(Long versaoDadosApp,
                                                              Long codUnidade,
                                                              ColaboradorService colaboradorService) {
        IntervaloOfflineSupport intervaloOfflineSupport;
        try {
            final List<Colaborador> colaboradores = colaboradorService.getColaboradoresComAcessoFuncaoByUnidade(
                    codUnidade,
                    Pilares.Gente.Intervalo.MARCAR_INTERVALO);
            final List<TipoMarcacao> tiposIntervalo = Injection.provideTipoMarcacaoDao().getTiposMarcacoes(codUnidade,  true, true);
            final Optional<DadosMarcacaoUnidade> versaoDados = daoNova.getDadosMarcacaoUnidade(codUnidade);
            EstadoVersaoIntervalo estadoVersaoIntervalo;

            // Isso é algo importante para se destacar: se ao buscarmos a versão dos dados de intervalo para uma unidade
            // e não existir nada, assumimos que a unidade também não possui nenhum colaborador com acesso a essa
            // funcionalidade, o que faz sentido. Além disso, poupamos uma nova requisição ao banco, agilizando o login.
            // Porém, para isso funcionar bem, o ProLog deve garantir que se existe alguém de uma unidade com permissão de
            // marcação de intervalo, DEVE existir para essa unidade um valor de versão dos dados.
            if (!versaoDados.isPresent()) {
                estadoVersaoIntervalo = EstadoVersaoIntervalo.UNIDADE_SEM_USO_INTERVALO;
            } else {
                final Long versaoDadosBanco = versaoDados.get().getVersaoDadosBanco();
                // Se a unidade tem uma versão dos dados de intervalo no banco, nós precisamos comparar com a versão que
                // o App enviou.
                if (versaoDadosApp != null && versaoDadosApp.equals(versaoDadosBanco)) {
                    // Se a versão está atualizada não precisamos setar mais nada no IntervaloOfflineSupport.
                    estadoVersaoIntervalo = EstadoVersaoIntervalo.VERSAO_ATUALIZADA;
                } else {
                    if (versaoDadosApp != null && versaoDadosApp > versaoDadosBanco) {
                        // Isso nunca deveria acontecer! Porém, para não impedirmos o login do usuário, vamos retornar
                        // como se sua versão estivesse desatualizada e mandar os dados que temos.
                        Log.e(TAG, "Erro versão dados intervalo",
                                new IllegalStateException("Versão dos dados do app ("
                                        + versaoDadosApp + ") não pode ser " +
                                        "maior do que a versão dos dados no banco("
                                        + versaoDadosBanco + ")!"));
                    }
                    estadoVersaoIntervalo = EstadoVersaoIntervalo.VERSAO_DESATUALIZADA;
                }
            }
            // Criamos o objeto.
            intervaloOfflineSupport = new IntervaloOfflineSupport(estadoVersaoIntervalo);
            intervaloOfflineSupport.setColaboradores(colaboradores);
            intervaloOfflineSupport.setTiposIntervalo(tiposIntervalo);
            intervaloOfflineSupport.setEstadoVersaoIntervalo(estadoVersaoIntervalo);
            if (versaoDados.isPresent()) {
                intervaloOfflineSupport.setVersaoDadosIntervalo(versaoDados.get().getVersaoDadosBanco());
                intervaloOfflineSupport.setTokenSincronizacaoMarcacao(versaoDados.get().getTokenSincronizacaoMarcacao());
            }
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar o IntervaloOfflineSupport. \n" +
                    "codUnidade: %d \n" +
                    "versaoDadosApp: %d \n", codUnidade, versaoDadosApp), t);
            throw new RuntimeException("Erro ao criar IntervaoOfflineSupport");
        }

        return intervaloOfflineSupport;
    }

    @Deprecated
    public Long iniciaIntervalo(Long codUnidade, Long cpf, Long codTipo) {
        try {
            return new DeprecatedControleIntervaloDaoImpl_1().iniciaIntervalo(codUnidade, cpf, codTipo);
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
            return new DeprecatedControleIntervaloDaoImpl_1().insereFinalizacaoIntervalo(intervalo, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao inserir uma finalização de intevalo. \n" +
                    "codUnidade: %d", codUnidade), e);
            return false;
        }
    }
}