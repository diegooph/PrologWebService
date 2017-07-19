package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.AfericaoAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.AfericaoAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.CadastroAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.CadastroAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.VeiculosAtivos;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.BuscaQuestionarioColaborador;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ChecklistAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ChecklistAvaCorpAvilanSoap;
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
    public boolean insertAfericao(@NotNull Afericao afericao, @NotNull Long codUnidade) throws Exception {
        return getAfericaoSoap().incluirMedida(AvaCorpAvilanConverter.convert(afericao)).isSucesso();
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao) throws Exception {
        // TODO: passar CPF aqui.
        final BuscaQuestionarioColaborador request = getChecklistSoap().buscarQuestionariosColaborador("CPF AQUI");
        if (request != null && request.isSucesso()) {
            return AvaCorpAvilanConverter.convert(request.getQuestionarioVeiculos());
        }

        throw new Exception(request != null ? request.getMensagem() : "");
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