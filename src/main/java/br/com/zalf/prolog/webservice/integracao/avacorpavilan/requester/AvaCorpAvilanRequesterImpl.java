package br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.AfericaoAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.AfericaoAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.header.HeaderHandlerResolver;
import com.sun.istack.internal.NotNull;


/**
 * Created by luiz on 24/07/17.
 */
public class AvaCorpAvilanRequesterImpl implements AvaCorpAvilanRequester {

    @Override
    public ArrayOfVeiculo getVeiculosAtivos(@NotNull String cpf) throws Exception {
        final VeiculosAtivos request = getCadastroSoap().buscarVeiculosAtivos(cpf);
        if (request != null && request.isSucesso()) {
            return request.getListaVeiculos();
        }

        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public ArrayOfQuestionarioVeiculos getSelecaoModeloChecklistPlacaVeiculo(@NotNull String cpf) throws Exception {
        final BuscaQuestionarioColaborador request = getChecklistSoap().buscarQuestionariosColaborador(cpf);
        if (request != null && request.isSucesso()) {
            return request.getQuestionarioVeiculos();
        }

        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public boolean insertChecklist(@NotNull RespostasAvaliacao respostasAvaliacao) throws Exception {
        return getChecklistSoap().enviarChecklist(respostasAvaliacao).isSucesso();
    }

    @Override
    public ArrayOfVeiculoQuestao getQuestoesVeiculo(int codigoQuestionario,
                                                    @NotNull String placaVeiculo,
                                                    @NotNull String cpf,
                                                    @NotNull String dataNascimento) throws Exception {
        final AdicionarChecklist adicionarChecklist = new AdicionarChecklist();
        adicionarChecklist.setCpf(cpf);
        adicionarChecklist.setDtNascimento(dataNascimento);
        adicionarChecklist.setVeiculo(placaVeiculo);
        adicionarChecklist.setCodigoQuestionario(codigoQuestionario);
        // As demais informações do objeto AdicionarChecklist não precisam ser setadas

        final PerguntasAlternativasQuestionario request
                = getChecklistSoap().buscarPerguntasAlternativasQuestionario(adicionarChecklist);
        if (request != null && request.isSucesso()) {
            // Esse request retorna uma lista de VeiculoQuestao pois, dado um veículo ABC,
            // caso queiramos buscar seu questionário, ele pode estar atrelado a carretas DIK e XYZ, por exemplo.
            // Desse modo, as questões vêm separadas por veículo. Como essa distinção não existe no ProLog, iremos
            // agrupar tudo na mesma lista de perguntas.
            return request.getVeiculoQuestoes();
        }


        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public boolean insertAfericao(@NotNull IncluirMedida2 medidas) throws Exception {
        return getAfericaoSoap().incluirMedida(medidas).isSucesso();
    }

    @Override
    public ArrayOfPneu getPneusVeiculo(@NotNull String placaVeiculo) throws Exception {
        final PneusVeiculo request = getCadastroSoap().buscarPneusVeiculo(placaVeiculo);
        if (request != null && request.isSucesso()) {
            return request.getPneus();
        }

        throw new Exception(request != null ? request.getMensagem() : "");
    }


    private CadastroAvaCorpAvilanSoap getCadastroSoap() {
        CadastroAvaCorpAvilanService service = new CadastroAvaCorpAvilanService();
        service.setHandlerResolver(new HeaderHandlerResolver());
        return service.getCadastroSoap();
    }

    private AfericaoAvaCorpAvilanSoap getAfericaoSoap() {
        AfericaoAvaCorpAvilanService service = new AfericaoAvaCorpAvilanService();
        service.setHandlerResolver(new HeaderHandlerResolver());
        return service.getAfericaoSoap();
    }

    private ChecklistAvaCorpAvilanSoap getChecklistSoap() {
        ChecklistAvaCorpAvilanService service = new ChecklistAvaCorpAvilanService();
        service.setHandlerResolver(new HeaderHandlerResolver());
        return service.getChecklistSoap();
    }
}