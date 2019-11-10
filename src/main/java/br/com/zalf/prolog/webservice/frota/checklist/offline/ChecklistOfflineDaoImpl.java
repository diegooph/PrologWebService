package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.zalf.prolog.webservice.frota.checklist.offline.model.ChecklistOfflineConverter.*;

/**
 * Created on 10/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ChecklistOfflineDaoImpl extends DatabaseConnection implements ChecklistOfflineDao {

    @NotNull
    @Override
    public Long insertChecklistOffline(@NotNull final ChecklistInsercao checklist) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            // Caso o checklist contenha informações que caracterizem uma duplicata nós não iremos inserir,
            // apenas retornamos o código do checklist que já está no banco de dados.
            final Optional<Long> optionalCodChecklist = getCodChecklistIfExists(conn, checklist);
            if (optionalCodChecklist.isPresent()) {
                conn.commit();
                return optionalCodChecklist.get();
            } else {
                final Long codChecklistInserido = internalInsertChecklist(conn, checklist);
                conn.commit();
                return codChecklistInserido;
            }
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn);
        }
    }

    @Override
    public boolean getChecklistOfflineAtivoEmpresa(@NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM FUNC_CHECKLIST_OFFLINE_EMPRESA_LIBERADA(?) AS CHECKLIST_OFFLINE_LIBERADO");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("CHECKLIST_OFFLINE_LIBERADO");
            } else {
                throw new SQLException("Erro ao verificar se a empresa está liberada para executar checklist offline");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Optional<TokenVersaoChecklist> getDadosAtuaisUnidade(@NotNull final Long codUnidade) throws Throwable {
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
                    return Optional.of(new TokenVersaoChecklist(codUnidade, versaoDados, token));
                } else {
                    throw new SQLException("A unidade possui configuração inconsistentes.\n" +
                            "CodUnidade: " + codUnidade + "\n" +
                            "VersaoDados: " + versaoDados + "\n" +
                            "Token: " + token);
                }
            } else {
                // Caso unidade não tenha dados.
                return Optional.empty();
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
                    alternativas.add(createAlternativaModeloChecklistOffline(rSet));
                    pergunta = createPerguntaModeloChecklistOffline(rSet, alternativas);
                    perguntas.add(pergunta);
                    modelo = createModeloChecklistOffline(
                            rSet.getLong("COD_UNIDADE_MODELO_CHECKLIST"),
                            rSet.getLong("COD_MODELO_CHECKLIST"),
                            rSet.getLong("COD_VERSAO_MODELO_CHECKLIST"),
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
                            alternativas.add(createAlternativaModeloChecklistOffline(rSet));
                            perguntas = new ArrayList<>();
                            pergunta = createPerguntaModeloChecklistOffline(rSet, alternativas);
                            perguntas.add(pergunta);
                            cargos = new ArrayList<>();
                            tiposVeiculos = new ArrayList<>();
                            modelo = createModeloChecklistOffline(
                                    rSet.getLong("COD_UNIDADE_MODELO_CHECKLIST"),
                                    rSet.getLong("COD_MODELO_CHECKLIST"),
                                    rSet.getLong("COD_VERSAO_MODELO_CHECKLIST"),
                                    rSet.getString("NOME_MODELO_CHECKLIST"),
                                    cargos,
                                    tiposVeiculos,
                                    perguntas);
                        } else {
                            if (pergunta != null
                                    && pergunta.getCodigo().equals(rSet.getLong("COD_PERGUNTA"))) {
                                // Mesma pergunta.
                                // Precisamos processar apenas a nova alternativa.
                                alternativas.add(createAlternativaModeloChecklistOffline(rSet));
                            } else {
                                // Trocou de pergunta.
                                // Precisamos criar a nova pergunta e adicionar a ela a nova alternativa;
                                alternativas = new ArrayList<>();
                                alternativas.add(createAlternativaModeloChecklistOffline(rSet));
                                pergunta = createPerguntaModeloChecklistOffline(rSet, alternativas);
                                perguntas.add(pergunta);
                            }
                        }
                    } else {
                        // Processamos 'cargo' ou 'tipo de veículo' do modelo de checklist.
                        if (modelo != null
                                && modelo.getCodModelo().equals(rSet.getLong("COD_MODELO_CHECKLIST"))
                                && rSet.getLong("COD_CARGO") > 0) {
                            // Adicionamos os cargos do modelo de checklist.
                            cargos.add(createCargoChecklistOffline(rSet));
                        }
                        if (modelo != null
                                && modelo.getCodModelo().equals(rSet.getLong("COD_MODELO_CHECKLIST"))
                                && rSet.getLong("COD_TIPO_VEICULO") > 0) {
                            // Adicionamos os tipos de veículo do modelo de checklist.
                            tiposVeiculos.add(createTipoVeiculoChecklistOffline(rSet));
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
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OFFLINE_GET_COLABORADORES_DISPONIVEIS(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            final List<ColaboradorChecklistOffline> colaboradores = new ArrayList<>();
            while (rSet.next()) {
                colaboradores.add(createColaboradorChecklistOffline(rSet));
            }
            return colaboradores;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<VeiculoChecklistOffline> getVeiculosChecklistOffline(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OFFLINE_GET_PLACAS_DISPONIVEIS(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            final List<VeiculoChecklistOffline> veiculos = new ArrayList<>();
            while (rSet.next()) {
                veiculos.add(createVeiculoChecklistOffline(rSet));
            }
            return veiculos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public UnidadeChecklistOffline getUnidadeChecklistOffline(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OFFLINE_GET_INFORMACOES_UNIDADE(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createEmpresaChecklistOffline(rSet);
            } else {
                throw new SQLException("Erro ao buscar informações da unidade: " + codUnidade);
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

    @NotNull
    private Optional<Long> getCodChecklistIfExists(@NotNull final Connection conn,
                                                   @NotNull final ChecklistInsercao checklist) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_CHECKLIST_GET_COD_CHECKLIST_DUPLICADO(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(checklist.getCodUnidade(), conn);
            stmt.setLong(1, checklist.getCodUnidade());
            stmt.setLong(2, checklist.getCodModelo());
            stmt.setObject(3, checklist.getDataHoraRealizacao().atZone(zoneId).toOffsetDateTime());
            stmt.setLong(4, checklist.getCodColaborador());
            stmt.setString(5, checklist.getPlacaVeiculo());
            stmt.setString(6, String.valueOf(checklist.getTipo().asChar()));
            stmt.setLong(7, checklist.getKmColetadoVeiculo());
            stmt.setLong(8, checklist.getTempoRealizacaoCheckInMillis());
            stmt.setString(9, checklist.getFonteDataHoraRealizacao().asString());
            stmt.setInt(10, checklist.getVersaoAppMomentoRealizacao());
            stmt.setString(11, checklist.getDeviceId());
            stmt.setString(12, checklist.getDeviceImei());
            stmt.setLong(13, checklist.getDeviceUptimeRealizacaoMillis());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (rSet.getBoolean("CHECKLIST_JA_EXISTE")) {
                    return Optional.of(rSet.getLong("COD_CHECKLIST"));
                } else {
                    return Optional.empty();
                }
            } else {
                throw new SQLException("Não foi possível verificar se checklist já existe");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private Long internalInsertChecklist(@NotNull final Connection conn,
                                         @NotNull final ChecklistInsercao checklist) throws Throwable {
       return Injection.provideChecklistDao().insert(conn, checklist, true, true);
    }
}
