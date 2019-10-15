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
import java.time.LocalDate;
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
 * Created on 2019-08-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Deprecated
public final class MigracaoEstruturaChecklistV2 {
    @NotNull
    private static final String TAG = MigracaoEstruturaChecklistV2.class.getSimpleName();

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
//            log("************************ PASSO 3 - INÍCIO ************************");
//            executaPasso3(conn);
//            log("************************ PASSO 3 - FIM ************************");
//            log("************************ PASSO 4 - INÍCIO ************************");
//            executaPasso4(conn);
//            log("************************ PASSO 4 - FIM ************************");
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
            stmt = conn.prepareCall("{CALL FUNC_MIGRATION_1_CRIA_ESTRUTURA_VERSIONAMENTO_MODELO()}");
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
        long totalChecksProcessados = 0;
        long versaoAtual = 0;
        boolean teveTrocaVersao = false;
        final List<MigrationModeloCheck> todosModelos = getTodosModelos(conn);
        log("Modelos de checklist buscados");
        for (int i = 0; i < todosModelos.size(); i++) {
            final MigrationModeloCheck modelo = todosModelos.get(i);
            log(String.format("-> Iterando no modelo (%d / %d): %s", i + 1, todosModelos.size(), modelo.getNome()));

            final List<MigrationCheck> checklists = getTodosChecksDoModelo(conn, modelo.getCodigo());
            MigrationCheck checklistAnterior = null;
            MigrationCheck primeiroCheckModeloVersao = null;
            for (int j = 0; j < checklists.size(); j++) {
                totalChecksProcessados++;
                final MigrationCheck checklist = checklists.get(j);
                log(String.format("--> Iterando no check (%d / %d) do modelo (%d / %d) -- TOTAL VERSÕES / CHECKS: %d / %d",
                        j + 1,
                        checklists.size(),
                        i + 1,
                        todosModelos.size(),
                        versaoAtual,
                        totalChecksProcessados));

                // Verifica se é a primeira iteração.
                if (checklistAnterior == null) {
                    primeiroCheckModeloVersao = checklist;
                    versaoAtual = criaVersaoModelo(conn, modelo.getCodigo(), primeiroCheckModeloVersao.getCodigo(), agora);
                    log(String.format("---> Criando versão 1 (%d) do modelo %d e checklist %d",
                            versaoAtual,
                            modelo.getCodigo(),
                            primeiroCheckModeloVersao.getCodigo()));
                    setarVersaoNoChecklistRealizado(conn, checklist.getCodigo(), versaoAtual);
                } else {
                    if (perguntasOuAlternativasMudaram(conn, checklistAnterior, checklist)) {
                        log(String.format("Versão Criada - Anterior %d e Novo %d",
                                checklistAnterior.getCodigo(),
                                checklist.getCodigo()));


                        // Sempre que uma versão for criada, o primeiro check da versão precisa ser atualizado.
                        primeiroCheckModeloVersao = checklist;

                        teveTrocaVersao = true;
                        setarVidaUtilModeloCheck(
                                conn,
                                modelo.getCodigo(),
                                versaoAtual,
                                primeiroCheckModeloVersao.getDataRealizacao(),
                                checklist.getDataRealizacao());
                        versaoAtual = criaVersaoModelo(conn, modelo.getCodigo(), primeiroCheckModeloVersao.getCodigo(), agora);
                        log(String.format("---> Criando nova versão (%d) do modelo %d e checklist %d",
                                versaoAtual,
                                modelo.getCodigo(),
                                primeiroCheckModeloVersao.getCodigo()));

                        setarVersaoNoChecklistRealizado(conn, checklist.getCodigo(), versaoAtual);
                    } else {
                        setarVersaoNoChecklistRealizado(conn, checklist.getCodigo(), versaoAtual);
                    }
                }

                checklistAnterior = checklist;
            }

            // Se não teve troca de versão, temos que setar a vida útil do modelo aqui fora, pois significa que ele
            // teve apenas uma versão.
            if (!teveTrocaVersao && primeiroCheckModeloVersao != null) {
                teveTrocaVersao = false;
                setarVidaUtilModeloCheck(
                        conn,
                        modelo.getCodigo(),
                        versaoAtual,
                        primeiroCheckModeloVersao.getDataRealizacao(),
                        checklistAnterior.getDataRealizacao());
            }
            versaoAtual = 0;
        }

        if (true)
            throw new IllegalStateException("Aborted!");
//        log("Ativando as constraints nas tabelas de versão e dependências");
//        ativarConstraintsTabelasVersao(conn);
    }

    private void ativarConstraintsTabelasVersao(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall("{CALL FUNC_CHECKLIST_ATIVAR_CONSTRAINTS_VERSAO()}");
            if (stmt.executeUpdate() < 0) {
                throw new IllegalStateException("Erro ao ativar as constraints");
            }
        } finally {
            DatabaseConnection.close(stmt);
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

    private void setarVidaUtilModeloCheck(@NotNull final Connection conn,
                                          @NotNull final Long codModelo,
                                          final long versaoAtual,
                                          @NotNull final LocalDate dataInicial,
                                          @NotNull final LocalDate dataFinal) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO CHECK_VIDA_MODELO_AUX (" +
                    "cod_modelo," +
                    "cod_modelo_versao," +
                    "data_inicial," +
                    "data_final) VALUES (?, ?, ?, ?);");
            stmt.setLong(1, codModelo);
            stmt.setLong(2, versaoAtual);
            stmt.setObject(3, dataInicial);
            stmt.setObject(4, dataFinal);
            if (stmt.executeUpdate() < 0) {
                throw new IllegalStateException("Erro ao setar vida útil do modelo de checklist");
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
            stmt.setLong(1, versaoModelo);
            stmt.setLong(2, codChecklist);
            if (stmt.executeUpdate() != 1) {
                throw new IllegalStateException("Erro ao setar versão " + versaoModelo + " no checklist " + codChecklist);
            }
        } finally {
            DatabaseConnection.close(stmt);
        }
    }

    private long criaVersaoModelo(@NotNull final Connection conn,
                                  @NotNull final Long codModelo,
                                  @NotNull final Long codPrimeiroChecklistVersaoModelo,
                                  @NotNull final OffsetDateTime dataHoraAtual) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * from func_checklist_cria_versao_modelo(" +
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
                    "CM.COD_UNIDADE FROM CHECKLIST_MODELO_DATA CM WHERE CM.CODIGO = 391;");
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
                    "CD.COD_UNIDADE, " +
                    "(CD.DATA_HORA AT TIME ZONE TZ_UNIDADE(CD.COD_UNIDADE)) :: DATE " +
                    "FROM CHECKLIST_DATA CD " +
                    "WHERE CD.COD_CHECKLIST_MODELO = ? " +
                    "ORDER BY CD.DATA_HORA ASC;");
            stmt.setLong(1, codModeloChecklist);
            rSet = stmt.executeQuery();
            final List<MigrationCheck> checks = new ArrayList<>();
            while (rSet.next()) {
                checks.add(new MigrationCheck(
                        rSet.getLong(1),
                        rSet.getLong(2),
                        rSet.getObject(3, LocalDate.class)));
            }
            return checks;
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    private void log(@NotNull final String message) {
        Log.d(TAG, message);
    }

    private static class MigrationCheck {
        @NotNull
        private final Long codigo;
        @NotNull
        private final Long codUnidade;
        @NotNull
        private final LocalDate dataRealizacao;

        private MigrationCheck(@NotNull final Long codigo,
                               @NotNull final Long codUnidade,
                               @NotNull final LocalDate dataRealizacao) {
            this.codigo = codigo;
            this.codUnidade = codUnidade;
            this.dataRealizacao = dataRealizacao;
        }

        @NotNull
        public Long getCodigo() {
            return codigo;
        }

        @NotNull
        public Long getCodUnidade() {
            return codUnidade;
        }

        @NotNull
        public LocalDate getDataRealizacao() {
            return dataRealizacao;
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