package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.AfericaoAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.AfericaoAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.CadastroAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.CadastroAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.PneusVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.VeiculosAtivos;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegradorHttp;
import com.sun.istack.internal.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created by luiz on 18/07/17.
 */
public final class IntegradorHttpAvaCorpAvilan extends IntegradorHttp {

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        // TODO: passar CPF aqui.
        final VeiculosAtivos request = getCadastroSoap().buscarVeiculosAtivos("CPF AQUI");
        if (request != null && request.isSucesso()) {
            return AvaCorpAvilanConverter.convert(request.getListaVeiculos());
        }

        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public NovaAfericao getNovaAfericao(String placaVeiculo) throws Exception {
        final PneusVeiculo request = getCadastroSoap().buscarPneusVeiculo(placaVeiculo);
        if (request != null && request.isSucesso()) {
            // TODO: aqui precisariamos ter uma busca no BD do ProLog para também montar o objeto Restrição, melhor
            // caminho deve ser deixar isso por conta da classe Sistema específica
            final List<Pneu> pneus = AvaCorpAvilanConverter.convert(request.getPneus());
            return new NovaAfericao();
        }

        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao, @NotNull Long codUnidade) throws Exception {
        return getAfericaoSoap().incluirMedida(AvaCorpAvilanConverter.convert(afericao)).isSucesso();
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao) throws Exception {
        // TODO: passar CPF aqui.
        final BuscaQuestionarioColaborador request = getChecklistSoap().buscarQuestionariosColaborador(""
                /* TODO: CPF AQUI*/);
        if (request != null && request.isSucesso()) {
            return AvaCorpAvilanConverter.convert(request.getQuestionarioVeiculos());
        }

        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo) throws Exception {
        final AdicionarChecklist adicionarChecklist = new AdicionarChecklist();
        adicionarChecklist.setCpf("" /* TODO: passar CPF aqui */);
        adicionarChecklist.setDtNascimento("" /* TODO: passar data nascimento aqui */);
        adicionarChecklist.setVeiculo(placaVeiculo);
        // As demais informações do objeto AdicionarChecklist não precisam ser setadas

        final PerguntasAlternativasQuestionario request
                = getChecklistSoap().buscarPerguntasAlternativasQuestionario(adicionarChecklist);
        if (request != null && request.isSucesso()) {
            // Esse request retorna uma lista de VeiculoQuestao pois, dado um veículo ABC,
            // caso queiramos buscar seu questionário, ele pode estar atrelado a carretas DIK e XYZ, por exemplo.
            // Desse modo, as questões vêm separadas por veículo. Como essa distinção não existe no ProLog, iremos
            // agrupar tudo na mesma lista de perguntas.
            return AvaCorpAvilanConverter.convert(request.getVeiculoQuestoes(), placaVeiculo);
        }


        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public boolean insertChecklist(@NotNull Checklist checklist) throws Exception {
        return getChecklistSoap().enviarChecklist(AvaCorpAvilanConverter.convert(checklist)).isSucesso();
    }

    private CadastroAvaCorpAvilanSoap getCadastroSoap() {
        return new CadastroAvaCorpAvilanService().getCadastroSoap();
    }

    private AfericaoAvaCorpAvilanSoap getAfericaoSoap() {
        return new AfericaoAvaCorpAvilanService().getAfericaoSoap();
    }

    private ChecklistAvaCorpAvilanSoap getChecklistSoap() {
        return new ChecklistAvaCorpAvilanService().getChecklistSoap();
    }
}