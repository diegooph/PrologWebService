package br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema;

import br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema._model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface OpcaoProblemaDao {

    @NotNull
    List<OpcaoProblemaAberturaSocorro> getOpcoesProblemasDisponiveisAberturaSocorroByEmpresa(
            @NotNull final Long codEmpresa) throws Throwable;

    @NotNull
    List<OpcaoProblemaSocorroRotaListagem> getOpcoesProblemasSocorroRotaByEmpresa(@NotNull final Long codEmpresa)
            throws Throwable;

    @NotNull
    OpcaoProblemaSocorroRotaVisualizacao getOpcaoProblemaSocorroRotaVisualizacao(@NotNull final Long codOpcaoProblema)
            throws Throwable;

    @NotNull
    Long insertOpcoesProblemas(
            @NotNull final OpcaoProblemaSocorroRotaCadastro opcaoProblemaSocorroRotaCadastro) throws Throwable;

    void updateOpcoesProblemas(
            @NotNull final OpcaoProblemaSocorroRotaEdicao opcaoProblemaSocorroRotaEdicao) throws Throwable;

    void updateStatusAtivo(@NotNull final OpcaoProblemaSocorroRotaStatus opcaoProblemaSocorroRotaStatus)
            throws Throwable;
}