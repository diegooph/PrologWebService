package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.*;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Zart on 19/08/2017.
 */
public class ControleIntervaloService {

    private static final String TAG = ControleIntervaloService.class.getSimpleName();
    private ControleIntervaloDao dao = new ControleIntervaloDaoImpl();

    public List<TipoIntervalo> getTiposIntervalos(Long codUnidade, boolean withCargos) {
        try {
            return dao.getTiposIntervalosByUnidade(codUnidade, withCargos);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Intervalo getIntervaloAberto(Long cpf, TipoIntervalo tipoInvervalo) throws Exception {
        try {
            return dao.getIntervaloAberto(cpf, tipoInvervalo);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public ResponseIntervalo insertOrUpdateIntervalo(long versaoDadosIntervalo, Intervalo intervalo) {
        EstadoVersaoIntervalo estadoVersaoIntervalo = null;
        try {
            final long codUnidade = intervalo.getColaborador().getCodUnidade();
            // Temos certeza que existira no banco, se não existir, então melhor dar erro.
            @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
            final long versaoDadosBanco = dao.getVersaoDadosIntervaloByUnidade(codUnidade).get();
            if (versaoDadosIntervalo < versaoDadosBanco) {
                estadoVersaoIntervalo = EstadoVersaoIntervalo.VERSAO_DESATUALIZADA;
            } else {
                estadoVersaoIntervalo = EstadoVersaoIntervalo.VERSAO_ATUALIZADA;
            }
            dao.insertOrUpdateIntervalo(intervalo);
            return ResponseIntervalo.ok("Intervalo inserido com sucesso", estadoVersaoIntervalo);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseIntervalo.error("Erro ao inserir intervalo", estadoVersaoIntervalo);
        }
    }

    public List<Intervalo> getIntervalosColaborador(Long cpf, String codTipo,long limit ,long offset) {
        try {
            return dao.getIntervalosColaborador(cpf, codTipo, limit, offset);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateTipoIntervalo(@NotNull final TipoIntervalo tipoIntervalo) {
        try {
            dao.updateTipoIntervalo(tipoIntervalo, new VersaoDadosIntervaloAtualizador());
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    @NotNull
    public IntervaloOfflineSupport getIntervaloOfflineSupport(Long versaoDadosApp,
                                                              Long codUnidade,
                                                              ColaboradorService colaboradorService) {
        IntervaloOfflineSupport intervaloOfflineSupport = null;
        try {
            final List<Colaborador> colaboradores = colaboradorService.getColaboradoresComAcessoFuncaoByUnidade(
                    Pilares.Gente.Intervalo.MARCAR_INTERVALO,
                    codUnidade);
            final List<TipoIntervalo> tiposIntervalo = dao.getTiposIntervalosByUnidade(codUnidade, false);
            final Optional<Long> versaoDadosBanco = dao.getVersaoDadosIntervaloByUnidade(codUnidade);
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
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar IntervaoOfflineSupport");
        }

        return intervaloOfflineSupport;
    }

    @Deprecated
    public Long iniciaIntervalo(Long codUnidade, Long cpf, Long codTipo) {
        try {
            return dao.iniciaIntervalo(codUnidade, cpf, codTipo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public boolean insereFinalizacaoIntervalo(Intervalo intervalo, Long codUnidade) {
        try {
            return dao.insereFinalizacaoIntervalo(intervalo, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}