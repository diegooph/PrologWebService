package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.requester;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvaCorpAvilanException;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvacorpAvilanTipoChecklist;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao.service.AfericaoAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao.service.AfericaoAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.service.CadastroAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.service.CadastroAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.service.ChecklistAvaCorpAvilanService;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.service.ChecklistAvaCorpAvilanSoap;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.header.HeaderEntry;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.header.HeaderUtils;
import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.ws.BindingProvider;
import java.util.List;

/**
 * Created by luiz on 24/07/17.
 */
public class AvaCorpAvilanRequesterImpl implements AvaCorpAvilanRequester {

    @NotNull
    private static final String TODOS_COLABORADORES = "";

    @Override
    public ArrayOfVeiculo getVeiculosAtivos(@NotNull final String cpf,
                                            @NotNull final String dataNascimento) throws Exception {
        try {
            final VeiculosAtivos request = getCadastroSoap(cpf, dataNascimento).buscarVeiculosAtivos(cpf);

            if (success(request)) {
                return request.getListaVeiculos();
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException("[INTEGRAÇÃO - AVILAN] Erro ao buscar os veículos ativos", t);
        }
    }

    @Override
    public Veiculo getVeiculoAtivo(@NotNull final String placaVeiculo,
                                   @NotNull final String cpf,
                                   @NotNull final String dataNascimento) throws Exception {
        try {
            final VeiculosAtivos request = getCadastroSoap(cpf, dataNascimento).buscarVeiculoAtivo(cpf, placaVeiculo);

            if (success(request)) {
                final ArrayOfVeiculo veiculos = request.getListaVeiculos();
                if (veiculos.getVeiculo().size() != 1) {
                    throw new IllegalStateException("Busca de um veículo retornou mais de um resultado para a placa: "
                            + placaVeiculo);
                }
                return veiculos.getVeiculo().get(0);
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException(
                    "[INTEGRAÇÃO - AVILAN] Erro ao buscar veículo com placa: " + placaVeiculo, t);
        }
    }

    @Override
    public ArrayOfTipoVeiculo getTiposVeiculo(@NotNull final String cpf,
                                              @NotNull final String dataNascimento) throws Exception {
        try {
            final TiposVeiculo request = getCadastroSoap(cpf, dataNascimento).buscarTiposVeiculo();

            if (success(request)) {
                return request.getTiposVeiculo();
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException("[INTEGRAÇÃO - AVILAN] Erro ao buscar tipos de veículo", t);
        }
    }

    @Override
    public ArrayOfString getPlacasVeiculoByTipo(@NotNull final String codTipoVeiculo,
                                                @NotNull final String cpf,
                                                @NotNull final String dataNascimento) throws Exception {
        try {
            final VeiculoTipo request = getCadastroSoap(cpf, dataNascimento).buscarVeiculosTipo(codTipoVeiculo);

            if (success(request)) {
                return request.getVeiculos();
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException("[INTEGRAÇÃO - AVILAN] Erro ao buscar placas de um tipo de veículo", t);
        }
    }

    @Override
    public ArrayOfQuestionarioVeiculos getSelecaoModeloChecklistPlacaVeiculo(
            @NotNull final String cpf,
            @NotNull final String dataNascimento) throws Exception {
        try {
            final BuscaQuestionarioColaborador request =
                    getChecklistSoap(cpf, dataNascimento).buscarQuestionariosColaborador(cpf);

            if (success(request)) {
                return request.getQuestionarioVeiculos();
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException("[INTEGRAÇÃO - AVILAN] Erro ao buscar modelos de checklist", t);
        }
    }

    @Override
    public Long insertChecklist(@NotNull final RespostasAvaliacao respostasAvaliacao,
                                @NotNull final String cpf,
                                @NotNull final String dataNascimento) throws Exception {
        try {
            final EnviaRespostaAvaliacao request =
                    getChecklistSoap(cpf, dataNascimento).enviarChecklist(respostasAvaliacao);

            if (success(request)) {
                return (long) respostasAvaliacao.getCodigoAvaliacao();
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException("[INTEGRAÇÃO - AVILAN] Erro ao inserir checklist", t);

        }
    }

    @Override
    public ArrayOfVeiculoQuestao getQuestoesVeiculo(final int codigoQuestionario,
                                                    @NotNull final String placaVeiculo,
                                                    @NotNull final AvacorpAvilanTipoChecklist tipoChecklist,
                                                    @NotNull final String cpf,
                                                    @NotNull final String dataNascimento) throws Exception {
        try {
            final AdicionarChecklist adicionarChecklist = new AdicionarChecklist();
            adicionarChecklist.setCpf(cpf);
            adicionarChecklist.setDtNascimento(dataNascimento);
            adicionarChecklist.setVeiculo(placaVeiculo);
            adicionarChecklist.setCodigoQuestionario(codigoQuestionario);
            adicionarChecklist.setTipoChecklist(tipoChecklist);

            final PerguntasAlternativasQuestionario request =
                    getChecklistSoap(cpf, dataNascimento).buscarPerguntasAlternativasQuestionario(adicionarChecklist);
            if (success(request)) {
                // Esse request retorna uma lista de VeiculoQuestao pois, dado um veículo ABC,
                // caso queiramos buscar seu questionário, ele pode estar atrelado a carretas DIK e XYZ, por exemplo.
                // Desse modo, as questões vêm separadas por veículo.
                return request.getVeiculoQuestoes();
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException(
                    "[INTEGRAÇÃO - AVILAN] Erro ao buscar modelo de checklist para a placa: " + placaVeiculo, t);
        }
    }

    @Override
    public Long insertAfericao(@NotNull final IncluirMedida2 medidas,
                               @NotNull final String cpf,
                               @NotNull final String dataNascimento) throws Exception {
        try {
            final IncluirRegistroVeiculo request = getAfericaoSoap(cpf, dataNascimento).incluirMedida(medidas);

            if (success(request)) {
                return (long) request.getSequenciaRegistro();
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException("[INTEGRAÇÃO - AVILAN] Erro ao inserir aferição", t);
        }
    }

    @Override
    public AfericaoFiltro getAfericaoByCodigo(final int codigoAfericao,
                                              @NotNull final String cpf,
                                              @NotNull final String dataNascimento) throws Exception {
        try {
            final AfericoesFiltro request = getAfericaoSoap(cpf, dataNascimento)
                    .buscarAfericoesFiltroEspecifico(codigoAfericao);

            if (success(request)) {
                final List<AfericaoFiltro> afericoesFiltro = request.getAfericoes().getAfericaoFiltro();
                if (afericoesFiltro.size() != 1) {
                    throw new IllegalStateException(
                            "Busca de uma aferição retornou mais de um resultado para o código: " + codigoAfericao);
                }

                return afericoesFiltro.get(0);
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException("[INTEGRAÇÃO - AVILAN] Erro ao buscar aferição", t);
        }
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
        try {
            final AfericoesFiltro request = getAfericaoSoap(cpf, dataNascimento).buscarAfericoesFiltro(
                    codFilialAvilan,
                    codUnidadeAvilan,
                    dataInicial,
                    dataFinal,
                    placaVeiculo,
                    codTipoVeiculo,
                    limit,
                    offset);

            if (success(request)) {
                return request.getAfericoes();
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException("[INTEGRAÇÃO - AVILAN] Erro ao buscar aferições", t);
        }
    }

    @Override
    public ArrayOfPneu getPneusVeiculo(@NotNull final String placaVeiculo,
                                       @NotNull final String cpf,
                                       @NotNull final String dataNascimento) throws Exception {
        try {
            final PneusVeiculo request = getCadastroSoap(cpf, dataNascimento).buscarPneusVeiculo(placaVeiculo);

            if (success(request)) {
                return request.getPneus();
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException("[INTEGRAÇÃO - AVILAN] Erro ao buscar pneus da placa: " + placaVeiculo, t);
        }
    }

    @Override
    public ChecklistFiltro getChecklistByCodigo(final int codigoAvaliacao,
                                                @NotNull final String cpf,
                                                @NotNull final String dataNascimento) throws Exception {

        try {
            final ChecklistsFiltro request =
                    getChecklistSoap(cpf, dataNascimento).buscarAvaliacaoFiltro(codigoAvaliacao);

            if (success(request)) {
                final List<ChecklistFiltro> checklists = request.getChecklists().getChecklistFiltro();
                if (checklists.size() != 1) {
                    throw new IllegalStateException(
                            "Busca de um checklist retornou mais de um resultado para o código: " + codigoAvaliacao);
                }

                return checklists.get(0);
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException("[INTEGRAÇÃO - AVILAN] Erro ao buscar um checklist", t);
        }
    }

    @Override
    public ArrayOfChecklistFiltro getChecklistsByColaborador(
            final int codFilialAvilan,
            final int codUnidadeAvilan,
            @NotNull final String codTipoVeiculo,
            @NotNull final String placaVeiculo,
            @NotNull final String dataInicial,
            @NotNull final String dataFinal,
            @NotNull final String cpf,
            @NotNull final String dataNascimento) throws Exception {
        return internalGetChecklists(
                codFilialAvilan,
                codUnidadeAvilan,
                codTipoVeiculo,
                placaVeiculo,
                dataInicial,
                dataFinal,
                cpf,
                dataNascimento,
                false);
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
        return internalGetChecklists(
                codFilialAvilan,
                codUnidadeAvilan,
                codTipoVeiculo,
                placaVeiculo,
                dataInicial,
                dataFinal,
                cpf,
                dataNascimento,
                true);
    }

    @NotNull
    private ArrayOfChecklistFiltro internalGetChecklists(
            final int codFilialAvilan,
            final int codUnidadeAvilan,
            @NotNull final String codTipoVeiculo,
            @NotNull final String placaVeiculo,
            @NotNull final String dataInicial,
            @NotNull final String dataFinal,
            @NotNull final String cpf,
            @NotNull final String dataNascimento,
            final boolean buscarTodosChecklists) throws Exception {
        try {
            final ChecklistsFiltro request = getChecklistSoap(cpf, dataNascimento).buscarChecklistFiltro(
                    codFilialAvilan,
                    codUnidadeAvilan,
                    dataInicial,
                    dataFinal,
                    placaVeiculo,
                    buscarTodosChecklists ? TODOS_COLABORADORES : cpf,
                    codTipoVeiculo);

            if (success(request)) {
                return request.getChecklists();
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException("[INTEGRAÇÃO - AVILAN] Erro ao buscar checklists", t);
        }
    }

    @Override
    public ArrayOfFarolDia getFarolChecklist(final int codFilialAvilan,
                                             final int codUnidadeAvilan,
                                             @NotNull final String dataInicial,
                                             @NotNull final String dataFinal,
                                             final boolean itensCriticosRetroativos,
                                             @NotNull final String cpf,
                                             @NotNull final String dataNascimento) throws Exception {
        try {
            final FarolChecklist2 request = getChecklistSoap(cpf, dataNascimento).farolChecklist(
                    codFilialAvilan,
                    codUnidadeAvilan,
                    dataInicial,
                    dataFinal,
                    itensCriticosRetroativos);

            if (success(request)) {
                return request.getFarolDia();
            }

            throw new Exception(Strings.isNullOrEmpty(request.getMensagem()) ? "SEM MENSAGEM" : request.getMensagem());
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException("[INTEGRAÇÃO - AVILAN] Erro ao buscar farol do checklist", t);
        }
    }

    private boolean success(@Nullable final AvacorpAvilanRequestStatus requestStatus) {
        // Se a busca tiver sido feita COM sucesso, mas não tem dados, então sucesso false e mensagem igual a null ou
        // vazio.
        // Se a busca tiver sido feita SEM sucesso, então sucesso false e mensagem diferente de null ou vazio.
        return requestStatus != null
                && (requestStatus.isSucesso()
                || (!requestStatus.isSucesso() && StringUtils.isNullOrEmpty(requestStatus.getMensagem())));
    }

    @Deprecated
    private boolean error(final boolean sucesso,
                          @Nullable final String mensagem) {
        // Se a busca tiver sido feita COM sucesso, mas não tem dados, então sucesso false e mensagem igual a null ou
        // vazio.
        // Se a busca tiver sido feita SEM sucesso, então sucesso false e mensagem diferente de null ou vazio.

        return !sucesso && !Strings.isNullOrEmpty(mensagem);
    }

    private CadastroAvaCorpAvilanSoap getCadastroSoap(
            @NotNull final String cpf,
            @NotNull final String dataNascimento) {
        final CadastroAvaCorpAvilanService service = new CadastroAvaCorpAvilanService();
        final CadastroAvaCorpAvilanSoap soap = service.getCadastroSoap();
        HeaderUtils.bindHeadersToService(
                (BindingProvider) soap,
                HeaderEntry.createAuthorizationBasic(cpf.concat(":").concat(dataNascimento)));
        return soap;
    }

    private AfericaoAvaCorpAvilanSoap getAfericaoSoap(
            @NotNull final String cpf,
            @NotNull final String dataNascimento) {
        final AfericaoAvaCorpAvilanService service = new AfericaoAvaCorpAvilanService();
        final AfericaoAvaCorpAvilanSoap soap = service.getAfericaoSoap();
        HeaderUtils.bindHeadersToService(
                (BindingProvider) soap,
                HeaderEntry.createAuthorizationBasic(cpf.concat(":").concat(dataNascimento)));
        return soap;
    }

    private ChecklistAvaCorpAvilanSoap getChecklistSoap(
            @NotNull final String cpf,
            @NotNull final String dataNascimento) {
        final ChecklistAvaCorpAvilanService service = new ChecklistAvaCorpAvilanService();
        final ChecklistAvaCorpAvilanSoap soap = service.getChecklistSoap();
        HeaderUtils.bindHeadersToService(
                (BindingProvider) soap,
                HeaderEntry.createAuthorizationBasic(cpf.concat(":").concat(dataNascimento)));
        return soap;
    }

}