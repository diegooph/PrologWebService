package br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.AfericaoAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.AfericaoAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirRegistroVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.header.HeaderEntry;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.header.HeaderUtils;
import com.google.common.base.Strings;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import javax.xml.ws.BindingProvider;
import java.util.List;


/**
 * Created by luiz on 24/07/17.
 */
public class AvaCorpAvilanRequesterImpl implements AvaCorpAvilanRequester {

    @Override
    public ArrayOfVeiculo getVeiculosAtivos(@NotNull String cpf,
                                            @NotNull String dataNascimento) throws Exception {
        final VeiculosAtivos request = getCadastroSoap(cpf, dataNascimento).buscarVeiculosAtivos(cpf);

        if (!error(request.isSucesso(), request.getMensagem())) {
            return request.getListaVeiculos();
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar os veículos ativos da Avilan para o CPF: " + cpf
                : request.getMensagem());
    }

    @Override
    public Veiculo getVeiculoAtivo(@NotNull final String placaVeiculo,
                                   @NotNull final String cpf,
                                   @NotNull final String dataNascimento) throws Exception {
        final VeiculosAtivos request = getCadastroSoap(cpf, dataNascimento).buscarVeiculoAtivo(cpf, placaVeiculo);

        if (!error(request.isSucesso(), request.getMensagem())) {
            final ArrayOfVeiculo veiculos = request.getListaVeiculos();
            // Irá retornar sempre um único veículo. Lista com tamanho 1.
            return veiculos.getVeiculo().get(0);
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar veículo ativo da Avilan com placa: " + placaVeiculo
                : request.getMensagem());
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
        final EnviaRespostaAvaliacao request = getChecklistSoap(cpf, dataNascimento).enviarChecklist(respostasAvaliacao);

        if (request != null && request.isSucesso()) {
            return true;
        }

        throw new Exception(request != null ? request.getMensagem() : "");
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

        final PerguntasAlternativasQuestionario request
                = getChecklistSoap(cpf, dataNascimento).buscarPerguntasAlternativasQuestionario(adicionarChecklist);
        if (request != null && request.isSucesso()) {
            // Esse request retorna uma lista de VeiculoQuestao pois, dado um veículo ABC,
            // caso queiramos buscar seu questionário, ele pode estar atrelado a carretas DIK e XYZ, por exemplo.
            // Desse modo, as questões vêm separadas por veículo.
            return request.getVeiculoQuestoes();
        }


        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public boolean insertAfericao(@NotNull IncluirMedida2 medidas,
                                  @NotNull String cpf,
                                  @NotNull String dataNascimento) throws Exception {
        final IncluirRegistroVeiculo request = getAfericaoSoap(cpf, dataNascimento).incluirMedida(medidas);

        if (request != null && request.isSucesso()) {
            return true;
        }

        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public ArrayOfPneu getPneusVeiculo(@NotNull String placaVeiculo,
                                       @NotNull String cpf,
                                       @NotNull String dataNascimento) throws Exception {
        final PneusVeiculo request = getCadastroSoap(cpf, dataNascimento).buscarPneusVeiculo(placaVeiculo);

        if (!error(request.isSucesso(), request.getMensagem())) {
            return request.getPneus();
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar pneus da Avilan para o veículo: " + placaVeiculo
                : request.getMensagem());
    }

    @Override
    public List<FarolDia> getFarolChecklist(@NotNull final String codUnidadeAvilan,
                                            @NotNull final String dataInicial,
                                            @NotNull final String dataFinal,
                                            @NotNull final boolean itensCriticosRetroativos,
                                            @NotNull final String cpf,
                                            @NotNull final String dataNascimento) throws Exception {
        final BuscaFarolDia request = getChecklistSoap(cpf, dataNascimento)
                .getFarol(codUnidadeAvilan, dataInicial, dataFinal, itensCriticosRetroativos);

        if (!error(request.isSucesso(), request.getMensagem())) {
            return request.getFarol();
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar o farol do checklist para a unidade: " + codUnidadeAvilan + " da Avilan"
                : request.getMensagem());
    }


    private boolean error(final boolean sucesso, @Nullable final String mensagem) {
        // Se a busca tiver sido feita COM sucesso, mas não tem dados, então sucesso false e mensagem igual a null ou
        // vazio.
        // Se a busca tiver sido feita SEM sucesso, então sucesso false e mensagem diferente de null ou vazio.

        return !sucesso && !Strings.isNullOrEmpty(mensagem);
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
        return soap;
    }
}