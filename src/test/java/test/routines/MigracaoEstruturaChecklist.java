package test.routines;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;

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

        final OffsetDateTime agora = Now.offsetDateTimeUtc();
        long versaoAtual = 0;
        final List<MigrationModeloCheck> todosModelos = getTodosModelos(conn);
        for (final MigrationModeloCheck modelo : todosModelos) {
            final List<MigrationCheck> checklists = getTodosChecksDoModelo(conn, modelo.getCodigo());
            MigrationCheck checklistAnterior = null;
            for (final MigrationCheck checklist : checklists) {
                if (checklistAnterior == null) {
                    versaoAtual = criaVersaoModelo(conn, modelo.getCodigo(), agora);
                    setarVersaoNoChecklistRealizado(conn, checklist.getCodigo(), versaoAtual);
                } else {
                    if (perguntasOuAlternativasMudaram(conn, checklistAnterior, checklist)) {
                        versaoAtual = criaVersaoModelo(conn, modelo.getCodigo(), agora);
                        setarVersaoNoChecklistRealizado(conn, checklist.getCodigo(), versaoAtual);
                    } else {
                        setarVersaoNoChecklistRealizado(conn, checklist.getCodigo(), versaoAtual);
                    }
                }
                checklistAnterior = checklist;
            }
            versaoAtual = 0;
        }
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

    private void setarVersaoNoChecklistRealizado(@NotNull final Connection conn,
                                                 @NotNull final Long codChecklist,
                                                 final long versaoModelo) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_DATA " +
                    "SET COD_VERSAO_CHECKLIST_MODELO = ? WHERE CODIGO = ?;");
            stmt.setLong(1, codChecklist);
            stmt.setLong(2, versaoModelo);
            if (stmt.executeUpdate() != 1) {
                throw new IllegalStateException("Erro ao setar versão " + versaoModelo + " no checklist " + codChecklist);
            }
        } finally {
            DatabaseConnection.close(stmt);
        }
    }

    private long criaVersaoModelo(@NotNull final Connection conn,
                                  @NotNull final Long codModelo,
                                  @NotNull final OffsetDateTime dataHoraAtual) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select func_checklist_cria_versao_modelo(" +
                    "f_cod_modelo      := ?, " +
                    "f_data_hora_atual := ?) as novo_cod_versao_modelo;");
            stmt.setLong(1, codModelo);
            stmt.setObject(2, dataHoraAtual);
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

    private boolean perguntasOuAlternativasMudaram(@NotNull final Connection conn,
                                                   @NotNull final MigrationCheck anterior,
                                                   @NotNull final MigrationCheck atual) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("with antigo as ( " +
                    "    SELECT cr.cod_alternativa " +
                    "    FROM checklist_respostas CR " +
                    "    WHERE cr.cod_checklist = ? " +
                    "    order by cr.cod_alternativa " +
                    "), " +
                    "     novo as ( " +
                    "         SELECT cr.cod_alternativa " +
                    "    FROM checklist_respostas CR " +
                    "    WHERE cr.cod_checklist = ? " +
                    "    order by cr.cod_alternativa " +
                    "     ) " +
                    " " +
                    " " +
                    "select " +
                    "       coalesce(a.cod_alternativa = n.cod_alternativa, false) as iguais " +
                    "from antigo a left join novo n on n.cod_alternativa = a .cod_alternativa " +
                    "group by iguais;");
            stmt.setLong(1, anterior.getCodigo());
            stmt.setLong(2, atual.getCodigo());
            rSet = stmt.executeQuery();
            // Se fore IGUAIS vai retornar apenas uma linha com valor TRUE.
            // Se forem PARCIALMENTE IGUAIS vai retornar duas linhas. Uma TRUE e outra FALSE.
            // Se forem TOTALMENTE DIFERENTES vai retornar uma linha com valor FALSE.
            if (rSet.next()) {
                //noinspection RedundantIfStatement
                if (rSet.isLast() && rSet.getBoolean(1)) {
                    // São iguais.
                    return false;
                } else {
                    // Parcialmente iguais ou diferentes, então mudaram.
                    return true;
                }
            } else {
                throw new IllegalStateException("Erro ao buscar todos os modelos de check");
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
                    "WHERE CD.COD_CHECKLIST_MODELO = ? " +
                    "ORDER BY CD.DATA_HORA ASC;");
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