package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaCadastro;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaItemCadastro;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaItemVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 10/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuNomenclaturaDaoImpl implements PneuNomenclaturaDao {
    private static final int EXECUTE_BATCH_SUCCESS = 0;

    @Override
    public void insertOrUpdateNomenclatura(@NotNull final PneuNomenclaturaCadastro pneuNomenclaturaCadastro,
                                           @NotNull final String userToken) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Antes de qualquer coisa, garantimos que todas as posições do diagrama tiverem uma nomenclatura definida.
            final int[] posicoesNaoEstepes = pneuNomenclaturaCadastro.getPosicoesNaoEstepes();
            garanteNomenclaturaCompleta(conn, pneuNomenclaturaCadastro.getCodDiagrama(), posicoesNaoEstepes);

            // Antes de inserir, deleta a nomenclatura cadastrada dos estepes.
            // Fazemos isso pois a nomenclatura para estepes pode ser removida no Sistema Web.
            deletaNomenclaturaEstepes(conn,
                                      pneuNomenclaturaCadastro.getCodEmpresa(),
                                      pneuNomenclaturaCadastro.getCodDiagrama());

            stmt = conn.prepareCall("{CALL FUNC_PNEU_NOMENCLATURA_INSERE_EDITA_NOMENCLATURA(" +
                    "F_COD_EMPRESA                := ?, " +
                    "F_COD_DIAGRAMA               := ?, " +
                    "F_POSICAO_PROLOG             := ?, " +
                    "F_NOMENCLATURA               := ?," +
                    "F_TOKEN_RESPONSAVEL_INSERCAO := ?," +
                    "F_DATA_HORA_CADASTRO         := ?)}");
            final List<PneuNomenclaturaItemCadastro> nomenclaturas = pneuNomenclaturaCadastro.getNomenclaturas();
            for (final PneuNomenclaturaItemCadastro nomenclaturaItem : nomenclaturas) {
                stmt.setLong(1, pneuNomenclaturaCadastro.getCodEmpresa());
                stmt.setLong(2, pneuNomenclaturaCadastro.getCodDiagrama());
                stmt.setLong(3, nomenclaturaItem.getPosicaoProLog());
                stmt.setString(4, StringUtils.trimToNull(nomenclaturaItem.getNomenclatura()));
                stmt.setString(5, userToken);
                stmt.setObject(6, Now.offsetDateTimeUtc());
                stmt.addBatch();
            }
            final int[] batchResult = stmt.executeBatch();
            final boolean tudoOk = IntStream
                    .of(batchResult)
                    .allMatch(result -> result == EXECUTE_BATCH_SUCCESS);
            if (!tudoOk || batchResult.length != nomenclaturas.size()) {
                throw new IllegalStateException("Erro ao cadastrar as nomenclaturas da empresa: "
                        + pneuNomenclaturaCadastro.getCodEmpresa());
            }
            conn.commit();
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    @Override
    public List<PneuNomenclaturaItemVisualizacao> getPneuNomenclaturaItemVisualizacao(
            @NotNull final Long codEmpresa,
            @NotNull final Long codDiagrama) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_NOMENCLATURA_GET_NOMENCLATURA(" +
                    "F_COD_EMPRESA  := ?," +
                    "F_COD_DIAGRAMA := ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codDiagrama);
            rSet = stmt.executeQuery();
            final List<PneuNomenclaturaItemVisualizacao> nomenclaturas = new ArrayList<>();
            while (rSet.next()) {
                nomenclaturas.add(PneuNomenclaturaConverter.createNomenclaturaItemVisualizacao(rSet));
            }
            return nomenclaturas;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void garanteNomenclaturaCompleta(@NotNull final Connection conn,
                                             @NotNull final Long codDiagrama,
                                             final int[] posicoesProLog) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall("{CALL FUNC_GARANTE_PNEU_NOMENCLATURA_COMPLETA(" +
                    "F_COD_DIAGRAMA    := ?," +
                    "F_POSICOES_PROLOG := ?)}");
            stmt.setLong(1, codDiagrama);
            stmt.setObject(2, posicoesProLog);
            stmt.execute();
        } finally {
            close(stmt);
        }
    }

    private void deletaNomenclaturaEstepes(@NotNull final Connection conn,
                                           @NotNull final Long codEmpresa,
                                           @NotNull final Long codDiagrama) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall("{CALL FUNC_PNEU_NOMENCLATURA_DELETA_ESTEPES(" +
                    "F_COD_EMPRESA  := ?," +
                    "F_COD_DIAGRAMA := ?)}");
            stmt.setLong(1, codEmpresa);
            stmt.setObject(2, codDiagrama);
            stmt.execute();
        } finally {
            close(stmt);
        }
    }
}
