package br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.AfericaoAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.AfericaoAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.header.HeaderEntry;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.header.HeaderUtils;
import com.sun.istack.internal.NotNull;

import javax.xml.ws.BindingProvider;


/**
 * Created by luiz on 24/07/17.
 */
public class AvaCorpAvilanRequesterImpl implements AvaCorpAvilanRequester {

    @Override
    public ArrayOfVeiculo getVeiculosAtivos(@NotNull String cpf,
                                            @NotNull String dataNascimento) throws Exception {
        final VeiculosAtivos request = getCadastroSoap(cpf, dataNascimento).buscarVeiculosAtivos(cpf);
        if (request != null && request.isSucesso()) {
            return request.getListaVeiculos();
        }

        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public ArrayOfQuestionarioVeiculos getSelecaoModeloChecklistPlacaVeiculo(@NotNull String cpf,
                                                                             @NotNull String dataNascimento) throws Exception {
        final BuscaQuestionarioColaborador request = getChecklistSoap(cpf, dataNascimento).buscarQuestionariosColaborador(cpf);
        if (request != null && request.isSucesso()) {
            return request.getQuestionarioVeiculos();
        }

        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public boolean insertChecklist(@NotNull RespostasAvaliacao respostasAvaliacao,
                                   @NotNull String cpf,
                                   @NotNull String dataNascimento) throws Exception {
        return getChecklistSoap(cpf, dataNascimento).enviarChecklist(respostasAvaliacao).isSucesso();
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
                = getChecklistSoap(cpf, dataNascimento).buscarPerguntasAlternativasQuestionario(adicionarChecklist);
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
    public boolean insertAfericao(@NotNull IncluirMedida2 medidas,
                                  @NotNull String cpf,
                                  @NotNull String dataNascimento) throws Exception {
        return getAfericaoSoap(cpf, dataNascimento).incluirMedida(medidas).isSucesso();
    }

    @Override
    public ArrayOfPneu getPneusVeiculo(@NotNull String placaVeiculo,
                                       @NotNull String cpf,
                                       @NotNull String dataNascimento) throws Exception {
        final PneusVeiculo request = getCadastroSoap(cpf, dataNascimento).buscarPneusVeiculo(placaVeiculo);
        if (request != null && request.isSucesso()) {
            return request.getPneus();
        }

        throw new Exception(request != null ? request.getMensagem() : "");
    }

    private CadastroAvaCorpAvilanSoap getCadastroSoap(@NotNull String cpf,
                                                      @NotNull String dataNascimento) {
        final CadastroAvaCorpAvilanService service = new CadastroAvaCorpAvilanService();
        final CadastroAvaCorpAvilanSoap soap = service.getCadastroSoap();
        HeaderUtils.bindHeadersToService(
                (BindingProvider) soap,
                HeaderEntry.createAuthorizationBasic(cpf.concat(":").concat(dataNascimento)));
        return soap;
    }

    private AfericaoAvaCorpAvilanSoap getAfericaoSoap(@NotNull String cpf,
                                                      @NotNull String dataNascimento) {
        final AfericaoAvaCorpAvilanService service = new AfericaoAvaCorpAvilanService();
        final AfericaoAvaCorpAvilanSoap soap = service.getAfericaoSoap();
        HeaderUtils.bindHeadersToService(
                (BindingProvider) soap,
                HeaderEntry.createAuthorizationBasic(cpf.concat(":").concat(dataNascimento)));
        return soap;
    }

    private ChecklistAvaCorpAvilanSoap getChecklistSoap(@NotNull String cpf,
                                                        @NotNull String dataNascimento) {
        final ChecklistAvaCorpAvilanService service = new ChecklistAvaCorpAvilanService();
        final ChecklistAvaCorpAvilanSoap soap = service.getChecklistSoap();
        HeaderUtils.bindHeadersToService(
                (BindingProvider) soap,
                HeaderEntry.createAuthorizationBasic(cpf.concat(":").concat(dataNascimento)));
        return service.getChecklistSoap();
    }
}