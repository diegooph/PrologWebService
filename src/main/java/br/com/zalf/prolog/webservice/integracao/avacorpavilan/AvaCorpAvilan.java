package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.colaborador.Colaborador;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfVeiculoQuestao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequester;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Created by luiz on 7/17/17.
 */
public final class AvaCorpAvilan extends Sistema {
    @NotNull
    private final AvaCorpAvilanRequester requester;
    @Nullable
    private Colaborador colaborador;

    public AvaCorpAvilan(@NotNull final AvaCorpAvilanRequester requester,
                         @NotNull final IntegradorProLog integradorProLog,
                         @NotNull final String userToken) {
        super(integradorProLog, userToken);
        this.requester = requester;
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        return AvaCorpAvilanConverter.convert(requester.getVeiculosAtivos(cpf()));
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao)
            throws Exception {
        return AvaCorpAvilanConverter.convert(requester.getSelecaoModeloChecklistPlacaVeiculo(cpf()));
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
        final List<Veiculo> veiculos = AvaCorpAvilanConverter.convert(requester.getVeiculosAtivos(cpf()));
        for (Veiculo veiculo : veiculos) {
            if (veiculo.getPlaca().equals(placaVeiculo)) {
                final List<Pneu> pneus = AvaCorpAvilanConverter.convert(requester.getPneusVeiculo(placaVeiculo));
                final Restricao restricao = getIntegradorProLog().getRestricaoByCodUnidade(codUnidade());
                veiculo.setListPneus(pneus);

                // Cria NovaAfericao
                final NovaAfericao novaAfericao = new NovaAfericao();
                novaAfericao.setVeiculo(veiculo);
                novaAfericao.setRestricao(restricao);
                return novaAfericao;
            }
        }

        throw new IllegalStateException();
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo) throws Exception {
        final ArrayOfVeiculoQuestao questoesVeiculo = requester.getQuestoesVeiculo(
                Math.toIntExact(codModelo),
                placaVeiculo,
                cpf(),
                dataNascimento());
        return AvaCorpAvilanConverter.convert(questoesVeiculo, placaVeiculo);
    }

    private String cpf() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        // Preenche com 0 a esquerda caso CPF tenha menos do que 11 caracteres
        return String.format("%011d",  colaborador.getCpf());
    }

    private String dataNascimento() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        return AvaCorpAvilanUtils.createDatePattern(colaborador.getDataNascimento());
    }

    private Long codUnidade() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        return colaborador.getUnidade().getCodigo();
    }
}