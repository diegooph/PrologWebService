package br.com.zalf.prolog.webservice.integracao.api.afericao;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.api.afericao._model.ApiPneuMedicaoRealizada;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
final class ApiAfericaoDaoImpl extends DatabaseConnection implements ApiAfericaoDao {
    @NotNull
    @Override
    public List<ApiPneuMedicaoRealizada> getAfericoesRealizadas(
            @NotNull final String tokenIntegracao,
            @Nullable final Long codigoProcessoAfericao,
            @Nullable final LocalDateTime dataHoraUltimaAtualizacaoUtc) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            if (codigoProcessoAfericao != null) {
                stmt = conn.prepareStatement("select * from " +
                        "integracao.func_pneu_afericao_busca_afericoes_realizadas_by_codigo(?, ?);");
                stmt.setString(1, tokenIntegracao);
                stmt.setLong(2, codigoProcessoAfericao);
            } else {
                stmt = conn.prepareStatement("select * from " +
                        "integracao.func_pneu_afericao_busca_afericoes_realizadas_by_data_hora(?, ?);");
                stmt.setString(1, tokenIntegracao);
                stmt.setObject(2, dataHoraUltimaAtualizacaoUtc);
            }
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<ApiPneuMedicaoRealizada> afericoesRealizadas = new ArrayList<>();
                do {
                    afericoesRealizadas.add(ApiAfericaoConverter.createAfericaoRealizada(rSet));
                } while (rSet.next());
                return afericoesRealizadas;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
