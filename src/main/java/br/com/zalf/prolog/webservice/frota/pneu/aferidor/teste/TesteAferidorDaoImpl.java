package br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste._model.ProcedimentoTesteAferidor;
import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste._model.TesteAferidorExecutado;
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
                final String[] comandos = (String[]) rSet.getArray(1).getArray();
                return new ProcedimentoTesteAferidor(Arrays.asList(comandos));
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
                    "F_COD_COLABORADOR_EXECUCAO :=?," +
                    "F_DATA_HORA_EXECUCAO       :=?," +
                    "F_NOME_DISPOSITIVO         :=?," +
                    "F_COMANDOS_EXECUTADOS      :=?);");
            stmt.setLong(1, teste.getCodColaboradorExecucao());
            stmt.setObject(2, Now.getOffsetDateTimeUtc());
            stmt.setString(3, teste.getNomeDispositivo());
            final String json = GsonUtils.getGson().toJson(teste.getComandosExecutados());
            stmt.setObject(4, PostgresUtils.toJsonb(json));
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.getLong(1) != 0) {
                return rSet.getLong(1);
            } else {
                throw new IllegalStateException("Erro ao salvar testes executados do aferidor");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}