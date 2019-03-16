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
            List<PerguntaModeloChecklistOffline> perguntas = new ArrayList<>();
            List<AlternativaModeloChecklistOffline> alternativas = new ArrayList<>();

            Long codModeloChecklistAnterior = null;
            Long codPerguntaAnterior = null;
            while (rSet.next()) {
                final Long codModeloChecklistAtual = rSet.getLong("COD_MODELO_CHECKLIST");
                if (codModeloChecklistAnterior == null) {
                    codModeloChecklistAnterior = codModeloChecklistAtual;
                }

                final Long codPerguntaAtual = rSet.getLong("COD_PERGUNTA");
                if (codPerguntaAnterior == null) {
                    codPerguntaAnterior = codPerguntaAtual;
                }

                if (!codModeloChecklistAnterior.equals(codModeloChecklistAtual)) {
                    alternativas.add(ChecklistOfflineConverter.createAlternativaModeloChecklistOffline(rSet));
                    perguntas.add(ChecklistOfflineConverter.createPerguntaModeloChecklistOffline(rSet, alternativas));
                    modelosChecklistOffline.add(
                            ChecklistOfflineConverter.createModeloChecklistOffline(rSet, perguntas));
                    perguntas = new ArrayList<>();
                    alternativas = new ArrayList<>();
                } else {
                    if (!codPerguntaAnterior.equals(codPerguntaAtual)) {
                        perguntas.add(
                                ChecklistOfflineConverter.createPerguntaModeloChecklistOffline(rSet, alternativas));
                        alternativas = new ArrayList<>();
                    }
                    alternativas.add(ChecklistOfflineConverter.createAlternativaModeloChecklistOffline(rSet));
                }

                codModeloChecklistAnterior = codModeloChecklistAtual;
                codPerguntaAnterior = codPerguntaAtual;
            }
            if (codModeloChecklistAnterior != null) {
                perguntas.add(ChecklistOfflineConverter.createPerguntaModeloChecklistOffline(rSet, alternativas));
                modelosChecklistOffline.add(ChecklistOfflineConverter.createModeloChecklistOffline(rSet, perguntas));
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
