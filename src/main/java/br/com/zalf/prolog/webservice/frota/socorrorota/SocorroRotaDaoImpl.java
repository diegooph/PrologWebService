package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.*;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */

public final class SocorroRotaDaoImpl extends DatabaseConnection implements SocorroRotaDao {

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
    public List<ColaboradorNotificacaoAberturaSocorro> getColaboradoresNotificacaoAbertura(
            @NotNull final Long codUnidade) throws Throwable {
        Preconditions.checkNotNull(codUnidade, "codUnidade não pode ser null!");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "C.CODIGO, " +
                    "PCT.TOKEN_PUSH_FIREBASE " +
                    "FROM COLABORADOR C " +
                    "JOIN CARGO_FUNCAO_PROLOG_V11 CFP ON C.COD_UNIDADE = CFP.COD_UNIDADE " +
                    "AND C.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR " +
                    "JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO AND " +
                    "F.CODIGO = CFP.COD_FUNCAO_COLABORADOR AND C.COD_EMPRESA = F.COD_EMPRESA " +
                    "JOIN MESSAGING.PUSH_COLABORADOR_TOKEN PCT ON C.CODIGO = PCT.COD_COLABORADOR " +
                    "WHERE C.COD_UNIDADE = ? " +
                    "AND CFP.COD_FUNCAO_PROLOG = ? " +
                    // Apenas busca os colaboradores que tenham tokens para os aplicativos do Prolog.
                    "AND PCT.APLICACAO_REFERENCIA_TOKEN IN ('PROLOG_ANDROID_DEBUG', 'PROLOG_ANDROID_PROD') " +
                    "AND C.STATUS_ATIVO = TRUE;");
            stmt.setLong(1, codUnidade);
            stmt.setInt(2, Pilares.Frota.SocorroRota.TRATAR_SOCORRO);
            rSet = stmt.executeQuery();
            if (!rSet.next()) {
                return Collections.emptyList();
            } else {
                final List<ColaboradorNotificacaoAberturaSocorro> colaboradores = new ArrayList<>();
                do {
                    colaboradores.add(new ColaboradorNotificacaoAberturaSocorro(
                            rSet.getLong("CODIGO"),
                            rSet.getString("TOKEN_PUSH_FIREBASE")));
                } while (rSet.next());
                return colaboradores;
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
    public List<VeiculoAberturaSocorro> getVeiculosDisponiveisAberturaSocorroByUnidade(@NotNull final Long codUnidade)
            throws Throwable {
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
    public List<SocorroRotaListagem> getListagemSocorroRota(@NotNull final List<Long> codUnidades,
                                                            @NotNull final LocalDate dataInicial,
                                                            @NotNull final LocalDate dataFinal,
                                                            @NotNull final String userToken) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_SOCORRO_ROTA_LISTAGEM(" +
                    "F_COD_UNIDADES := ?, " +
                    "F_DATA_INICIAL := ?," +
                    "F_DATA_FINAL := ?," +
                    "F_TOKEN := ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            stmt.setString(4, TokenCleaner.getOnlyToken(userToken));
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

    @NotNull
    @Override
    public Long finalizacaoSocorro(@NotNull final SocorroRotaFinalizacao socorroRotaFinalizacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_SOCORRO_ROTA_FINALIZACAO(" +
                    "F_COD_SOCORRO_ROTA := ?," +
                    "F_COD_COLABORADOR_FINALIZACAO := ?," +
                    "F_MOTIVO_FINALIZACAO := ?::TEXT," +
                    "F_DATA_HORA_FINALIZACAO := ?," +
                    "F_URL_FOTO_1_FINALIZACAO := ?::TEXT," +
                    "F_URL_FOTO_2_FINALIZACAO := ?::TEXT," +
                    "F_URL_FOTO_3_FINALIZACAO := ?::TEXT," +
                    "F_LATITUDE_FINALIZACAO := ?::TEXT," +
                    "F_LONGITUDE_FINALIZACAO := ?::TEXT," +
                    "F_PRECISAO_LOCALIZACAO_FINALIZACAO_METROS := ?," +
                    "F_ENDERECO_AUTOMATICO := ?::TEXT," +
                    "F_VERSAO_APP_MOMENTO_FINALIZACAO := ?," +
                    "F_DEVICE_ID_FINALIZACAO := ?::TEXT," +
                    "F_DEVICE_IMEI_FINALIZACAO := ?::TEXT," +
                    "F_DEVICE_UPTIME_MILLIS_FINALIZACAO := ?," +
                    "F_ANDROID_API_VERSION_FINALIZACAO := ?," +
                    "F_MARCA_DEVICE_FINALIZACAO := ?::TEXT," +
                    "F_MODELO_DEVICE_FINALIZACAO := ?::TEXT) AS CODIGO;");
            final Long codUnidade = socorroRotaFinalizacao.getCodUnidade();
            stmt.setLong(1, socorroRotaFinalizacao.getCodSocorroRota());
            stmt.setLong(2, socorroRotaFinalizacao.getCodColaborador());
            stmt.setString(3, socorroRotaFinalizacao.getObservacaoFinalizacao());
            // Ignoramos a data hora do objeto e usamos a do WS
            stmt.setObject(4, Now.offsetDateTimeUtc());
            stmt.setString(5, socorroRotaFinalizacao.getUrlFoto1Finalizacao());
            stmt.setString(6, socorroRotaFinalizacao.getUrlFoto2Finalizacao());
            stmt.setString(7, socorroRotaFinalizacao.getUrlFoto3Finalizacao());
            stmt.setString(8, socorroRotaFinalizacao.getLocalizacao().getLatitude());
            stmt.setString(9, socorroRotaFinalizacao.getLocalizacao().getLongitude());
            stmt.setObject(10, socorroRotaFinalizacao.getLocalizacao().getPrecisaoLocalizacaoMetros(), SqlType.NUMERIC.asIntTypeJava());
            stmt.setString(11, socorroRotaFinalizacao.getEnderecoAutomatico());
            stmt.setLong(12, socorroRotaFinalizacao.getVersaoAppAtual());
            stmt.setString(13, socorroRotaFinalizacao.getDeviceId());
            stmt.setString(14, socorroRotaFinalizacao.getDeviceImei());
            stmt.setLong(15, socorroRotaFinalizacao.getDeviceUptimeMillis());
            stmt.setInt(16, socorroRotaFinalizacao.getAndroidApiVersion());
            stmt.setString(17, socorroRotaFinalizacao.getMarcaDevice());
            stmt.setString(18, socorroRotaFinalizacao.getModeloDevice());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new Throwable("Erro ao finalizar esta solitação de socorro");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }


    @NotNull
    @Override
    public SocorroRotaVisualizacao getVisualizacaoSocorroRota(@NotNull final Long codSocorroRota) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_SOCORRO_ROTA_VISUALIZACAO(F_COD_SOCORRO_ROTA := ?);");
            stmt.setLong(1, codSocorroRota);
            rSet = stmt.executeQuery();

            if (rSet.next()) {
                final SocorroRotaAberturaVisualizacao socorroRotaAberturaVisualizacao =
                        SocorroRotaConverter.createSocorroRotaAberturaVisualizacao(rSet);

                SocorroRotaAtendimentoVisualizacao socorroRotaAtendimentoVisualizacao = null;
                if (rSet.getObject("DATA_HORA_ATENDIMENTO", LocalDateTime.class) != null) {
                    socorroRotaAtendimentoVisualizacao =
                            SocorroRotaConverter.createSocorroRotaAtendimentoVisualizacao(rSet);
                }

                SocorroRotaInvalidacaoVisualizacao socorroRotaInvalidacaoVisualizacao = null;
                if (rSet.getObject("DATA_HORA_INVALIDACAO", LocalDateTime.class) != null) {
                    socorroRotaInvalidacaoVisualizacao =
                            SocorroRotaConverter.createSocorroRotaInvalidacaoVisualizacao(rSet);
                }

                SocorroRotaFinalizacaoVisualizacao socorroRotaFinalizacaoVisualizacao = null;
                if (rSet.getObject("DATA_HORA_FINALIZACAO", LocalDateTime.class) != null) {
                    socorroRotaFinalizacaoVisualizacao =
                            SocorroRotaConverter.createSocorroRotaFinalizacaoVisualizacao(rSet);
                }

                final SocorroRotaVisualizacao socorrosRota = new SocorroRotaVisualizacao(
                        rSet.getLong("COD_SOCORRO_ROTA"),
                        StatusSocorroRota.fromString(rSet.getString("STATUS_SOCORRO_ROTA")),
                        socorroRotaAberturaVisualizacao,
                        socorroRotaAtendimentoVisualizacao,
                        socorroRotaFinalizacaoVisualizacao,
                        socorroRotaInvalidacaoVisualizacao
                );
                return socorrosRota;
            } else {
                throw new Throwable("Erro ao finalizar esta solitação de socorro");
            }


        } finally {
            close(conn, stmt, rSet);
        }
    }
}