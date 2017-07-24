package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfVeiculoQuestao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequester;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import com.sun.istack.internal.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created by luiz on 7/17/17.
 */
public final class AvaCorpAvilan extends Sistema {
    private final AvaCorpAvilanRequester requester;

    public AvaCorpAvilan(@NotNull final AvaCorpAvilanRequester requester,
                         @NotNull final IntegradorProLog integradorProLog) {
        super(integradorProLog);
        this.requester = requester;
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        // TODO: CPF
        return AvaCorpAvilanConverter.convert(requester.getVeiculosAtivos(""));
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao)
            throws Exception {
        // TODO: CPF
        return AvaCorpAvilanConverter.convert(requester.getSelecaoModeloChecklistPlacaVeiculo(""));
    }

    @Override
    public boolean insertChecklist(@NotNull Checklist checklist) throws Exception {
        return requester.insertChecklist(AvaCorpAvilanConverter.convert(checklist));
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao,
                                  @NotNull Long codUnidade) throws Exception {
        return requester.insertAfericao(AvaCorpAvilanConverter.convert(afericao));
    }

    @Override
    public NovaAfericao getNovaAfericao(@NotNull String placaVeiculo) throws Exception {
        final List<Pneu> pneus = AvaCorpAvilanConverter.convert(requester.getPneusVeiculo(placaVeiculo));
        // TODO: buscar restrição BD ProLog
        return new NovaAfericao();
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo) throws Exception {
        // TODO: cpf e data de nascimento
        final ArrayOfVeiculoQuestao questoesVeiculo = requester.getQuestoesVeiculo(
                Math.toIntExact(codModelo),
                placaVeiculo,
                "CPF",
                "DATA_NASCIMENTO");
        return AvaCorpAvilanConverter.convert(questoesVeiculo, placaVeiculo);
    }
}