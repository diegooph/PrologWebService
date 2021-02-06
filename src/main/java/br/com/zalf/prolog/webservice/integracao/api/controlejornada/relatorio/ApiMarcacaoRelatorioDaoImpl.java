package br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio._model.ApiMarcacaoRelatorio1510;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

/**
 * Created on 11/5/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMarcacaoRelatorioDaoImpl extends DatabaseConnection implements ApiMarcacaoRelatorioDao {
    @NotNull
    @Override
    public List<ApiMarcacaoRelatorio1510> getRelatorioPortaria1510(
            @NotNull final String tokenIntegracao,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal,
            @Nullable final Long codUnidadeProLog,
            @Nullable final Long codTipoMarcacao,
            @Nullable final String cpfColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM INTEGRACAO.FUNC_MARCACAO_RELATORIO_INTERVALO_PORTARIA_1510_TIPO_3(" +
                    "F_TOKEN_INTEGRACAO := ?, " +
                    "F_DATA_INICIAL := ?, " +
                    "F_DATA_FINAL := ?, " +
                    "F_COD_UNIDADE := ?, " +
                    "F_COD_TIPO_INTERVALO := ?, " +
                    "F_CPF_COLABORADOR := ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            bindValueOrNull(stmt, 4, codUnidadeProLog, SqlType.BIGINT);
            bindValueOrNull(stmt, 5, codTipoMarcacao, SqlType.BIGINT);
            bindValueOrNull(
                    stmt,
                    6,
                    cpfColaborador == null ? null : Colaborador.formatCpf(cpfColaborador),
                    SqlType.BIGINT);
            rSet = stmt.executeQuery();
            final List<ApiMarcacaoRelatorio1510> marcacoes = new ArrayList<>();
            while (rSet.next()) {
                marcacoes.add(ApiMarcacaoRelatorioCreator.createApiMarcacaoRelatorio1510(rSet));
            }
            return marcacoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
