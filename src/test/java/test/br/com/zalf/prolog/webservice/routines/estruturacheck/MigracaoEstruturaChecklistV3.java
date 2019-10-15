package test.routines.estruturacheck;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A migração de toda a estrutura do checklist será dividida em 4 partes:
 * 1 -> [BD] Iremos migrar a estrutura de modelos de checklist:
 *      • Criar tabela de versionamento de modelos
 *      • Adicionar versão atual na tabela de MODELO_DATA
 *      • Adicionar versão na tabela de CHECKLIST_DATA
 *      • Adicionar versão na tabela de CHECKLIST_PERGUNTAS_DATA
 *      • Adicionar versão na tabela de CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
 *
 * 2 -> [JAVA] Iremos criar versões dos modelos de checklist nos baseando na CHECKLIST_RESPOSTAS
 *
 * 3 -> [BD] Iremos migrar o restante da estrutura do checklist e a COSI:
 *      • Criar tabela para salvar apenas as respostas NOK
 *      • Migrar dados da CHECKLIST_RESPOSTAS para a CHECKLIST_RESPOSTAS_NOK
 *      • Criar novas colunas de código contexto e preencher na COSI
 *
 * 4 -> [BD] Iremos criar/recriar functions/views necessárias:
 *      • Tudo que precisou ser refatorado por conta de mudanças na estrutura, será alterado aqui
 *
 * Created on 2019-09-04
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class MigracaoEstruturaChecklistV3 {
    @NotNull
    private static final String TAG = MigracaoEstruturaChecklistV3.class.getSimpleName();

    @Test
    public void executaMigracaoCheckist() throws Throwable {
        Connection conn = null;
        try {
            DatabaseManager.init();
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            log("************************ Iniciando da execução da migração ************************");
            log("************************ PASSO 1 - INÍCIO ************************");
            executaPasso1(conn);
            log("************************ PASSO 1 - FIM ************************");
            log("************************ PASSO 2 - INÍCIO ************************");
            executaPasso2(conn);
            log("************************ PASSO 2 - FIM ************************");
            log("************************ PASSO 3 - INÍCIO ************************");
            executaPasso3(conn);
            log("************************ PASSO 3 - FIM ************************");
            log("************************ PASSO 4 - INÍCIO ************************");
            executaPasso4(conn);
            log("************************ PASSO 4 - FIM ************************");
            log("************************ PASSO 5 - INÍCIO ************************");
            executaPasso5(conn);
            log("************************ PASSO 5 - FIM ************************");
            log("************************ PASSO 6 - INÍCIO ************************");
            executaPasso6(conn);
            log("************************ PASSO 6 - FIM ************************");
            log("************************ PASSO 7 - INÍCIO ************************");
            executaPasso7(conn);
            log("************************ PASSO 7 - FIM ************************");
            conn.commit();
            log("************************ Fim da execução da migração ************************");
        } catch (final Throwable t) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (final Throwable ignored) {}
            }
            throw t;
        } finally {
            DatabaseConnection.close(conn);
            DatabaseManager.finish();
        }
    }

    /**
     * 1 -> [BD] Iremos migrar a estrutura de modelos de checklist:
     *      • Criar tabela de versionamento de modelos
     *      • Adicionar versão atual na tabela de MODELO_DATA
     *      • Adicionar versão na tabela de CHECKLIST_DATA
     *      • Adicionar versão na tabela de CHECKLIST_PERGUNTAS_DATA
     *      • Adicionar versão na tabela de CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
     */
    private void executaPasso1(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall("{CALL MIGRATION_CHECKLIST.FUNC_MIGRATION_1_CRIA_ESTRUTURA_VERSIONAMENTO_MODELO()}");
            if (stmt.executeUpdate() < 0) {
                throw new IllegalStateException("Erro ao executar passo 1");
            }
        } finally {
            DatabaseConnection.close(stmt);
        }
    }

    /**
     * 2 -> [JAVA] Iremos criar versões dos modelos de checklist nos baseando na CHECKLIST_RESPOSTAS
     */
    private void executaPasso2(@NotNull final Connection conn) throws Throwable {
        final OffsetDateTime agora = Now.offsetDateTimeUtc();
        long versaoAtual = 0;
        long totalUltimasVersoes = 0;
        final List<MigrationModeloCheck> todosModelos = getTodosModelos(conn);
        log("Modelos de checklist buscados");
        for (int i = 0; i < todosModelos.size(); i++) {
            final MigrationModeloCheck modelo = todosModelos.get(i);
            log(String.format("-> Iterando no modelo (%d / %d): %s", i + 1, todosModelos.size(), modelo.getNome()));

            final List<MigrationVersaoModelo> versoes = getTodasVersoesDoModelo(conn, modelo.getCodigo());
            for (int j = 0; j < versoes.size(); j++) {
                final MigrationVersaoModelo versao = versoes.get(j);
                versaoAtual = criaVersaoModelo(
                        conn,
                        versao.getCodModelo(),
                        versao.getCodChecklistPrimeiraRealizacaoModelo(),
                        agora);

                log(String.format("--> Versão criada (%d / %d) do modelo (%d / %d) -- TOTAL VERSÕES: %d",
                        j + 1,
                        versoes.size(),
                        i + 1,
                        todosModelos.size(),
                        versaoAtual));
            }

            criaUltimaVersaoModelo(conn, modelo.getCodigo(), agora);
            totalUltimasVersoes++;
            log("Criou última versão modelo: " + versaoAtual);
            log("*** Ultimas versoes: " + totalUltimasVersoes);
        }
    }

    /**
     * 3 -> [BD] Iremos migrar o restante da estrutura do checklist e a COSI:
     *      • Criar tabela para salvar apenas as respostas NOK
     *      • Migrar dados da CHECKLIST_RESPOSTAS para a CHECKLIST_RESPOSTAS_NOK
     *      • Criar novas colunas de código contexto e preencher na COSI
     */
    private void executaPasso3(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        try {
            log("Migrando respostas para nova tabela de NOK");
            stmt = conn.prepareCall("{CALL MIGRATION_CHECKLIST.FUNC_MIGRATION_2_MIGRA_ESTRUTURA_RESPOSTAS_NOK()}");
            if (stmt.executeUpdate() < 0) {
                throw new IllegalStateException("Erro ao executar passo 3 - Respostas NOK");
            }

            log("Migrando dados da COSI");
            stmt = conn.prepareCall("{CALL MIGRATION_CHECKLIST.FUNC_MIGRATION_3_MIGRA_ESTRUTURA_COSI()}");
            if (stmt.executeUpdate() < 0) {
                throw new IllegalStateException("Erro ao executar passo 3 - COSI");
            }
        } finally {
            DatabaseConnection.close(stmt);
        }
    }

    /**
     * 4 -> [BD] Iremos criar/recriar functions/views necessárias:
     *      • Tudo que precisou ser refatorado por conta de mudanças na estrutura, será alterado aqui
     */
    private void executaPasso4(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        try {
            log("Ativando as constraints nas tabelas de versão e dependências");
            stmt = conn.prepareCall("{CALL MIGRATION_CHECKLIST.FUNC_MIGRATION_4_ADICIONA_CONSTRAINTS_VERSAO_MODELO()}");
            if (stmt.executeUpdate() < 0) {
                throw new IllegalStateException("Erro ao executar passo 4 -- ativar constraints");
            }
        } finally {
            DatabaseConnection.close(stmt);
        }
    }

    private void executaPasso5(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        try {
            log("Corrigindo modelos");
            stmt = conn.prepareCall("{CALL MIGRATION_CHECKLIST.FUNC_MIGRATION_5_CORRIGE_MODELO()}");
            if (stmt.executeUpdate() < 0) {
                throw new IllegalStateException("Erro ao executar passo 4");
            }
        } finally {
            DatabaseConnection.close(stmt);
        }
    }

    /**
     * 6 -> [BD] Iremos criar/recriar functions/views necessárias:
     *      • Tudo que precisou ser refatorado por conta de mudanças na estrutura, será alterado aqui
     */
    private void executaPasso6(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        try {
            log("Migrando functions e views");
            stmt = conn.prepareCall("{CALL MIGRATION_CHECKLIST.FUNC_MIGRATION_6_MIGRA_FUNCTIONS_VIEWS()}");
            if (stmt.executeUpdate() < 0) {
                throw new IllegalStateException("Erro ao executar passo 6");
            }
        } finally {
            DatabaseConnection.close(stmt);
        }
    }

    private void executaPasso7(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        try {
            log("Migrando mudanças na Dao de modelos");
            stmt = conn.prepareCall("{CALL MIGRATION_CHECKLIST.FUNC_MIGRATION_7_MUDANCAS_DAO_MODELOS_CHECK()}");
            if (stmt.executeUpdate() < 0) {
                throw new IllegalStateException("Erro ao executar passo 7");
            }
        } finally {
            DatabaseConnection.close(stmt);
        }
    }

    private long criaUltimaVersaoModelo(@NotNull final Connection conn,
                                        @NotNull final Long codModelo,
                                        @NotNull final OffsetDateTime dataHoraAtual) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareCall("select * from MIGRATION_CHECKLIST.FUNC_CHECKLIST_CRIA_MODELO_ULTIMA_VERSAO(" +
                    "F_COD_MODELO      := ?, " +
                    "F_DATA_HORA_ATUAL := ?) as cod_versao");
            stmt.setLong(1, codModelo);
            stmt.setObject(2, dataHoraAtual);
            rSet = stmt.executeQuery();
            final long codVersaoModelo;
            if (rSet.next() && (codVersaoModelo = rSet.getLong(1)) > 0) {
                return codVersaoModelo;
            } else {
                throw new IllegalStateException("Erro ao criar última versão do modelo: " + codModelo);
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    private long criaVersaoModelo(@NotNull final Connection conn,
                                  @NotNull final Long codModelo,
                                  @NotNull final Long codPrimeiroChecklistVersaoModelo,
                                  @NotNull final OffsetDateTime dataHoraAtual) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * from migration_checklist.func_checklist_cria_versao_modelo(" +
                    "f_cod_modelo                       := ?, " +
                    "f_cod_primeiro_check_versao_modelo := ?, " +
                    "f_data_hora_atual                  := ?);");
            stmt.setLong(1, codModelo);
            stmt.setLong(2, codPrimeiroChecklistVersaoModelo);
            stmt.setObject(3, dataHoraAtual);
            rSet = stmt.executeQuery();
            final long codVersaoModelo;
            if (rSet.next() && (codVersaoModelo = rSet.getLong(1)) > 0) {
                return codVersaoModelo;
            } else {
                throw new IllegalStateException("Erro ao criar nova versão do modelo: " + codModelo);
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    @NotNull
    private List<MigrationModeloCheck> getTodosModelos(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT " +
                    "CM.CODIGO, " +
                    "CM.NOME, " +
                    "CM.COD_UNIDADE FROM CHECKLIST_MODELO_DATA CM order by codigo;");
            rSet = stmt.executeQuery();
            final List<MigrationModeloCheck> modelos = new ArrayList<>();
            while (rSet.next()) {
                    modelos.add(new MigrationModeloCheck(
                            rSet.getLong(1),
                            rSet.getString(2),
                            rSet.getLong(3)));
            }
            return modelos;
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    @NotNull
    private List<MigrationVersaoModelo> getTodasVersoesDoModelo(@NotNull final Connection conn,
                                                                @NotNull final Long codModeloChecklist) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT " +
                    "MCM.COD_CHECKLIST_MODELO, " +
                    "MCM.COD_CHECKLIST_PRIMEIRA_REALIZACAO_MODELO " +
                    "FROM MIGRATION_CHECKLIST.MIGRATION_CHECKLIST_MODELOS MCM " +
                    "WHERE MCM.COD_CHECKLIST_MODELO = ? " +
                    "ORDER BY MCM.ORDEM_VERSAO ASC;");
            stmt.setLong(1, codModeloChecklist);
            rSet = stmt.executeQuery();
            final List<MigrationVersaoModelo> checks = new ArrayList<>();
            while (rSet.next()) {
                checks.add(new MigrationVersaoModelo(
                        rSet.getLong(1),
                        rSet.getLong(2)));
            }
            return checks;
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    private void log(@NotNull final String message) {
        Log.d(TAG, message);
    }

    private static class MigrationVersaoModelo {
        @NotNull
        private final Long codModelo;
        @NotNull
        private final Long codChecklistPrimeiraRealizacaoModelo;

        private MigrationVersaoModelo(@NotNull final Long codModelo,
                                      @NotNull final Long codChecklistPrimeiraRealizacaoModelo) {
            this.codModelo = codModelo;
            this.codChecklistPrimeiraRealizacaoModelo = codChecklistPrimeiraRealizacaoModelo;
        }

        @NotNull
        public Long getCodModelo() {
            return codModelo;
        }

        @NotNull
        public Long getCodChecklistPrimeiraRealizacaoModelo() {
            return codChecklistPrimeiraRealizacaoModelo;
        }
    }

    private static class MigrationModeloCheck {
        @NotNull
        private final Long codigo;
        @NotNull
        private final String nome;
        @NotNull
        private final Long codUnidade;

        private MigrationModeloCheck(@NotNull final Long codigo,
                                     @NotNull final String nome,
                                     @NotNull final Long codUnidade) {
            this.codigo = codigo;
            this.nome = nome;
            this.codUnidade = codUnidade;
        }

        @NotNull
        public Long getCodigo() {
            return codigo;
        }

        @NotNull
        public String getNome() {
            return nome;
        }

        @NotNull
        public Long getCodUnidade() {
            return codUnidade;
        }
    }
}
