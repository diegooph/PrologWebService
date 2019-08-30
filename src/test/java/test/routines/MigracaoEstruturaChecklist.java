package test.routines;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
 *      • Criar novas colunas de código fixo e preencher na COSI
 *
 * 4 -> [BD] Iremos criar/recriar functions/views necessárias:
 *      • Tudo que precisou ser refatorado por conta de mudanças na estrutura, será alterado aqui
 *
 * Created on 2019-08-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class MigracaoEstruturaChecklist {

    public void executaMigracaoCheckist() throws Throwable {
        Connection conn = null;
        try {
            DatabaseManager.init();
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            executaPasso1(conn);
            executaPasso2(conn);
            executaPasso3(conn);
            executaPasso4(conn);
            conn.commit();
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
            stmt = conn.prepareStatement("SELECT FUNC_CRIA_ESTRUTURA_VERSIONAMENTO_MODELO();");
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
        // VAR VERSAO_ATUAL = 0;
        // Busca todos os modelos de checklists existentes
        // FOR cada modelo .. modelos existentes
        //     Busca checklists realizados modelo ordenados cronologicamente
        //     FOR cada checklist .. checklists realizados
        //         SE primeira iteração
        //              Cria versão 1;
        //              VERSAO_ATUAL = 1;
        //         END SE;
        //         SE checklist.alternativasOuPerguntasMudaram comparado a check anterior
        //         ENTÃO
        //             Criar nova versão para esse modelo
        //             VERSAO_ATUAL++;
        //             Seta VERSAO_ATUAL no checklist realizado e dependências
        //         SENÃO
        //             Seta VERSAO_ATUAL no checklist realizado e dependências
        //         END SE;
        //     END FOR;
        //     VERSAO_ATUAL = 0;
        // END FOR;
    }

    /**
     * 3 -> [BD] Iremos migrar o restante da estrutura do checklist e a COSI:
     *      • Criar tabela para salvar apenas as respostas NOK
     *      • Migrar dados da CHECKLIST_RESPOSTAS para a CHECKLIST_RESPOSTAS_NOK
     *      • Criar novas colunas de código fixo e preencher na COSI
     */
    private void executaPasso3(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT FUNC_MIGRA_DADOS_CHECKLIST_RESPOSTAS();");
            if (stmt.executeUpdate() < 0) {
                throw new IllegalStateException("Erro ao executar passo 3");
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
            stmt = conn.prepareStatement("SELECT FUNC_MIGRA_FUNCTIONS_VIEWS();");
            if (stmt.executeUpdate() < 0) {
                throw new IllegalStateException("Erro ao executar passo 4");
            }
        } finally {
            DatabaseConnection.close(stmt);
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
                    "CM.COD_UNIDADE FROM CHECKLIST_MODELO_DATA CM;");
            rSet = stmt.executeQuery();
            final List<MigrationModeloCheck> modelos = new ArrayList<>();
            if (rSet.next()) {
                do {
                    modelos.add(new MigrationModeloCheck(
                            rSet.getLong(1),
                            rSet.getString(2),
                            rSet.getLong(3)));
                } while (rSet.next());
            } else {
                throw new IllegalStateException("Erro ao buscar todos os modelos de check");
            }
            return modelos;
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    @NotNull
    private List<MigrationCheck> getTodosChecksDoModelo(@NotNull final Connection conn,
                                                        @NotNull final Long codModeloChecklist) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT " +
                    "CD.CODIGO, " +
                    "CD.COD_UNIDADE FROM CHECKLIST_DATA CD " +
                    "WHERE CD.COD_CHECKLIST_MODELO = ?;");
            stmt.setLong(1, codModeloChecklist);
            rSet = stmt.executeQuery();
            final List<MigrationCheck> checks = new ArrayList<>();
            if (rSet.next()) {
                do {
                    checks.add(new MigrationCheck(
                            rSet.getLong(1),
                            rSet.getLong(2)));
                } while (rSet.next());
            } else {
                throw new IllegalStateException("Erro ao buscar todos os checklists do modelo: " + codModeloChecklist);
            }
            return checks;
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    private static class MigrationCheck {
        @NotNull
        private final Long codigo;
        @NotNull
        private final Long codUnidade;

        private MigrationCheck(@NotNull final Long codigo,
                               @NotNull final Long codUnidade) {
            this.codigo = codigo;
            this.codUnidade = codUnidade;
        }

        @NotNull
        public Long getCodigo() {
            return codigo;
        }

        @NotNull
        public Long getCodUnidade() {
            return codUnidade;
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