package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
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

    @Override
    public DadosChecklistOfflineUnidade getVersaoDadosAtual(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT VERSAO_DADOS, TOKEN_SINCRONIZACAO_CHECKLIST " +
                    "   FROM CHECKLIST_OFFLINE_DADOS_UNIDADE " +
                    "   WHERE COD_UNIDADE = ?");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final String token = rSet.getString("TOKEN_SINCRONIZACAO_CHECKLIST");
                final long versao_dados = rSet.getLong("VERSAO_DADOS");
                // Precisamos nos atentar à um cenário incrivelmente improvável de acontecer, porém se acontecer esse
                // código estará preparado para lidar. Trata-se do caso de a Unidade ter dados cadastrados na tabela
                // CHECKLIST_OFFLINE_DADOS_UNIDADE porém não ter (ou ter um valor inválido) a informação VERSAO_DADOS.
                if (versao_dados > 0 && token != null) {
                    return new DadosChecklistOfflineUnidade(codUnidade, versao_dados, token);
                } else {
                    throw new SQLException("A unidade possui configuração inconsistentes.\n" +
                            "CodUnidade: " + codUnidade);
                }
            } else {
                return new DadosChecklistOfflineUnidade(codUnidade);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

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
                    // Processamos Pergunta/Alternativas apenas se não tem cargo ou tipo de veículo para processar.
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
                        // Processamos cargo ou tipo de veículo do modelo de checklist.
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

    @Override
    public List<VeiculoChecklistOffline> getVeiculosChecklistOffline(final Long codUnidade) {
        return null;
    }

    @Override
    public EmpresaChecklistOffline getEmpresaChecklistOffline(final Long codUnidade) {
        return null;
    }
}
