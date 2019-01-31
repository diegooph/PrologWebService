package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso.ColaboradorEmDescanso;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso.ViagemEmDescanso;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 31/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AcompanhamentoViagemDaoImpl extends DatabaseConnection implements AcompanhamentoViagemDao {

    @NotNull
    @Override
    public ViagemEmDescanso getColaboradoresEmDescanso(@NotNull final Long codUnidade,
                                                       @NotNull final List<Long> codCargos) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MARCACAO_GET_COLABORADORES_JORNADA_FINALIZADA(?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codCargos));
            rSet = stmt.executeQuery();
            final List<ColaboradorEmDescanso> colaboradores = new ArrayList<>();
            while (rSet.next()) {
                colaboradores.add(new ColaboradorEmDescanso(
                        rSet.getString("NOME_COLABORADOR"),
                        rSet.getObject("DATA_HORA_INICIO_ULTIMA_VIAGEM", LocalDateTime.class),
                        rSet.getObject("DATA_HORA_FIM_ULTIMA_VIAGEM", LocalDateTime.class),
                        Duration.ofSeconds(rSet.getLong("TEMPO_DESCANSO_SEGUNDOS"))));
            }
            return new ViagemEmDescanso(colaboradores, colaboradores.size());
        } finally {
            close(conn, stmt, rSet);
        }
    }
}