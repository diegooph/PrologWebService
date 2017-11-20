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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nonnull
    private final IntegracaoDao integracaoDao;
    @Nonnull
    private final IntegradorProLog integradorProLog;
    @Nonnull
    private final String userToken;
    @Nonnull
    private final RecursoIntegrado recursoIntegrado;
    @Nullable
    private SistemaKey sistemaKey;
    private boolean hasTried;

    Router(@Nonnull final IntegracaoDao integracaoDao,
           @Nonnull final IntegradorProLog integradorProLog,
           @Nonnull final String userToken,
           @Nonnull final RecursoIntegrado recursoIntegrado) {
        checkNotNull(userToken, "userToken não pode ser null!");
        this.integracaoDao = checkNotNull(integracaoDao, "integracaoDao não pode ser null!");
        this.integradorProLog = checkNotNull(integradorProLog, "integradorProLog não pode ser null!");
        // Remove o "Bearer " de antes do token
        // TODO: tirar essa remoção daqui e do AuthorizationFilter tbm, botar em uma Utils.
        this.userToken = userToken.substring("Bearer".length()).trim();
        this.recursoIntegrado = checkNotNull(recursoIntegrado, "recursoIntegrado não pode ser null!");
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@Nonnull Long codUnidade) throws Exception {
        if (getSistema() != null) {
            return getSistema().getVeiculosAtivosByUnidade(codUnidade);
        } else {
            return integradorProLog.getVeiculosAtivosByUnidade(codUnidade);
        }
    }

    @Override
    public List<TipoVeiculo> getTiposVeiculosByUnidade(@Nonnull Long codUnidade) throws Exception {
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
    public CronogramaAfericao getCronogramaAfericao(@Nonnull Long codUnidade) throws Exception {
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
    public boolean insertAfericao(@Nonnull Afericao afericao, @Nonnull Long codUnidade) throws Exception {
        if (getSistema() != null) {
            return getSistema().insertAfericao(afericao, codUnidade);
        } else {
            return integradorProLog.insertAfericao(afericao, codUnidade);
        }
    }

    @Nonnull
    @Override
    public Afericao getAfericaoByCodigo(@Nonnull Long codUnidade, @Nonnull Long codAfericao) throws Exception {
        if (getSistema() != null) {
            return getSistema().getAfericaoByCodigo(codUnidade, codAfericao);
        } else {
            return integradorProLog.getAfericaoByCodigo(codUnidade, codAfericao);
        }
    }

    @Override
    public List<Afericao> getAfericoes(@Nonnull Long codUnidade,
                                       @Nonnull String codTipoVeiculo,
                                       @Nonnull String placaVeiculo,
                                       long dataInicial,
                                       long dataFinal,
                                       int limit,
                                       long offset) throws Exception {
        if (getSistema() != null) {
            return getSistema().getAfericoes(codUnidade, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal,
                    limit, offset);
        } else {
            return integradorProLog.getAfericoes(codUnidade, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal,
                    limit, offset);
        }
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@Nonnull Long codUnidade,
                                                                                    @Nonnull Long codFuncao) throws Exception {
        if (getSistema() != null) {
            return getSistema().getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
        } else {
            return integradorProLog.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
        }
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@Nonnull Long codUnidade,
                                                      @Nonnull Long codModelo,
                                                      @Nonnull String placaVeiculo,
                                                      char tipoChecklist) throws Exception {
        if (getSistema() != null) {
            return getSistema().getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo, tipoChecklist);
        } else {
            return integradorProLog.getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo, tipoChecklist);
        }
    }

    @Override
    public Long insertChecklist(@Nonnull Checklist checklist) throws Exception {
        if (getSistema() != null) {
            return getSistema().insertChecklist(checklist);
        } else {
            return integradorProLog.insertChecklist(checklist);
        }
    }

    @Nonnull
    @Override
    public Checklist getChecklistByCodigo(@Nonnull Long codChecklist) throws Exception {
        if (getSistema() != null) {
            return getSistema().getChecklistByCodigo(codChecklist);
        } else {
            return integradorProLog.getChecklistByCodigo(codChecklist);
        }
    }

    @Nonnull
    @Override
    public List<Checklist> getChecklistsByColaborador(@Nonnull final Long cpf,
                                                      @Nullable final Long dataInicial,
                                                      @Nullable final Long dataFinal,
                                                      final int limit,
                                                      final long offset,
                                                      final boolean resumido) throws Exception {
        if (getSistema() != null) {
            return getSistema().getChecklistsByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
        } else {
            return integradorProLog.getChecklistsByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
        }
    }

    @Nonnull
    @Override
    public List<Checklist> getTodosChecklists(@Nonnull final Long codUnidade,
                                              @Nullable final Long codEquipe,
                                              @Nullable final Long codTipoVeiculo,
                                              @Nullable final String placaVeiculo,
                                              final long dataInicial,
                                              final long dataFinal,
                                              final int limit,
                                              final long offset,
                                              final boolean resumido) throws Exception {
        if (getSistema() != null) {
            return getSistema()
                    .getTodosChecklists(codUnidade, codEquipe, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal,
                            limit, offset, resumido);
        } else {
            return integradorProLog
                    .getTodosChecklists(codUnidade, codEquipe, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal, limit, offset, resumido);
        }
    }

    @Nonnull
    @Override
    public FarolChecklist getFarolChecklist(@Nonnull final Long codUnidade,
                                            @Nonnull final Date dataInicial,
                                            @Nonnull final Date dataFinal,
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