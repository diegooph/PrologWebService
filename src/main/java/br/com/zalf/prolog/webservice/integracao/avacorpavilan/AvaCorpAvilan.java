package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.colaborador.Colaborador;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.VeiculoLiberacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfVeiculoQuestao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequester;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Subclasse de {@link Sistema} responsável por cuidar da integração com o AvaCorp para a empresa Avilan.
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
        return AvaCorpAvilanConverter.convert(requester.getVeiculosAtivos(cpf(), dataNascimento()));
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao)
            throws Exception {
        return AvaCorpAvilanConverter.convert(requester.getSelecaoModeloChecklistPlacaVeiculo(cpf(), dataNascimento()));
    }

    @Override
    public boolean insertChecklist(@NotNull Checklist checklist) throws Exception {
        return requester.insertChecklist(
                AvaCorpAvilanConverter.convert(checklist, cpf(), dataNascimento()),
                cpf(),
                dataNascimento());
    }

    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull Long codUnidade) throws Exception {
        final Restricao restricao = getIntegradorProLog().getRestricaoByCodUnidade(codUnidade);
        final ArrayOfVeiculo arrayOfVeiculo = requester.getVeiculosAtivos(cpf(), dataNascimento());
        return AvaCorpAvilanConverter.convert(arrayOfVeiculo, restricao);
    }

    @Override
    public NovaAfericao getNovaAfericao(@NotNull String placaVeiculo) throws Exception {
        final Veiculo veiculo = AvaCorpAvilanConverter.convert(requester.getVeiculoAtivo(placaVeiculo, cpf(), dataNascimento()));
        final List<Pneu> pneus = AvaCorpAvilanConverter.convert(requester.getPneusVeiculo(placaVeiculo, cpf(), dataNascimento()));
        final Restricao restricao = getIntegradorProLog().getRestricaoByCodUnidade(codUnidade());
        final DiagramaVeiculo diagramaVeiculo = getIntegradorProLog().getDiagramaVeiculoByPlaca(placaVeiculo);
        veiculo.setDiagrama(diagramaVeiculo);
        veiculo.setListPneus(pneus);

        // Cria NovaAfericao.
        final NovaAfericao novaAfericao = new NovaAfericao();
        novaAfericao.setVeiculo(veiculo);
        novaAfericao.setRestricao(restricao);
        novaAfericao.setEstepesVeiculo(veiculo.getEstepes());
        veiculo.removeEstepes();
        return novaAfericao;
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao,
                                  @NotNull Long codUnidade) throws Exception {
        return requester.insertAfericao(AvaCorpAvilanConverter.convert(afericao), cpf(), dataNascimento());
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

    @Override
    public List<VeiculoLiberacao> getStatusLiberacaoVeiculos(Long codUnidade) throws Exception {
        final String codUnidadeAvilan = getIntegradorProLog().getCodUnidadeClienteByCodUnidadeProLog(codUnidade);
        final Object farolChecklist = requester.getFarolChecklist(codUnidadeAvilan, cpf(), dataNascimento());
        return AvaCorpAvilanConverter.convert(farolChecklist);
    }

    private String cpf() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        // Preenche com 0 a esquerda caso CPF tenha menos do que 11 caracteres.
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