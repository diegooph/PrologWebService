package test.routines;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtil;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * Essa classe move veículos e seus pneus de uma unidade para outra.
 * Também será alterado qualquer vínculo que exista na PNEU_VALOR_VIDA, nas tabelas de movimentação e
 * nas tabelas de aferição.
 * <p>
 * Created on 10/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class MigrateVeiculosUnidade extends DatabaseConnection {

    public void run(@NotNull final Map<Long, Long> codigosTiposVeiculos,
                    @NotNull final List<String> todosVeiculos,
                    @NotNull final List<String> todosPneus,
                    @NotNull final Long codUnidadeAtualVeiculosPneus,
                    @NotNull final Long novoCodUnidadeVeiculosPneus) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            //////////////////////////////////////////////////////////////////////////////////////////
            stmt = conn.prepareStatement("ALTER TABLE movimentacao DROP CONSTRAINT " +
                    "fk_movimentacao_movimentacao_procecsso;");
            stmt.execute();
            stmt = conn.prepareStatement("ALTER TABLE movimentacao DROP CONSTRAINT fk_movimentacao_pneu;");
            stmt.execute();
            // Migra as movimentações
            migrateMovimentacoes(
                    todosPneus,
                    codUnidadeAtualVeiculosPneus,
                    novoCodUnidadeVeiculosPneus,
                    conn);
            // Fim da migração das movimentações
            //////////////////////////////////////////////////////////////////////////////////////////


            //////////////////////////////////////////////////////////////////////////////////////////
            stmt = conn.prepareStatement("ALTER TABLE afericao_manutencao DROP CONSTRAINT fk_afericao_manutencao_pneu;");
            stmt.execute();
            stmt = conn.prepareStatement("ALTER TABLE afericao_valores DROP CONSTRAINT fk_afericao_valores_pneu;");
            stmt.execute();
            stmt = conn.prepareStatement("ALTER TABLE afericao_manutencao DROP CONSTRAINT fk_afericao_manutencao_pneu_inserido;");
            stmt.execute();
            // Migra as aferições
            migrateAfericoes(
                    todosVeiculos,
                    codUnidadeAtualVeiculosPneus,
                    novoCodUnidadeVeiculosPneus,
                    conn);
            // Fim da migração das aferições
            //////////////////////////////////////////////////////////////////////////////////////////

            //////////////////////////////////////////////////////////////////////////////////////////
            // Migra a associação entre veículos - pneus
            stmt = conn.prepareStatement("ALTER TABLE veiculo_pneu DROP CONSTRAINT fk_veiculo_pneu_pneu;");
            stmt.execute();
            stmt = conn.prepareStatement("UPDATE VEICULO_PNEU SET COD_UNIDADE = ? " +
                    "WHERE COD_PNEU::TEXT LIKE ANY (ARRAY[?]) AND COD_UNIDADE = ? ;");
            stmt.setLong(1, novoCodUnidadeVeiculosPneus);
            stmt.setArray(2, PostgresUtil.ListToArray(conn, todosPneus));
            stmt.setLong(3, codUnidadeAtualVeiculosPneus);
            if (stmt.executeUpdate() == 0) {
                throw new IllegalStateException("Erro ao atualizar o código da unidade na VEICULO_PNEU");
            }
            // Fim da migração da associação entre veículos - pneus
            //////////////////////////////////////////////////////////////////////////////////////////

            //////////////////////////////////////////////////////////////////////////////////////////
            // Migra o pneu_valor_vida
            stmt = conn.prepareStatement("ALTER TABLE pneu_valor_vida DROP CONSTRAINT fk_pneu_valor_vida_pneu;");
            stmt.execute();
            stmt = conn.prepareStatement("UPDATE PNEU_VALOR_VIDA SET COD_UNIDADE = ? " +
                    "WHERE COD_PNEU::TEXT LIKE ANY (ARRAY[?]);");
            stmt.setLong(1, novoCodUnidadeVeiculosPneus);
            stmt.setArray(2, PostgresUtil.ListToArray(conn, todosPneus));
            if (stmt.executeUpdate() == 0) {
                throw new IllegalStateException("Erro ao atualizar o código da unidade na PNEU_VALOR_VIDA");
            }
            // Fim da migração do pneu_valor_vida
            //////////////////////////////////////////////////////////////////////////////////////////


            //////////////////////////////////////////////////////////////////////////////////////////
            // Migra os pneus para a nova unidade
            stmt = conn.prepareStatement("UPDATE PNEU P SET COD_UNIDADE = ? " +
                    "WHERE CODIGO::TEXT LIKE ANY (ARRAY[?]) AND COD_UNIDADE = ?;");
            System.out.println("Pneus que serão atualizados: " + todosPneus);
            stmt.setLong(1, novoCodUnidadeVeiculosPneus);
            stmt.setArray(2, PostgresUtil.ListToArray(conn, todosPneus));
            stmt.setLong(3, codUnidadeAtualVeiculosPneus);
            System.out.println("query: " + stmt.toString());
            if (stmt.executeUpdate() != todosPneus.size()) {
                throw new IllegalStateException("Erro ao atualizar a unidade e modelo do pneu");
            }
            // Fim da migração de pneus para a nova unidade
            //////////////////////////////////////////////////////////////////////////////////////////

            //////////////////////////////////////////////////////////////////////////////////////////
            // Migra os veículos para a nova unidade
            for (String placa : todosVeiculos) {
                stmt = conn.prepareStatement("SELECT V.COD_TIPO FROM VEICULO V WHERE V.PLACA = ?;");
                stmt.setString(1, placa);
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    final Long codTipoAtual = rSet.getLong("COD_TIPO");
                    stmt = conn.prepareStatement("UPDATE VEICULO SET COD_UNIDADE = ?, COD_TIPO = ? " +
                            "WHERE PLACA = ?;");
                    stmt.setLong(1, novoCodUnidadeVeiculosPneus);
                    stmt.setLong(2, codigosTiposVeiculos.get(codTipoAtual));
                    stmt.setString(3, placa);
                    if (stmt.executeUpdate() == 0) {
                        throw new IllegalStateException("Erro ao atualizar a unidade e modelo do veículo");
                    }
                } else {
                    throw new IllegalStateException("Erro ao buscar o código de tipo do veículo");
                }
            }
            // Fim da migração de veículos para a nova unidade
            //////////////////////////////////////////////////////////////////////////////////////////

            //////////////////////////////////////////////////////////////////////////////////////////
            // Cria todas as constraints que foram removidas
            stmt = conn.prepareStatement("ALTER TABLE movimentacao " +
                    "  ADD CONSTRAINT fk_movimentacao_movimentacao_procecsso " +
                    "FOREIGN KEY (cod_movimentacao_processo, cod_unidade) " +
                    "REFERENCES movimentacao_processo(codigo, cod_unidade);");
            stmt.execute();

            stmt = conn.prepareStatement("ALTER TABLE movimentacao " +
                    "            ADD CONSTRAINT fk_movimentacao_pneu " +
                    "            FOREIGN KEY (cod_pneu, cod_unidade) " +
                    "            REFERENCES pneu(codigo, cod_unidade);");
            stmt.execute();

            stmt = conn.prepareStatement("ALTER TABLE afericao_manutencao\n" +
                    "            ADD CONSTRAINT fk_afericao_manutencao_pneu\n" +
                    "            FOREIGN KEY (cod_pneu, cod_unidade)\n" +
                    "            REFERENCES pneu(codigo, cod_unidade);");
            stmt.execute();

            stmt = conn.prepareStatement("ALTER TABLE afericao_valores\n" +
                    "            ADD CONSTRAINT fk_afericao_valores_pneu\n" +
                    "            FOREIGN KEY (cod_pneu, cod_unidade)\n" +
                    "            REFERENCES pneu(codigo, cod_unidade);");
            stmt.execute();

            stmt = conn.prepareStatement("ALTER TABLE afericao_manutencao\n" +
                    "            ADD CONSTRAINT fk_afericao_manutencao_pneu_inserido\n" +
                    "            FOREIGN KEY (cod_pneu_inserido, cod_unidade)\n" +
                    "            REFERENCES pneu(codigo, cod_unidade);");
            stmt.execute();

            stmt = conn.prepareStatement("ALTER TABLE veiculo_pneu " +
                    "   ADD CONSTRAINT fk_veiculo_pneu_pneu " +
                    "   FOREIGN KEY (cod_pneu, cod_unidade) " +
                    "   REFERENCES pneu(codigo, cod_unidade);");
            stmt.execute();

            stmt = conn.prepareStatement("ALTER TABLE pneu_valor_vida " +
                    "  ADD CONSTRAINT fk_pneu_valor_vida_pneu " +
                    "FOREIGN KEY (cod_pneu, cod_unidade) " +
                    "REFERENCES pneu (codigo, cod_unidade);");
            stmt.execute();
            // Fim da criação das constraints
            //////////////////////////////////////////////////////////////////////////////////////////


            conn.commit();
        } catch (final Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            closeConnection(conn, stmt, rSet);
            throw e;
        }
    }

    private void migrateMovimentacoes(@NotNull final List<String> todosPneus,
                                      @NotNull final Long codUnidadeAtualVeiculosPneus,
                                      @NotNull final Long novoCodUnidadeVeiculosPneus,
                                      @NotNull final Connection conn) throws Exception {
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM MOVIMENTACAO M WHERE " +
                "M.COD_PNEU::TEXT LIKE ANY (ARRAY[?]) AND M.COD_UNIDADE = ?");
        statement.setArray(1, PostgresUtil.ListToArray(conn, todosPneus));
        statement.setLong(2, codUnidadeAtualVeiculosPneus);
        final ResultSet rSet = statement.executeQuery();

        while (rSet.next()) {
            final Long codMovimentacao = rSet.getLong("CODIGO");
            final Long codProcessoMovimentacao = rSet.getLong("COD_MOVIMENTACAO_PROCESSO");

            System.out.println("Código mov: " + codMovimentacao + " - Processo: " + codProcessoMovimentacao);

            statement = conn.prepareStatement("UPDATE MOVIMENTACAO SET COD_UNIDADE = ? WHERE CODIGO = ?;");
            statement.setLong(1, novoCodUnidadeVeiculosPneus);
            statement.setLong(2, codMovimentacao);
            if (statement.executeUpdate() == 0) {
                throw new IllegalStateException("Erro ao atualizar o código da unidade da movimentação");
            }

            statement = conn.prepareStatement("UPDATE MOVIMENTACAO_PROCESSO SET COD_UNIDADE = ? WHERE CODIGO " +
                    "= ?;");
            statement.setLong(1, novoCodUnidadeVeiculosPneus);
            statement.setLong(2, codProcessoMovimentacao);
            if (statement.executeUpdate() == 0) {
                throw new IllegalStateException("Erro ao atualizar o código da unidade do processo de movimentação");
            }
        }
    }


    private void migrateAfericoes(final List<String> todosVeiculos,
                                  final Long codUnidadeAtualVeiculosPneus,
                                  final Long novoCodUnidadeVeiculosPneus,
                                  final Connection conn) throws Exception {
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM AFERICAO A WHERE " +
                "A.PLACA_VEICULO::TEXT LIKE ANY (ARRAY[?]) AND A.COD_UNIDADE = ?");
        statement.setArray(1, PostgresUtil.ListToArray(conn, todosVeiculos));
        statement.setLong(2, codUnidadeAtualVeiculosPneus);
        final ResultSet rSet = statement.executeQuery();

        while (rSet.next()) {
            final Long codAfericao = rSet.getLong("CODIGO");
            statement = conn.prepareStatement("UPDATE AFERICAO SET COD_UNIDADE = ? WHERE CODIGO = ?;");
            statement.setLong(1, novoCodUnidadeVeiculosPneus);
            statement.setLong(2, codAfericao);
            if (statement.executeUpdate() == 0) {
                throw new IllegalStateException("Erro ao atualizar o código da unidade da aferição");
            }

            statement = conn.prepareStatement("UPDATE AFERICAO_VALORES SET COD_UNIDADE = ? WHERE COD_AFERICAO = ?;");
            statement.setLong(1, novoCodUnidadeVeiculosPneus);
            statement.setLong(2, codAfericao);
            if (statement.executeUpdate() == 0) {
                throw new IllegalStateException("Erro ao atualizar o código da unidade da afericao_valores");
            }

            statement = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET COD_UNIDADE = ? WHERE COD_AFERICAO = ?;");
            statement.setLong(1, novoCodUnidadeVeiculosPneus);
            statement.setLong(2, codAfericao);
            if (statement.executeUpdate() == 0) {
                throw new IllegalStateException("Erro ao atualizar o código da unidade da afericao_manutencao");
            }
        }
    }
}