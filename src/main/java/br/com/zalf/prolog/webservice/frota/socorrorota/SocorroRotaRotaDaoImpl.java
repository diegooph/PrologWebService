package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.SocorroRotaConverter;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */

public final class SocorroRotaRotaDaoImpl extends DatabaseConnection implements SocorroRotaDao {

    @NotNull
    @Override
    public Long aberturaSocorro(@NotNull final SocorroRotaAbertura socorroRotaAbertura) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_SOCORRO_ROTA_ABERTURA(" +
                    "F_COD_UNIDADE := ?," +
                    "F_COD_COLABORADOR_ABERTURA := ?," +
                    "F_COD_VEICULO_PROBLEMA := ?," +
                    "F_KM_VEICULO_ABERTURA := ?," +
                    "F_COD_PROBLEMA_SOCORRO_ROTA := ?," +
                    "F_DESCRICAO_PROBLEMA := ?::TEXT," +
                    "F_DATA_HORA_ABERTURA := ?," +
                    "F_URL_FOTO_1_ABERTURA := ?::TEXT," +
                    "F_URL_FOTO_2_ABERTURA := ?::TEXT," +
                    "F_URL_FOTO_3_ABERTURA := ?::TEXT," +
                    "F_LATITUDE_ABERTURA := ?::TEXT," +
                    "F_LONGITUDE_ABERTURA := ?::TEXT," +
                    "F_PRECISAO_LOCALIZACAO_ABERTURA_METROS := ?," +
                    "F_ENDERECO_AUTOMATICO := ?," +
                    "F_PONTO_REFERENCIA := ?::TEXT," +
                    "F_VERSAO_APP_MOMENTO_ABERTURA := ?," +
                    "F_DEVICE_ID_ABERTURA := ?::TEXT," +
                    "F_DEVICE_IMEI_ABERTURA := ?::TEXT," +
                    "F_DEVICE_UPTIME_MILLIS_ABERTURA := ?," +
                    "F_ANDROID_API_VERSION_ABERTURA := ?," +
                    "F_MARCA_DEVICE_ABERTURA := ?::TEXT," +
                    "F_MODELO_DEVICE_ABERTURA := ?::TEXT) AS CODIGO;");
            final Long codUnidade = socorroRotaAbertura.getCodUnidade();
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, socorroRotaAbertura.getCodColaborador());
            stmt.setLong(3, socorroRotaAbertura.getCodVeiculoProblema());
            stmt.setLong(4, socorroRotaAbertura.getKmVeiculoAbertura());
            stmt.setLong(5, socorroRotaAbertura.getCodProblemaSocorroRota());
            stmt.setString(6, socorroRotaAbertura.getDescricaoProblema());
            // Ignoramos a data/hora do objeto e usamos a do WS.
            stmt.setObject(7, Now.offsetDateTimeUtc());
            stmt.setString(8, socorroRotaAbertura.getUrlFoto1Abertura());
            stmt.setString(9, socorroRotaAbertura.getUrlFoto2Abertura());
            stmt.setString(10, socorroRotaAbertura.getUrlFoto3Abertura());
            stmt.setString(11, socorroRotaAbertura.getLocalizacao().getLatitude());
            stmt.setString(12, socorroRotaAbertura.getLocalizacao().getLongitude());
            stmt.setObject(13, socorroRotaAbertura.getLocalizacao().getPrecisaoLocalizacaoMetros(), SqlType.NUMERIC.asIntTypeJava());
            stmt.setString(14, socorroRotaAbertura.getEnderecoAutomatico());
            stmt.setString(15, socorroRotaAbertura.getPontoReferencia());
            stmt.setLong(16, socorroRotaAbertura.getVersaoAppAtual());
            stmt.setString(17, socorroRotaAbertura.getDeviceId());
            stmt.setString(18, socorroRotaAbertura.getDeviceImei());
            stmt.setLong(19, socorroRotaAbertura.getDeviceUptimeMillis());
            stmt.setInt(20, socorroRotaAbertura.getAndroidApiVersion());
            stmt.setString(21, socorroRotaAbertura.getMarcaDevice());
            stmt.setString(22, socorroRotaAbertura.getModeloDevice());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new Throwable("Erro ao abrir uma solitação de socorro");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<UnidadeAberturaSocorro> getUnidadesDisponiveisAberturaSocorroByCodColaborador(
            @NotNull final Long codColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT(CODIGO_UNIDADE), NOME_UNIDADE " +
                    "FROM FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR := ?) ORDER BY NOME_UNIDADE;");
            stmt.setLong(1, codColaborador);
            rSet = stmt.executeQuery();
            final List<UnidadeAberturaSocorro> unidades = new ArrayList<>();
            while (rSet.next()) {
                unidades.add(SocorroRotaConverter.createUnidadeAberturaSocorro(rSet));
            }
            return unidades;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<VeiculoAberturaSocorro> getVeiculosDisponiveisAberturaSocorroByUnidade(
            @NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT CODIGO, PLACA, KM FROM VEICULO WHERE COD_UNIDADE = ? AND " +
                    "STATUS_ATIVO = TRUE ORDER BY PLACA;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            final List<VeiculoAberturaSocorro> veiculos = new ArrayList<>();
            while (rSet.next()) {
                veiculos.add(SocorroRotaConverter.createVeiculoAberturaSocorro(rSet));
            }
            return veiculos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<OpcaoProblemaAberturaSocorro> getOpcoesProblemasDisponiveisAberturaSocorroByEmpresa(
            @NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT CODIGO, DESCRICAO, OBRIGA_DESCRICAO " +
                    "FROM SOCORRO_ROTA_OPCAO_PROBLEMA " +
                    "WHERE COD_EMPRESA = ? AND STATUS_ATIVO IS TRUE " +
                    "ORDER BY DESCRICAO;");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            final List<OpcaoProblemaAberturaSocorro> opcoesProblemas = new ArrayList<>();
            while (rSet.next()) {
                opcoesProblemas.add(SocorroRotaConverter.createOpcaoProblemaAberturaSocorro(rSet));
            }
            return opcoesProblemas;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<SocorroRotaListagem> getListagemSocorroRota(
            @NotNull final List<Long> codUnidades,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_SOCORRO_ROTA_LISTAGEM(" +
                    "F_COD_UNIDADES := ?, " +
                    "F_DATA_INICIAL :=?," +
                    "F_DATA_FINAL := ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            rSet = stmt.executeQuery();
            final List<SocorroRotaListagem> socorrosRota = new ArrayList<>();
            while (rSet.next()) {
                socorrosRota.add(SocorroRotaConverter.createSocorroRotaListagem(rSet));
            }
            return socorrosRota;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long invalidacaoSocorro(@NotNull final SocorroRotaInvalidacao socorroRotaInvalidacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_SOCORRO_ROTA_INVALIDACAO(" +
                    "F_COD_SOCORRO_ROTA := ?," +
                    "F_COD_COLABORADOR_INVALIDACAO := ?," +
                    "F_MOTIVO_INVALIDACAO := ?::TEXT," +
                    "F_DATA_HORA_INVALIDACAO := ?," +
                    "F_URL_FOTO_1_INVALIDACAO := ?::TEXT," +
                    "F_URL_FOTO_2_INVALIDACAO := ?::TEXT," +
                    "F_URL_FOTO_3_INVALIDACAO := ?::TEXT," +
                    "F_LATITUDE_INVALIDACAO := ?::TEXT," +
                    "F_LONGITUDE_INVALIDACAO := ?::TEXT," +
                    "F_PRECISAO_LOCALIZACAO_INVALIDACAO_METROS := ?," +
                    "F_ENDERECO_AUTOMATICO := ?::TEXT," +
                    "F_VERSAO_APP_MOMENTO_INVALIDACAO := ?," +
                    "F_DEVICE_ID_INVALIDACAO := ?::TEXT," +
                    "F_DEVICE_IMEI_INVALIDACAO := ?::TEXT," +
                    "F_DEVICE_UPTIME_MILLIS_INVALIDACAO := ?," +
                    "F_ANDROID_API_VERSION_INVALIDACAO := ?," +
                    "F_MARCA_DEVICE_INVALIDACAO := ?::TEXT," +
                    "F_MODELO_DEVICE_INVALIDACAO := ?::TEXT) AS CODIGO;");
            final Long codUnidade = socorroRotaInvalidacao.getCodUnidade();
            stmt.setLong(1, socorroRotaInvalidacao.getCodSocorroRota());
            stmt.setLong(2, socorroRotaInvalidacao.getCodColaborador());
            stmt.setString(3, socorroRotaInvalidacao.getMotivoInvalidacao());
            // Ignoramos a data hora do objeto e usamos a do WS
            stmt.setObject(4, Now.offsetDateTimeUtc());
            stmt.setString(5, socorroRotaInvalidacao.getUrlFoto1());
            stmt.setString(6, socorroRotaInvalidacao.getUrlFoto2());
            stmt.setString(7, socorroRotaInvalidacao.getUrlFoto3());
            stmt.setString(8, socorroRotaInvalidacao.getLocalizacao().getLatitude());
            stmt.setString(9, socorroRotaInvalidacao.getLocalizacao().getLongitude());
            stmt.setObject(10, socorroRotaInvalidacao.getLocalizacao().getPrecisaoLocalizacaoMetros(), SqlType.NUMERIC.asIntTypeJava());
            stmt.setString(11, socorroRotaInvalidacao.getEnderecoAutomatico());
            stmt.setLong(12, socorroRotaInvalidacao.getVersaoAppAtual());
            stmt.setString(13, socorroRotaInvalidacao.getDeviceId());
            stmt.setString(14, socorroRotaInvalidacao.getDeviceImei());
            stmt.setLong(15, socorroRotaInvalidacao.getDeviceUptimeMillis());
            stmt.setInt(16, socorroRotaInvalidacao.getAndroidApiVersion());
            stmt.setString(17, socorroRotaInvalidacao.getMarcaDevice());
            stmt.setString(18, socorroRotaInvalidacao.getModeloDevice());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new Throwable("Erro ao invalidar esta solitação de socorro");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long atendimentoSocorro(@NotNull final SocorroRotaAtendimento socorroRotaAtendimento) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_SOCORRO_ROTA_ATENDIMENTO(" +
                    "F_COD_SOCORRO_ROTA := ?," +
                    "F_COD_COLABORADOR_ATENDIMENTO := ?," +
                    "F_OBSERVACAO_ATENDIMENTO := ?::TEXT," +
                    "F_DATA_HORA_ATENDIMENTO := ?," +
                    "F_LATITUDE_ATENDIMENTO := ?::TEXT," +
                    "F_LONGITUDE_ATENDIMENTO := ?::TEXT," +
                    "F_PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS := ?," +
                    "F_ENDERECO_AUTOMATICO := ?::TEXT," +
                    "F_VERSAO_APP_MOMENTO_ATENDIMENTO := ?," +
                    "F_DEVICE_ID_ATENDIMENTO := ?::TEXT," +
                    "F_DEVICE_IMEI_ATENDIMENTO := ?::TEXT," +
                    "F_DEVICE_UPTIME_MILLIS_ATENDIMENTO := ?," +
                    "F_ANDROID_API_VERSION_ATENDIMENTO := ?," +
                    "F_MARCA_DEVICE_ATENDIMENTO := ?::TEXT," +
                    "F_MODELO_DEVICE_ATENDIMENTO := ?::TEXT) AS CODIGO;");
            final Long codUnidade = socorroRotaAtendimento.getCodUnidade();
            stmt.setLong(1, socorroRotaAtendimento.getCodSocorroRota());
            stmt.setLong(2, socorroRotaAtendimento.getCodColaborador());
            stmt.setString(3, socorroRotaAtendimento.getObservacaoAtendimento());
            // Ignoramos a data hora do objeto e usamos a do WS
            stmt.setObject(4, Now.offsetDateTimeUtc());
            stmt.setString(5, socorroRotaAtendimento.getLocalizacao().getLatitude());
            stmt.setString(6, socorroRotaAtendimento.getLocalizacao().getLongitude());
            stmt.setObject(7, socorroRotaAtendimento.getLocalizacao().getPrecisaoLocalizacaoMetros(), SqlType.NUMERIC.asIntTypeJava());
            stmt.setString(8, socorroRotaAtendimento.getEnderecoAutomatico());
            stmt.setLong(9, socorroRotaAtendimento.getVersaoAppAtual());
            stmt.setString(10, socorroRotaAtendimento.getDeviceId());
            stmt.setString(11, socorroRotaAtendimento.getDeviceImei());
            stmt.setLong(12, socorroRotaAtendimento.getDeviceUptimeMillis());
            stmt.setInt(13, socorroRotaAtendimento.getAndroidApiVersion());
            stmt.setString(14, socorroRotaAtendimento.getMarcaDevice());
            stmt.setString(15, socorroRotaAtendimento.getModeloDevice());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new Throwable("Erro ao atender esta solitação de socorro");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}