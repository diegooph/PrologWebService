package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemasFactory;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 18/07/17.
 */
public abstract class Router implements OperacoesIntegradas {
    @NotNull
    private final IntegracaoDao integracaoDao;
    @NotNull
    private final Integrador integradorDatabase;
    @NotNull
    private final String userToken;
    @NotNull
    private final RecursoIntegrado recursoIntegrado;
    @Nullable
    private SistemaKey sistemaKey;
    private boolean hasTried;

    public Router(@NotNull final IntegracaoDao integracaoDao,
                  @NotNull final Integrador integradorDatabase,
                  @NotNull final String userToken,
                  @NotNull final RecursoIntegrado recursoIntegrado) {
        this.integracaoDao = checkNotNull(integracaoDao, "integracaoDao não pode ser null!");
        this.integradorDatabase = checkNotNull(integradorDatabase, "integradorDatabase não pode ser null!");
        this.userToken = checkNotNull(userToken, "userToken não pode ser null!");
        this.recursoIntegrado = checkNotNull(recursoIntegrado, "recursoIntegrado não pode ser null!");
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        if (getSistema() != null) {
            return getSistema().getVeiculosAtivosByUnidade(codUnidade);
        } else {
            return integradorDatabase.getVeiculosAtivosByUnidade(codUnidade);
        }
    }

    @Override
    public NovaAfericao getNovaAfericao(String placaVeiculo) throws Exception {
        if (getSistema() != null) {
            return getSistema().getNovaAfericao(placaVeiculo);
        } else {
            return integradorDatabase.getNovaAfericao(placaVeiculo);
        }
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao, @NotNull Long codUnidade) throws Exception {
        if (getSistema() != null) {
            return getSistema().insertAfericao(afericao, codUnidade);
        } else {
            return integradorDatabase.insertAfericao(afericao, codUnidade);
        }
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade, @NotNull Long codFuncao) throws Exception {
        if (getSistema() != null) {
            return getSistema().getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
        } else {
            return integradorDatabase.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
        }
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade, @NotNull Long codModelo, @NotNull String placaVeiculo) throws Exception {
        if (getSistema() != null) {
            return getSistema().getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo);
        } else {
            return integradorDatabase.getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo);
        }
    }

    @Override
    public boolean insertChecklist(@NotNull Checklist checklist) throws Exception {
        if (getSistema() != null) {
            return getSistema().insertChecklist(checklist);
        } else {
            return integradorDatabase.insertChecklist(checklist);
        }
    }

    private Sistema getSistema() throws Exception {
        if (sistemaKey == null && !hasTried) {
            sistemaKey = integracaoDao.getSistemaKey(userToken, recursoIntegrado);
            hasTried = true;
        }

        return SistemasFactory.createSistema(sistemaKey, integradorDatabase);
    }

}