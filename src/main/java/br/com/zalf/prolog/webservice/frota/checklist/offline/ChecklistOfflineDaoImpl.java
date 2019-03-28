package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistResposta;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ChecklistOfflineDaoImpl extends DatabaseConnection implements ChecklistOfflineDao {

    @NotNull
    @Override
    public Long insertChecklistOffline(final long versaoAppMomentoSincronizacao,
                                       @NotNull final ChecklistInsercao checklist) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_CHECKLIST_INSERT_CHECKLIST_INFOS(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "AS CODIGO;");
            stmt.setLong(1, checklist.getCodUnidade());
            stmt.setLong(2, checklist.getCodModelo());
            stmt.setObject(3, checklist.getDataHoraRealizacao());
            stmt.setLong(4, checklist.getCodColaborador());
            stmt.setLong(5, checklist.getCodVeiculo());
            stmt.setString(6, checklist.getPlacaVeiculo());
            stmt.setString(7, String.valueOf(checklist.getTipo().asChar()));
            stmt.setLong(8, checklist.getKmColetadoVeiculo());
            stmt.setLong(9, checklist.getTempoRealizacaoCheckInMillis());
            stmt.setObject(10, Now.offsetDateTimeUtc());
            stmt.setString(11, checklist.getFonteDataHoraRealizacao().asString());
            stmt.setInt(12, checklist.getVersaoAppMomentoRealizacao());
            stmt.setInt(13, checklist.getVersaoAppMomentoSincronizacao());
            stmt.setString(14, checklist.getDeviceId());
            stmt.setLong(15, checklist.getDeviceUptimeRealizacaoMillis());
            stmt.setLong(16, checklist.getDeviceUptimeSincronizacaoMillis());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long codChecklistInserido = rSet.getLong("CODIGO");
                insertChecklistPerguntasOffline(
                        conn,
                        checklist.getCodUnidade(),
                        checklist.getCodModelo(),
                        codChecklistInserido,
                        checklist.getRespostas());
                conn.commit();
                return codChecklistInserido;
            } else {
                throw new SQLException("Erro ao salvar checklist");
            }
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void insertChecklistPerguntasOffline(@NotNull final Connection conn,
                                                 @NotNull final Long codUnidadeChecklist,
                                                 @NotNull final Long codModeloChecklist,
                                                 @NotNull final Long codChecklistInserido,
                                                 @NotNull final List<ChecklistResposta> respostas) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM FUNC_CHECKLIST_INSERT_RESPOSTAS_CHECKLIST(?, ?, ?, ?, ?, ?);");
            stmt.setLong(1, codUnidadeChecklist);
            stmt.setLong(2, codModeloChecklist);
            stmt.setLong(3, codChecklistInserido);
            int linhasParaExecutar = 0;
            for (final ChecklistResposta resposta : respostas) {
                for (final ChecklistAlternativaResposta alternativa : resposta.getAlternativasRespostas()) {
                    if (alternativa.isAlternativaSelecionada()) {
                        if (alternativa.isTipoOutros()) {
                            stmt.setString(4, alternativa.getRespostaTipoOutros());
                        } else {
                            stmt.setString(4, alternativa.getDescricaoAlternativaNok());
                        }
                    } else {
                        stmt.setString(4, alternativa.getDescricaoAlternativaOk());
                    }
                    stmt.setLong(5, resposta.getCodPergunta());
                    stmt.setLong(6, alternativa.getCodAlternativa());
                    stmt.addBatch();
                    linhasParaExecutar++;
                }
            }
            if (stmt.executeBatch().length != linhasParaExecutar) {
                throw new SQLException("Não foi possível salvar todas as alternativas do checklist");
            }
        } finally {
            close(stmt);
        }
    }

    @Override
    public boolean getChecklistOfflineAtivoEmpresa(@NotNull final Long cpfColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM FUNC_CHECKLIST_OFFLINE_EMPRESA_LIBERADA(?) AS CHECKLIST_OFFLINE_LIBERADO");
            stmt.setLong(1, cpfColaborador);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("CHECKLIST_OFFLINE_LIBERADO");
            } else {
                throw new SQLException(
                        "Erro ao buscar informações se a empresa está liberada para executar checklist offline");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public DadosChecklistOfflineUnidade getVersaoDadosAtual(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT VERSAO_DADOS, TOKEN_SINCRONIZACAO_CHECKLIST " +
                    "   FROM CHECKLIST_OFFLINE_DADOS_UNIDADE " +
                    "   WHERE COD_UNIDADE = ?;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long versaoDados = rSet.getLong("VERSAO_DADOS");
                final String token = rSet.getString("TOKEN_SINCRONIZACAO_CHECKLIST");
                // Precisamos nos atentar à um cenário incrivelmente improvável de acontecer, porém se acontecer esse
                // código estará preparado para lidar. Trata-se do caso de a Unidade ter dados cadastrados na tabela
                // CHECKLIST_OFFLINE_DADOS_UNIDADE porém não ter (ou ter um valor inválido) a informação VERSAO_DADOS.
                if (versaoDados > 0 && token != null) {
                    return new DadosChecklistOfflineUnidade(codUnidade, versaoDados, token);
                } else {
                    throw new SQLException("A unidade possui configuração inconsistentes.\n" +
                            "CodUnidade: " + codUnidade + "\n" +
                            "VersaoDados: " + versaoDados + "\n" +
                            "Token: " + token);
                }
            } else {
                return new DadosChecklistOfflineUnidade(codUnidade);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<ModeloChecklistOffline> getModelosChecklistOffline(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<ModeloChecklistOffline> modelosChecklistOffline = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OFFLINE_GET_MODELOS_DISPONIVEIS(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            ModeloChecklistOffline modelo = null;
            PerguntaModeloChecklistOffline pergunta = null;
            List<PerguntaModeloChecklistOffline> perguntas = new ArrayList<>();
            List<AlternativaModeloChecklistOffline> alternativas = new ArrayList<>();
            List<CargoChecklistOffline> cargos = new ArrayList<>();
            List<TipoVeiculoChecklistOffline> tiposVeiculos = new ArrayList<>();
            while (rSet.next()) {
                if (perguntas.isEmpty() && alternativas.isEmpty()) {
                    // Estamos na primeira linha.
                    // Precisamos inicializar o modelo com as primeiras informações do resultSet.
                    alternativas.add(ChecklistOfflineConverter.createAlternativaModeloChecklistOffline(rSet));
                    pergunta = ChecklistOfflineConverter.createPerguntaModeloChecklistOffline(rSet, alternativas);
                    perguntas.add(pergunta);
                    modelo = ChecklistOfflineConverter.createModeloChecklistOffline(
                            rSet.getLong("COD_UNIDADE_MODELO_CHECKLIST"),
                            rSet.getLong("COD_MODELO_CHECKLIST"),
                            rSet.getString("NOME_MODELO_CHECKLIST"),
                            cargos,
                            tiposVeiculos,
                            perguntas);
                } else
                    // Processamos Pergunta/Alternativas apenas se não tem 'cargo' ou 'tipo de veículo' para processar.
                    if (rSet.getLong("COD_CARGO") <= 0
                            && rSet.getLong("COD_TIPO_VEICULO") <= 0) {
                        if (modelo != null
                                && !modelo.getCodModelo().equals(rSet.getLong("COD_MODELO_CHECKLIST"))) {
                            // Trocou de modelo.
                            modelosChecklistOffline.add(modelo);

                            // Precisamos criar as informações do novo modelo e o modelo em si.
                            alternativas = new ArrayList<>();
                            alternativas.add(ChecklistOfflineConverter.createAlternativaModeloChecklistOffline(rSet));
                            perguntas = new ArrayList<>();
                            pergunta = ChecklistOfflineConverter
                                    .createPerguntaModeloChecklistOffline(rSet, alternativas);
                            perguntas.add(pergunta);
                            cargos = new ArrayList<>();
                            tiposVeiculos = new ArrayList<>();
                            modelo = ChecklistOfflineConverter.createModeloChecklistOffline(
                                    rSet.getLong("COD_UNIDADE_MODELO_CHECKLIST"),
                                    rSet.getLong("COD_MODELO_CHECKLIST"),
                                    rSet.getString("NOME_MODELO_CHECKLIST"),
                                    cargos,
                                    tiposVeiculos,
                                    perguntas);
                        } else {
                            if (pergunta != null
                                    && pergunta.getCodigo().equals(rSet.getLong("COD_PERGUNTA"))) {
                                // Mesma pergunta.
                                // Precisamos processar apenas a nova alternativa.
                                alternativas.add(
                                        ChecklistOfflineConverter.createAlternativaModeloChecklistOffline(rSet));
                            } else {
                                // Trocou de pergunta.
                                // Precisamos criar a nova pergunta e adicionar a ela a nova alternativa;
                                alternativas = new ArrayList<>();
                                alternativas.add(
                                        ChecklistOfflineConverter.createAlternativaModeloChecklistOffline(rSet));
                                pergunta = ChecklistOfflineConverter
                                        .createPerguntaModeloChecklistOffline(rSet, alternativas);
                                perguntas.add(pergunta);
                            }
                        }
                    } else {
                        // Processamos 'cargo' ou 'tipo de veículo' do modelo de checklist.
                        if (modelo != null
                                && modelo.getCodModelo().equals(rSet.getLong("COD_MODELO_CHECKLIST"))
                                && rSet.getLong("COD_CARGO") > 0) {
                            // Adicionamos os cargos do modelo de checklist.
                            cargos.add(ChecklistOfflineConverter.createCargoChecklistOffline(rSet));
                        }
                        if (modelo != null
                                && modelo.getCodModelo().equals(rSet.getLong("COD_MODELO_CHECKLIST"))
                                && rSet.getLong("COD_TIPO_VEICULO") > 0) {
                            // Adicionamos os tipos de veículo do modelo de checklist.
                            tiposVeiculos.add(ChecklistOfflineConverter.createTipoVeiculoChecklistOffline(rSet));
                        }
                    }
            }
            // Não podemos retornar modelo = null caso não retornar nenhum dado do banco,
            // nesse caso não setamos o modelo.
            if (modelo != null) {
                modelosChecklistOffline.add(modelo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return modelosChecklistOffline;
    }

    @NotNull
    @Override
    public List<ColaboradorChecklistOffline> getColaboradoresChecklistOffline(
            @NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<ColaboradorChecklistOffline> colaboradores = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OFFLINE_GET_COLABORADORES_DISPONIVEIS(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                colaboradores.add(ChecklistOfflineConverter.createColaboradorChecklistOffline(rSet));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return colaboradores;
    }

    @NotNull
    @Override
    public List<VeiculoChecklistOffline> getVeiculosChecklistOffline(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<VeiculoChecklistOffline> veiculos = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OFFLINE_GET_PLACAS_DISPONIVEIS(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                veiculos.add(ChecklistOfflineConverter.createVeiculoChecklistOffline(rSet));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return veiculos;
    }

    @NotNull
    @Override
    public EmpresaChecklistOffline getEmpresaChecklistOffline(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OFFLINE_GET_INFORMACOES_EMPRESA(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return ChecklistOfflineConverter.createEmpresaChecklistOffline(rSet);
            } else {
                throw new SQLException("Erro ao buscar inforações da empresa para a unidade: " + codUnidade);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public boolean verifyIfTokenChecklistExists(@NotNull final String tokenSincronizacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT * " +
                    "              FROM CHECKLIST_OFFLINE_DADOS_UNIDADE " +
                    "              WHERE TOKEN_SINCRONIZACAO_CHECKLIST = ?) AS EXISTS_TOKEN;");
            stmt.setString(1, tokenSincronizacao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTS_TOKEN");
            } else {
                throw new SQLException("Erro ao verificar existência do token: " + tokenSincronizacao);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
