package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.requester;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvacorpAvilanTipoChecklist;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao.AfericaoFiltro;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao.ArrayOfAfericaoFiltro;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.*;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import org.jetbrains.annotations.NotNull;

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

    Long insertAfericao(@NotNull final IncluirMedida2 medidas,
                        @NotNull final String cpf,
                        @NotNull final String dataNascimento) throws Exception;

    AfericaoFiltro getAfericaoByCodigo(final int codigoAfericao,
                                       @NotNull final String cpf,
                                       @NotNull final String dataNascimento) throws Exception;

    ArrayOfAfericaoFiltro getAfericoes(final int codFilialAvilan,
                                       final int codUnidadeAvilan,
                                       @NotNull final String codTipoVeiculo,
                                       @NotNull final String placaVeiculo,
                                       @NotNull final String dataInicial,
                                       @NotNull final String dataFinal,
                                       final int limit,
                                       final int offset,
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
                                      final boolean itensCriticosRetroativos,
                                      @NotNull final String cpf,
                                      @NotNull final String dataNascimento) throws Exception;
}