package br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfQuestionarioVeiculos;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfVeiculoQuestao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.RespostasAvaliacao;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 24/07/17.
 */
public interface AvaCorpAvilanRequester extends Requester {
    ArrayOfVeiculo getVeiculosAtivos(@NotNull final String cpf,
                                     @NotNull String dataNascimento) throws Exception;

    ArrayOfQuestionarioVeiculos getSelecaoModeloChecklistPlacaVeiculo(@NotNull final String cpf,
                                                                      @NotNull String dataNascimento) throws Exception;

    boolean insertChecklist(@NotNull final RespostasAvaliacao respostasAvaliacao,
                            @NotNull String cpf,
                            @NotNull String dataNascimento) throws Exception;

    ArrayOfVeiculoQuestao getQuestoesVeiculo(int codigoQuestionario,
                                             @NotNull String placaVeiculo,
                                             @NotNull String cpf,
                                             @NotNull String dataNascimento) throws Exception;

    boolean insertAfericao(@NotNull final IncluirMedida2 medidas,
                           @NotNull String cpf,
                           @NotNull String dataNascimento) throws Exception;

    ArrayOfPneu getPneusVeiculo(@NotNull final String placaVeiculo,
                                @NotNull String cpf,
                                @NotNull String dataNascimento) throws Exception;
}