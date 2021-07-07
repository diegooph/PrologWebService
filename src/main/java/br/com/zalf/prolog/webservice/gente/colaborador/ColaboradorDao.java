package br.com.zalf.prolog.webservice.gente.colaborador;

import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.colaborador.model.ColaboradorEdicao;
import br.com.zalf.prolog.webservice.gente.colaborador.model.ColaboradorInsercao;
import br.com.zalf.prolog.webservice.gente.colaborador.model.ColaboradorListagem;
import br.com.zalf.prolog.webservice.gente.controlejornada.DadosIntervaloChangedListener;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para manipular os usuários no banco de dados.
 */
public interface ColaboradorDao {

    void insert(@NotNull final ColaboradorInsercao colaborador,
                @NotNull final DadosIntervaloChangedListener intervaloListener,
                @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
                @NotNull final String userToken) throws Throwable;

    void update(@NotNull final ColaboradorEdicao colaborador,
                @NotNull final DadosIntervaloChangedListener intervaloListener,
                @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
                @NotNull final String userToken) throws Throwable;

    void updateStatus(@NotNull final Long cpf,
                      @NotNull final Colaborador colaborador,
                      @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    void delete(@NotNull final Long cpf,
                @NotNull final DadosIntervaloChangedListener intervaloListener,
                @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    Colaborador getByCpf(Long cpf, boolean apenasAtivos) throws SQLException;

    @NotNull
    List<Colaborador> getAllByUnidade(@NotNull final Long codUnidade, final boolean apenasAtivos) throws Throwable;

    @NotNull
    List<ColaboradorListagem> getAllByUnidades(@NotNull List<Long> codUnidades,
                                               final boolean apenasAtivos) throws Throwable;

    @NotNull
    List<Colaborador> getAllByEmpresa(@NotNull final Long codEmpresa, final boolean apenasAtivos) throws Throwable;

    List<Colaborador> getMotoristasAndAjudantes(Long codUnidade) throws SQLException;

    boolean verifyIfCpfExists(Long cpf, Long codUnidade) throws SQLException;

    @NotNull
    Colaborador getByToken(@NotNull final String token) throws SQLException;

    @NotNull
    List<Colaborador> getColaboradoresComAcessoFuncaoByUnidade(@NotNull final Long codUnidade,
                                                               final int codFuncaoProLog) throws SQLException;

    Long getCodUnidadeByCpf(@NotNull final Long cpf) throws SQLException;

    boolean colaboradorTemAcessoFuncao(@NotNull final Long cpf, final int codPilar, final int codFuncaoProLog)
            throws SQLException;

    @NotNull
    Long getCodColaboradorByCpfAndCodEmpresa(@NotNull final Connection conn,
                                             @NotNull final Long codEmpresa,
                                             @NotNull final String cpfColaborador) throws Throwable;

    @NotNull
    Long getCodColaboradorByCpfAndCodEmpresa(@NotNull final Long codEmpresa,
                                             @NotNull final String cpfColaborador) throws Throwable;

    @NotNull
    Long getCodColaboradorByCpfAndCodColaboradorBase(@NotNull final Long codColaboradorBase,
                                                     @NotNull final String cpf) throws Throwable;
}
