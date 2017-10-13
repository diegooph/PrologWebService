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
import com.sun.istack.internal.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 7/17/17.
 */
public abstract class Sistema implements OperacoesIntegradas {
    @NotNull
    private final IntegradorProLog integradorProLog;
    @NotNull
    private final SistemaKey sistemaKey;
    @NotNull
    private final String userToken;

    protected Sistema(IntegradorProLog integradorProLog, SistemaKey sistemaKey, String userToken) {
        this.integradorProLog = checkNotNull(integradorProLog, "integradorProLog não pode ser nulo!");
        this.sistemaKey = checkNotNull(sistemaKey, "sistemaKey não pode ser nulo!");
        this.userToken = checkNotNull(userToken, "userToken não pode ser nulo!");
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        return getIntegradorProLog().getVeiculosAtivosByUnidade(codUnidade);
    }

    @Override
    public List<TipoVeiculo> getTiposVeiculosByUnidade(@NotNull Long codUnidade) throws Exception {
        return getIntegradorProLog().getTiposVeiculosByUnidade(codUnidade);
    }

    @Override
    public List<String> getPlacasVeiculosByTipo(@NotNull Long codUnidade, @NotNull String codTipo) throws Exception {
        return getIntegradorProLog().getPlacasVeiculosByTipo(codUnidade, codTipo);
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao) throws Exception {
        return getIntegradorProLog().getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
    }

    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull Long codUnidade) throws Exception {
        return getIntegradorProLog().getCronogramaAfericao(codUnidade);
    }

    @Override
    public NovaAfericao getNovaAfericao(String placaVeiculo) throws Exception {
        return getIntegradorProLog().getNovaAfericao(placaVeiculo);
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao, @NotNull Long codUnidade) throws Exception {
        return getIntegradorProLog().insertAfericao(afericao, codUnidade);
    }

    @Override
    public List<Afericao> getAfericoes(@NotNull Long codUnidade,
                                       @NotNull String codTipoVeiculo,
                                       @NotNull String placaVeiculo,
                                       long dataInicial,
                                       long dataFinal,
                                       long limit,
                                       long offset) throws Exception {
        return getIntegradorProLog()
                .getAfericoes(codUnidade, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal, limit, offset);
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo,
                                                      char tipoChecklist) throws Exception {
        return getIntegradorProLog().getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo, tipoChecklist);
    }

    @Override
    public Long insertChecklist(@NotNull Checklist checklist) throws Exception {
        return getIntegradorProLog().insertChecklist(checklist);
    }

    @Override
    public Checklist getChecklistByCodigo(Long codChecklist) throws Exception {
        return getIntegradorProLog().getChecklistByCodigo(codChecklist);
    }

    @Override
    public List<Checklist> getChecklistsByColaborador(Long cpf, int limit, long offset, boolean resumido) throws Exception {
        return getIntegradorProLog().getChecklistsByColaborador(cpf, limit, offset, resumido);
    }

    @Override
    public List<Checklist> getAll(Date dataInicial, Date dataFinal, String equipe, Long codUnidade, String placa,
                                  long limit, long offset, boolean resumido) throws Exception {
        return getIntegradorProLog().getAll(dataInicial, dataFinal, equipe, codUnidade, placa, limit, offset, resumido);
    }

    @Override
    public FarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                            @NotNull final Date dataInicial,
                                            @NotNull final Date dataFinal,
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