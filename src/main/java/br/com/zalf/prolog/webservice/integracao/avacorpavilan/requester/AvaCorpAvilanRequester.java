package br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvacorpAvilanTipoChecklist;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 24/07/17.
 */
public interface AvaCorpAvilanRequester extends Requester {
    ArrayOfVeiculo getVeiculosAtivos(@NotNull final String cpf,
                                     @NotNull final String dataNascimento) throws Exception;

    Veiculo getVeiculoAtivo(@NotNull final String placaVeiculo,
                            @NotNull final String cpf,
                            @NotNull final String dataNascimento) throws Exception;

    ArrayOfTipoVeiculo getTiposVeiculo(@NotNull final String cpf,
                                       @NotNull final String dataNascimento) throws Exception;

    ArrayOfString getPlacasVeiculoByTipo(@NotNull final String codTipoVeiculo,
                                         @NotNull final String cpf,
                                         @NotNull final String dataNascimento) throws Exception;

    ArrayOfQuestionarioVeiculos getSelecaoModeloChecklistPlacaVeiculo(@NotNull final String cpf,
                                                                      @NotNull final String dataNascimento) throws Exception;

    Long insertChecklist(@NotNull final RespostasAvaliacao respostasAvaliacao,
                         @NotNull final String cpf,
                         @NotNull final String dataNascimento) throws Exception;

    ArrayOfVeiculoQuestao getQuestoesVeiculo(final int codigoQuestionario,
                                             @NotNull final String placaVeiculo,
                                             @NotNull final AvacorpAvilanTipoChecklist tipoChecklist,
                                             @NotNull final String cpf,
                                             @NotNull final String dataNascimento) throws Exception;

    boolean insertAfericao(@NotNull final IncluirMedida2 medidas,
                           @NotNull final String cpf,
                           @NotNull final String dataNascimento) throws Exception;

    Object getAfericoes(final int codFilialAvilan,
                        final int codUnidadeAvilan,
                        @NotNull final String codTipoVeiculo,
                        @NotNull final String placaVeiculo,
                        @NotNull final String dataInicial,
                        @NotNull final String dataFinal,
                        @NotNull final String cpf,
                        @NotNull final String dataNascimento) throws Exception;

    ArrayOfPneu getPneusVeiculo(@NotNull final String placaVeiculo,
                                @NotNull final String cpf,
                                @NotNull final String dataNascimento) throws Exception;

    ChecklistFiltro getChecklistByCodigo(final int codigoAvaliacao,
                                         @NotNull final String cpf,
                                         @NotNull final String dataNascimento) throws Exception;

    ArrayOfChecklistFiltro getChecklistsByColaborador(final int codFilialAvilan,
                                                      final int codUnidadeAvilan,
                                                      @NotNull final String codTipoVeiculo,
                                                      @NotNull final String placaVeiculo,
                                                      @NotNull final String dataInicial,
                                                      @NotNull final String dataFinal,
                                                      @NotNull final String cpf,
                                                      @NotNull final String dataNascimento) throws Exception;

    ArrayOfChecklistFiltro getChecklists(final int codFilialAvilan,
                                         final int codUnidadeAvilan,
                                         @NotNull final String codTipoVeiculo,
                                         @NotNull final String placaVeiculo,
                                         @NotNull final String dataInicial,
                                         @NotNull final String dataFinal,
                                         @NotNull final String cpf,
                                         @NotNull final String dataNascimento) throws Exception;

    ArrayOfFarolDia getFarolChecklist(final int codFilialAvilan,
                                      final int codUnidadeAvilan,
                                      @NotNull final String dataInicial,
                                      @NotNull final String dataFinal,
                                      @NotNull final boolean itensCriticosRetroativos,
                                      @NotNull final String cpf,
                                      @NotNull final String dataNascimento) throws Exception;
}