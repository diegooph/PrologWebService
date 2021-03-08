package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.database.StatementUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 12/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SistemaProtheusNepomucenoDaoImpl extends DatabaseConnection implements SistemaProtheusNepomucenoDao {
    @NotNull
    @Override
    public List<Long> getApenasUnidadesMapeadas(@NotNull final Connection conn,
                                                @NotNull final List<Long> codUnidades) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT U.CODIGO AS COD_UNIDADE " +
                    "FROM UNIDADE U " +
                    "WHERE U.COD_AUXILIAR IS NOT NULL " +
                    "AND U.CODIGO = ANY (?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            final List<Long> codUnidadesMapeadas = new ArrayList<>();
            if (rSet.next()) {
                do {
                    codUnidadesMapeadas.add(rSet.getLong("COD_UNIDADE"));
                } while (rSet.next());
            }
            return codUnidadesMapeadas;
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Map<Long, String> getCodFiliais(@NotNull final Connection conn,
                                           @NotNull final List<Long> codUnidades) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * " +
                    "from integracao.func_pneu_afericao_get_cod_auxiliar_unidade_prolog(f_cod_unidades => ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Map<Long, String> codUnidadePrologCodAuxiliar = new HashMap<>();
                do {
                    codUnidadePrologCodAuxiliar.put(
                            rSet.getLong("COD_UNIDADE_PROLOG"),
                            rSet.getString("COD_AUXILIAR"));
                } while (rSet.next());
                return codUnidadePrologCodAuxiliar;
            } else {
                throw new SQLException("Nenhum código de filial mapeado para as unidades:\n" +
                        "codUnidades: " + codUnidades.toString());
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<String> verificaCodAuxiliarTipoVeiculoValido(@Nullable final Long codEmpresaTipoVeiculo,
                                                             @Nullable final Long codTipoVeiculo) throws Throwable {
        Preconditions.checkArgument(
                codEmpresaTipoVeiculo != null || codTipoVeiculo != null,
                "codEmpresaTipoVeiculo e codTipoVeiculo não pode ser nulos ao mesmo tempo!");
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select vt.cod_auxiliar as cod_auxiliar " +
                    "from veiculo_tipo vt " +
                    "where vt.cod_auxiliar is not null " +
                    "and f_if(? is null, true, vt.cod_empresa = ?) " +
                    "and f_if(? is null, true, vt.codigo != ?);");
            StatementUtils.bindValueOrNull(stmt, 1, codEmpresaTipoVeiculo, SqlType.BIGINT);
            StatementUtils.bindValueOrNull(stmt, 2, codEmpresaTipoVeiculo, SqlType.BIGINT);
            StatementUtils.bindValueOrNull(stmt, 3, codTipoVeiculo, SqlType.BIGINT);
            StatementUtils.bindValueOrNull(stmt, 4, codTipoVeiculo, SqlType.BIGINT);
            rSet = stmt.executeQuery();
            final List<String> codigosAuxiliares = new ArrayList<>();
            while (rSet.next()) {
                codigosAuxiliares.add(rSet.getString("cod_auxiliar"));
            }
            return codigosAuxiliares;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}