package br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste.model.ProcedimentoTesteAferidor;
import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste.model.TesteAferidorExecutado;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

/**
 * Created on 2019-10-07
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TesteAferidorDaoImpl extends DatabaseConnection implements TesteAferidorDao {

    @NotNull
    @Override
    public ProcedimentoTesteAferidor getProcedimentoTeste() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM AFERIDOR.FUNC_AFERIDOR_GET_PROCEDIMENTO_TESTE();");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final String[] array = (String[]) rSet.getArray(1).getArray();
                return new ProcedimentoTesteAferidor(Arrays.asList(array));
            } else {
                throw new IllegalStateException("Nenhum procedimento de teste do aferidor encontrado");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long insereTeste(final @NotNull TesteAferidorExecutado teste) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM AFERIDOR.FUNC_AFERIDOR_INSERE_TESTE(" +
                    "?," +
                    "?," +
                    "?) RETURNING CODIGO;");
            stmt.setLong(1, teste.getCodColaboradorExecucao());
            stmt.setString(2, teste.getDispositivo());
            final String json = GsonUtils.getGson().toJson(teste.getComandosExecutados());
            stmt.setObject(3, PostgresUtils.toJsonb(json));
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.getLong("CODIGO") != 0) {
                return rSet.getLong("CODIGO");
            } else {
                throw new IllegalStateException("Erro ao salvar testes executados do aferidor");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}