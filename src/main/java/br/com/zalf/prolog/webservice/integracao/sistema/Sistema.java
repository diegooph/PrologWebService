package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 7/17/17.
 */
public abstract class Sistema implements OperacoesIntegradas {
    @Nonnull
    private final IntegradorProLog integradorProLog;
    @Nonnull
    private final SistemaKey sistemaKey;
    @Nonnull
    private final String userToken;

    protected Sistema(IntegradorProLog integradorProLog, SistemaKey sistemaKey, String userToken) {
        this.integradorProLog = checkNotNull(integradorProLog, "integradorProLog não pode ser nulo!");
        this.sistemaKey = checkNotNull(sistemaKey, "sistemaKey não pode ser nulo!");
        this.userToken = checkNotNull(userToken, "userToken não pode ser nulo!");
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@Nonnull Long codUnidade) throws Exception {
        return getIntegradorProLog().getVeiculosAtivosByUnidade(codUnidade);
    }

    @Override
    public List<TipoVeiculo> getTiposVeiculosByUnidade(@Nonnull Long codUnidade) throws Exception {
        return getIntegradorProLog().getTiposVeiculosByUnidade(codUnidade);
    }

    @Override
    public List<String> getPlacasVeiculosByTipo(@Nonnull Long codUnidade, @Nonnull String codTipo) throws Exception {
        return getIntegradorProLog().getPlacasVeiculosByTipo(codUnidade, codTipo);
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@Nonnull Long codUnidade,
                                                                                    @Nonnull Long codFuncao) throws Exception {
        return getIntegradorProLog().getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
    }

    @Override
    public CronogramaAfericao getCronogramaAfericao(@Nonnull Long codUnidade) throws Exception {
        return getIntegradorProLog().getCronogramaAfericao(codUnidade);
    }

    @Override
    public NovaAfericao getNovaAfericao(String placaVeiculo) throws Exception {
        return getIntegradorProLog().getNovaAfericao(placaVeiculo);
    }

    @Override
    public boolean insertAfericao(@Nonnull Afericao afericao, @Nonnull Long codUnidade) throws Exception {
        return getIntegradorProLog().insertAfericao(afericao, codUnidade);
    }

    @Override
    public List<Afericao> getAfericoes(@Nonnull Long codUnidade,
                                       @Nonnull String codTipoVeiculo,
                                       @Nonnull String placaVeiculo,
                                       long dataInicial,
                                       long dataFinal,
                                       long limit,
                                       long offset) throws Exception {
        return getIntegradorProLog()
                .getAfericoes(codUnidade, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal, limit, offset);
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@Nonnull Long codUnidade,
                                                      @Nonnull Long codModelo,
                                                      @Nonnull String placaVeiculo,
                                                      char tipoChecklist) throws Exception {
        return getIntegradorProLog().getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo, tipoChecklist);
    }

    @Override
    public Long insertChecklist(@Nonnull Checklist checklist) throws Exception {
        return getIntegradorProLog().insertChecklist(checklist);
    }

    @Nonnull
    @Override
    public Checklist getChecklistByCodigo(@Nonnull Long codChecklist) throws Exception {
        return getIntegradorProLog().getChecklistByCodigo(codChecklist);
    }

    @Nonnull
    @Override
    public List<Checklist> getChecklistsByColaborador(@Nonnull Long cpf, int limit, long offset, boolean resumido) throws Exception {
        return getIntegradorProLog().getChecklistsByColaborador(cpf, limit, offset, resumido);
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
        return getIntegradorProLog().getTodosChecklists(codUnidade, codEquipe, codTipoVeiculo, placaVeiculo,
                dataInicial, dataFinal, limit, offset, resumido);
    }

    @Nonnull
    @Override
    public FarolChecklist getFarolChecklist(@Nonnull final Long codUnidade,
                                            @Nonnull final Date dataInicial,
                                            @Nonnull final Date dataFinal,
                                            final boolean itensCriticosRetroativos) throws Exception {
        return getIntegradorProLog().getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos);
    }

    protected IntegradorProLog getIntegradorProLog() {
        return integradorProLog;
    }

    protected SistemaKey getSistemaKey() {
        return sistemaKey;
    }

    protected String getUserToken() {
        return userToken;
    }
}