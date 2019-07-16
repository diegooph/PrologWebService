package br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvacorpAvilanTipoChecklist;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.service.AfericaoAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.service.AfericaoAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.service.CadastroAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.service.CadastroAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.service.ChecklistAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.service.ChecklistAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.header.HeaderEntry;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.header.HeaderUtils;
import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.ws.BindingProvider;
import java.util.List;


/**
 * Created by luiz on 24/07/17.
 */
public class AvaCorpAvilanRequesterImpl implements AvaCorpAvilanRequester {

    private static final String TODOS_COLABORADORES = "";

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
            if (veiculos.getVeiculo().size() != 1) {
                throw new IllegalStateException("Busca de um veículo retornou mais de um resultado para a placa: "
                        + placaVeiculo);
            }

            return veiculos.getVeiculo().get(0);
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar veículo ativo da Avilan com placa: " + placaVeiculo
                : request.getMensagem());
    }

    @Override
    public ArrayOfTipoVeiculo getTiposVeiculo(@NotNull final String cpf,
                                              @NotNull final String dataNascimento) throws Exception {
        final TiposVeiculo request = getCadastroSoap(cpf, dataNascimento).buscarTiposVeiculo();

        if (!error(request.isSucesso(), request.getMensagem())) {
            return request.getTiposVeiculo();
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar tipos de veículo"
                : request.getMensagem());
    }

    @Override
    public ArrayOfString getPlacasVeiculoByTipo(String codTipoVeiculo, String cpf, String dataNascimento) throws Exception {
        final VeiculoTipo request = getCadastroSoap(cpf, dataNascimento).buscarVeiculosTipo(codTipoVeiculo);

        if (!error(request.isSucesso(), request.getMensagem())) {
            return request.getVeiculos();
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar placas dos veículos para o tipo: " + codTipoVeiculo
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
    public Long insertChecklist(@NotNull RespostasAvaliacao respostasAvaliacao,
                                @NotNull String cpf,
                                @NotNull String dataNascimento) throws Exception {
        final EnviaRespostaAvaliacao request = getChecklistSoap(cpf, dataNascimento).enviarChecklist(respostasAvaliacao);

        if (request != null && request.isSucesso()) {
            return (long) respostasAvaliacao.getCodigoAvaliacao();
        }

        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public ArrayOfVeiculoQuestao getQuestoesVeiculo(int codigoQuestionario,
                                                    @NotNull String placaVeiculo,
                                                    @NotNull AvacorpAvilanTipoChecklist tipoChecklist,
                                                    @NotNull String cpf,
                                                    @NotNull String dataNascimento) throws Exception {
        final AdicionarChecklist adicionarChecklist = new AdicionarChecklist();
        adicionarChecklist.setCpf(cpf);
        adicionarChecklist.setDtNascimento(dataNascimento);
        adicionarChecklist.setVeiculo(placaVeiculo);
        adicionarChecklist.setCodigoQuestionario(codigoQuestionario);
        adicionarChecklist.setTipoChecklist(tipoChecklist);

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
    public Long insertAfericao(@NotNull IncluirMedida2 medidas,
                               @NotNull String cpf,
                               @NotNull String dataNascimento) throws Exception {
        final IncluirRegistroVeiculo request = getAfericaoSoap(cpf, dataNascimento).incluirMedida(medidas);

        if (request != null && request.isSucesso()) {
            return (long) request.getSequenciaRegistro();
        }

        throw new Exception(request != null ? request.getMensagem() : "");
    }

    @Override
    public AfericaoFiltro getAfericaoByCodigo(final int codigoAfericao,
                                              @NotNull final String cpf,
                                              @NotNull final String dataNascimento) throws Exception {
        final AfericoesFiltro request = getAfericaoSoap(cpf, dataNascimento)
                .buscarAfericoesFiltroEspecifico(codigoAfericao);

        if (!error(request.isSucesso(), request.getMensagem())) {
            final List<AfericaoFiltro> afericoesFiltro = request.getAfericoes().getAfericaoFiltro();
            if (afericoesFiltro.size() != 1) {
                throw new IllegalStateException("Busca de uma aferição retornou mais de um resultado para o código: "
                        + codigoAfericao);
            }

            return afericoesFiltro.get(0);
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar aferição com o código: " + codigoAfericao + " da Avilan"
                : request.getMensagem());
    }

    @Override
    public ArrayOfAfericaoFiltro getAfericoes(final int codFilialAvilan,
                                              final int codUnidadeAvilan,
                                              @NotNull final String codTipoVeiculo,
                                              @NotNull final String placaVeiculo,
                                              @NotNull final String dataInicial,
                                              @NotNull final String dataFinal,
                                              final int limit,
                                              final int offset,
                                              @NotNull final String cpf,
                                              @NotNull final String dataNascimento) throws Exception {

        final AfericoesFiltro request = getAfericaoSoap(cpf, dataNascimento).buscarAfericoesFiltro(
                codFilialAvilan,
                codUnidadeAvilan,
                dataInicial,
                dataFinal,
                placaVeiculo,
                codTipoVeiculo,
                limit,
                offset);

        if (!error(request.isSucesso(), request.getMensagem())) {
            return request.getAfericoes();
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar as aferições para a unidade: " + codUnidadeAvilan + " da Avilan"
                : request.getMensagem());
    }

    @Override
    public ArrayOfPneu getPneusVeiculo(@NotNull final String placaVeiculo,
                                       @NotNull final String cpf,
                                       @NotNull final String dataNascimento) throws Exception {
        final PneusVeiculo request = getCadastroSoap(cpf, dataNascimento).buscarPneusVeiculo(placaVeiculo);

        if (!error(request.isSucesso(), request.getMensagem())) {
            return request.getPneus();
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar pneus da Avilan para o veículo: " + placaVeiculo
                : request.getMensagem());
    }

    @Override
    public ChecklistFiltro getChecklistByCodigo(final int codigoAvaliacao,
                                                @NotNull final String cpf,
                                                @NotNull final String dataNascimento) throws Exception {

        final ChecklistsFiltro request = getChecklistSoap(cpf, dataNascimento).buscarAvaliacaoFiltro(codigoAvaliacao);

        if (!error(request.isSucesso(), request.getMensagem())) {
            final List<ChecklistFiltro> checklists = request.getChecklists().getChecklistFiltro();
            if (checklists.size() != 1) {
                throw new IllegalStateException("Busca de um checklist retornou mais de um resultado para o código: "
                        + codigoAvaliacao);
            }

            return checklists.get(0);
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar checklist com o código: " + codigoAvaliacao + " da Avilan"
                : request.getMensagem());
    }

    @Override
    public ArrayOfChecklistFiltro getChecklistsByColaborador(final int codFilialAvilan,
                                                             final int codUnidadeAvilan,
                                                             @NotNull final String codTipoVeiculo,
                                                             @NotNull final String placaVeiculo,
                                                             @NotNull final String dataInicial,
                                                             @NotNull final String dataFinal,
                                                             @NotNull final String cpf,
                                                             @NotNull final String dataNascimento) throws Exception {
        final ChecklistsFiltro request = getChecklistSoap(cpf, dataNascimento).buscarChecklistFiltro(
                codFilialAvilan,
                codUnidadeAvilan,
                dataInicial,
                dataFinal,
                placaVeiculo,
                cpf,
                codTipoVeiculo);

        if (!error(request.isSucesso(), request.getMensagem())) {
            return request.getChecklists();
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar os checklists para o colaborador: " + cpf + " da Avilan"
                : request.getMensagem());
    }

    @Override
    public ArrayOfChecklistFiltro getChecklists(final int codFilialAvilan,
                                                final int codUnidadeAvilan,
                                                @NotNull final String codTipoVeiculo,
                                                @NotNull final String placaVeiculo,
                                                @NotNull final String dataInicial,
                                                @NotNull final String dataFinal,
                                                @NotNull final String cpf,
                                                @NotNull final String dataNascimento) throws Exception {

        final ChecklistsFiltro request = getChecklistSoap(cpf, dataNascimento).buscarChecklistFiltro(
                codFilialAvilan,
                codUnidadeAvilan,
                dataInicial,
                dataFinal,
                placaVeiculo,
                TODOS_COLABORADORES,
                codTipoVeiculo);

        if (!error(request.isSucesso(), request.getMensagem())) {
            return request.getChecklists();
        }

        throw new Exception(Strings.isNullOrEmpty(request.getMensagem())
                ? "Erro ao buscar os checklists para a unidade: " + codUnidadeAvilan + " da Avilan"
                : request.getMensagem());
    }

    @Override
    public ArrayOfFarolDia getFarolChecklist(final int codFilialAvilan,
                                             final int codUnidadeAvilan,
                                             @NotNull final String dataInicial,
                                             @NotNull final String dataFinal,
                                             @NotNull final boolean itensCriticosRetroativos,
                                             @NotNull final String cpf,
                                             @NotNull final String dataNascimento) throws Exception {

        final FarolChecklist2 request = getChecklistSoap(cpf, dataNascimento).farolChecklist(
                codFilialAvilan,
                codUnidadeAvilan,
                dataInicial,
                dataFinal,
                itensCriticosRetroativos);

        if (!error(request.isSucesso(), request.getMensagem())) {
            return request.getFarolDia();
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