package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.frota.checklist.ChecklistResource;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemasFactory;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Os Routers são as classes responsáveis por direcionar o fluxo de um request depois do mesmo atingir o servidor do
 * ProLog. O que ele faz, na verdade, é verificar se a empresa do usuário que faz o request possui integração no recurso
 * que está sendo consumido. Por exemplo, se um usuário da Avilan consome um método do {@link ChecklistResource}, como
 * esse recurso possui integração com alguma empresa, o request pode ir parar em um Router (depende o método). Esse
 * Router irá verificar se a Avilan possui integração com o {@link RecursoIntegrado#CHECKLIST} e, se possuir,
 * instanciará a subclasse de {@link Sistema} correta para processar a requisição.
 */
public abstract class Router implements OperacoesIntegradas {
    @NotNull
    private final IntegracaoDao integracaoDao;
    @NotNull
    private final IntegradorProLog integradorProLog;
    @NotNull
    private final String userToken;
    @NotNull
    private final RecursoIntegrado recursoIntegrado;
    @Nullable
    private SistemaKey sistemaKey;
    private boolean hasTried;

    Router(@NotNull final IntegracaoDao integracaoDao,
           @NotNull final IntegradorProLog integradorProLog,
           @NotNull final String userToken,
           @NotNull final RecursoIntegrado recursoIntegrado) {
        checkNotNull(userToken, "userToken não pode ser null!");
        this.integracaoDao = checkNotNull(integracaoDao, "integracaoDao não pode ser null!");
        this.integradorProLog = checkNotNull(integradorProLog, "integradorProLog não pode ser null!");
        // Remove o "Bearer " de antes do token
        // TODO: tirar essa remoção daqui e do AuthorizationFilter tbm, botar em uma Utils.
        this.userToken = userToken.substring("Bearer".length()).trim();
        this.recursoIntegrado = checkNotNull(recursoIntegrado, "recursoIntegrado não pode ser null!");
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        if (getSistema() != null) {
            return getSistema().getVeiculosAtivosByUnidade(codUnidade);
        } else {
            return integradorProLog.getVeiculosAtivosByUnidade(codUnidade);
        }
    }

    @Override
    public List<TipoVeiculo> getTiposVeiculosByUnidade(@NotNull Long codUnidade) throws Exception {
        if (getSistema() != null) {
            return getSistema().getTiposVeiculosByUnidade(codUnidade);
        } else {
            return integradorProLog.getTiposVeiculosByUnidade(codUnidade);
        }
    }

    @Override
    public List<String> getPlacasVeiculosByTipo(Long codUnidade, String codTipo) throws Exception {
        if (getSistema() != null) {
            return getSistema().getPlacasVeiculosByTipo(codUnidade, codTipo);
        } else {
            return integradorProLog.getPlacasVeiculosByTipo(codUnidade, codTipo);
        }
    }

    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull Long codUnidade) throws Exception {
        if (getSistema() != null) {
            return getSistema().getCronogramaAfericao(codUnidade);
        } else {
            return integradorProLog.getCronogramaAfericao(codUnidade);
        }
    }

    @Override
    public NovaAfericao getNovaAfericao(String placaVeiculo) throws Exception {
        if (getSistema() != null) {
            return getSistema().getNovaAfericao(placaVeiculo);
        } else {
            return integradorProLog.getNovaAfericao(placaVeiculo);
        }
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao, @NotNull Long codUnidade) throws Exception {
        if (getSistema() != null) {
            return getSistema().insertAfericao(afericao, codUnidade);
        } else {
            return integradorProLog.insertAfericao(afericao, codUnidade);
        }
    }

    @Override
    public List<Afericao> getAfericoes(@NotNull Long codUnidade,
                                       @NotNull String codTipoVeiculo,
                                       @NotNull String placaVeiculo,
                                       long dataInicial,
                                       long dataFinal,
                                       long limit,
                                       long offset) throws Exception {
        return null;
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade, @NotNull Long codFuncao) throws Exception {
        if (getSistema() != null) {
            return getSistema().getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
        } else {
            return integradorProLog.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
        }
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo,
                                                      char tipoChecklist) throws Exception {
        if (getSistema() != null) {
            return getSistema().getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo, tipoChecklist);
        } else {
            return integradorProLog.getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo, tipoChecklist);
        }
    }

    @Override
    public Long insertChecklist(@NotNull Checklist checklist) throws Exception {
        if (getSistema() != null) {
            return getSistema().insertChecklist(checklist);
        } else {
            return integradorProLog.insertChecklist(checklist);
        }
    }

    @Override
    public Checklist getChecklistByCodigo(Long codChecklist) throws Exception {
        if (getSistema() != null) {
            return getSistema().getChecklistByCodigo(codChecklist);
        } else {
            return integradorProLog.getChecklistByCodigo(codChecklist);
        }
    }

    @Override
    public List<Checklist> getChecklistsByColaborador(Long cpf, int limit, long offset, boolean resumido) throws Exception {
        if (getSistema() != null) {
            return getSistema().getChecklistsByColaborador(cpf, limit, offset, resumido);
        } else {
            return integradorProLog.getChecklistsByColaborador(cpf, limit, offset, resumido);
        }
    }

    @Override
    public List<Checklist> getTodosChecklists(Date dataInicial, Date dataFinal, String equipe, Long codUnidade, String placa,
                                  int limit, long offset, boolean resumido) throws Exception {
        if (getSistema() != null) {
            return getSistema()
                    .getTodosChecklists(dataInicial, dataFinal, equipe, codUnidade, placa, limit, offset, resumido);
        } else {
            return integradorProLog
                    .getTodosChecklists(dataInicial, dataFinal, equipe, codUnidade, placa, limit, offset, resumido);
        }
    }

    @NotNull
    @Override
    public FarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                            @NotNull final Date dataInicial,
                                            @NotNull final Date dataFinal,
                                            final boolean itensCriticosRetroativos) throws Exception {
        if (getSistema() != null) {
            return getSistema().getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos);
        } else {
            return integradorProLog.getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos);
        }
    }

    @Nullable
    private Sistema getSistema() throws Exception {
        if (sistemaKey == null && !hasTried) {
            sistemaKey = integracaoDao.getSistemaKey(userToken, recursoIntegrado);
            hasTried = true;
        }
        if (sistemaKey == null) {
            return null;
        }

        return SistemasFactory.createSistema(sistemaKey, integradorProLog, userToken);
    }
}