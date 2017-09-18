package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.colaborador.Cargo;
import br.com.zalf.prolog.webservice.colaborador.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.Unidade;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.FonteDataHora;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Icone;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import com.sun.istack.internal.NotNull;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Zart on 18/08/2017.
 */
public class ControleIntervaloDaoImpl extends DatabaseConnection implements ControleIntervaloDao {

    private static final String TAG = ControleIntervaloDaoImpl.class.getSimpleName();

    @Override
    public List<TipoIntervalo> getTiposIntervalosByUnidade(Long codUnidade, boolean withCargos) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<TipoIntervalo> tipos = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT IT.CODIGO AS CODIGO_TIPO_INTERVALO, IT.NOME AS NOME_TIPO_INTERVALO, " +
                    " IT.COD_UNIDADE, IT.ATIVO, IT.HORARIO_SUGERIDO, IT.ICONE, IT.TEMPO_ESTOURO_MINUTOS, IT.TEMPO_RECOMENDADO_MINUTOS FROM\n" +
                    "  INTERVALO_TIPO_CARGO ITC JOIN INTERVALO_TIPO IT ON ITC.COD_UNIDADE = IT.COD_UNIDADE AND ITC.COD_TIPO_INTERVALO = IT.CODIGO\n" +
                    " WHERE IT.COD_UNIDADE = ? AND IT.ATIVO IS TRUE");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                tipos.add(createTipoInvervalo(rSet, withCargos, conn));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return tipos;
    }

    @Override
    public Intervalo getIntervaloAberto(Long cpf, TipoIntervalo tipoInvervalo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * \n" +
                    "FROM\n" +
                    "  INTERVALO I\n" +
                    "WHERE I.CPF_COLABORADOR = ? AND I.COD_TIPO_INTERVALO = ? AND I.COD_UNIDADE = (SELECT COD_UNIDADE\n" +
                    "                                                                                     FROM COLABORADOR\n" +
                    "                                                                                     WHERE CPF = ?) AND\n" +
                    "      DATA_HORA_FIM IS NULL\n" +
                    "      AND DATA_HORA_INICIO >= (SELECT MAX(DATA_HORA_INICIO)\n" +
                    "                               FROM INTERVALO I\n" +
                    "                               WHERE I.CPF_COLABORADOR = ?\n" +
                    "                                     AND I.COD_TIPO_INTERVALO = ? AND I.COD_UNIDADE = (SELECT COD_UNIDADE\n" +
                    "                                                                                       FROM COLABORADOR\n" +
                    "                                                                                       WHERE CPF = ?));");
            stmt.setLong(1, cpf);
            stmt.setLong(2, tipoInvervalo.getCodigo());
            stmt.setLong(3, cpf);
            stmt.setLong(4, cpf);
            stmt.setLong(5, tipoInvervalo.getCodigo());
            stmt.setLong(6, cpf);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createIntervaloAberto(rSet);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    private Intervalo createIntervaloAberto(ResultSet rSet) throws SQLException {
        Intervalo intervalo = new Intervalo();
        intervalo.setCodigo(rSet.getLong("CODIGO"));
        intervalo.setDataHoraInicio(rSet.getTimestamp("DATA_HORA_INICIO"));
        intervalo.setValido(rSet.getBoolean("VALIDO"));
        intervalo.setTempoDecorrido(Duration.ofSeconds(DateUtils.secondsBetween(intervalo.getDataHoraInicio().getTime(), System.currentTimeMillis())));
        Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF_COLABORADOR"));
        TipoIntervalo tipoIntervalo = new TipoIntervalo();
        tipoIntervalo.setCodigo(rSet.getLong("COD_TIPO_INTERVALO"));
        intervalo.setTipo(tipoIntervalo);
        intervalo.setColaborador(colaborador);
        return intervalo;
    }

    @Override
    public void insertOrUpdateIntervalo(Intervalo intervalo) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            if (intervalo.getDataHoraInicio() != null && intervalo.getFonteDataHoraFim() != null) {
                // Intervalo completo, apenas inserir.
                insertIntervalo(intervalo);
            } else if (intervalo.getDataHoraInicio() == null && intervalo.getFonteDataHoraFim() != null) {
                // Intervalo veio apenas com data de finalização, verificar se existe um em aberto para fazer o update,
                // caso não tenha, inserir a finalização avulsa.
                final Intervalo intervaloEmAberto = getIntervaloAberto(
                        intervalo.getColaborador().getCpf(),
                        intervalo.getTipo());
                if (intervaloEmAberto != null) {
                    intervaloEmAberto.setDataHoraFim(intervalo.getDataHoraFim());
                    intervaloEmAberto.setFonteDataHoraFim(intervalo.getFonteDataHoraFim());
                    intervaloEmAberto.setJustificativaEstouro(intervalo.getJustificativaEstouro());
                    intervaloEmAberto.setJustificativaTempoRecomendado(intervalo.getJustificativaTempoRecomendado());
                    updateIntervalo(intervaloEmAberto);
                } else {
                    insertIntervalo(intervalo);
                }
            } else {
                // Intervalo veio apenas com data_hora de início, inserir na tabela.
                insertIntervalo(intervalo);
            }
        } finally {
            closeConnection(conn, null, null);
        }
    }

    @Override
    public void insertIntervalo(Intervalo intervalo) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO INTERVALO(COD_UNIDADE, COD_TIPO_INTERVALO, CPF_COLABORADOR," +
                    "FONTE_DATA_HORA_INICIO, DATA_HORA_INICIO, FONTE_DATA_HORA_FIM, DATA_HORA_FIM, JUSTIFICATIVA_ESTOURO, " +
                    " JUSTIFICATIVA_TEMPO_RECOMENDADO) \n" +
                    "    VALUES (?,?,?,?,?,?,?,?,?)");
            stmt.setLong(1, intervalo.getColaborador().getUnidade().getCodigo());
            stmt.setLong(2, intervalo.getTipo().getCodigo());
            stmt.setLong(3, intervalo.getColaborador().getCpf());
            if (intervalo.getDataHoraInicio() != null) {
                stmt.setString(4, intervalo.getFonteDataHoraInicio().key());
                stmt.setTimestamp(5, DateUtils.toTimestamp(intervalo.getDataHoraInicio()));
            } else {
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.TIMESTAMP);
            }
            if (intervalo.getDataHoraFim() != null) {
                stmt.setString(6, intervalo.getFonteDataHoraFim().key());
                stmt.setTimestamp(7, DateUtils.toTimestamp(intervalo.getDataHoraFim()));
            } else {
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.TIMESTAMP);
            }
            stmt.setString(8, intervalo.getJustificativaEstouro());
            stmt.setString(9, intervalo.getJustificativaTempoRecomendado());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o intervalo");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public void updateIntervalo(Intervalo intervalo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE INTERVALO SET FONTE_DATA_HORA_INICIO = ?, DATA_HORA_INICIO = ?, " +
                    " FONTE_DATA_HORA_FIM = ?, DATA_HORA_FIM = ?, JUSTIFICATIVA_ESTOURO = ?, JUSTIFICATIVA_TEMPO_RECOMENDADO = ? " +
                    "WHERE CPF_COLABORADOR = ? AND CODIGO = ?;");
            stmt.setString(1, intervalo.getFonteDataHoraInicio().key());
            stmt.setTimestamp(2, DateUtils.toTimestamp(intervalo.getDataHoraInicio()));
            stmt.setString(3, intervalo.getFonteDataHoraFim().key());
            stmt.setTimestamp(4, DateUtils.toTimestamp(intervalo.getDataHoraFim()));
            stmt.setString(5, intervalo.getJustificativaEstouro());
            stmt.setString(6, intervalo.getJustificativaTempoRecomendado());
            stmt.setLong(7, intervalo.getColaborador().getCpf());
            stmt.setLong(8, intervalo.getCodigo());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao finalizar o intervalo");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public boolean insereFinalizacaoIntervalo (Intervalo intervalo, Long codUnidade) throws SQLException{
        Connection conn = null;
        try {
            conn = getConnection();
            Intervalo intervaloEmAberto = getIntervaloAberto(intervalo.getColaborador().getCpf(), intervalo.getTipo());
            if (intervaloEmAberto != null) {
                if(intervalo.getCodigo().equals(intervaloEmAberto.getCodigo())){
                    return finalizaIntervaloEmAberto(intervalo);
                }
            } else {
                intervalo.setDataHoraInicio(null);
                intervalo.setDataHoraFim(new Date(System.currentTimeMillis()));
                Long codigo = insertIntervalo(intervalo, codUnidade, conn);
                if (codigo != null) {
                    return true;
                }
            }
        } finally {
            closeConnection(conn, null, null);
        }
        return false;
    }

    @Override
    public List<Intervalo> getIntervalosColaborador(Long cpf, String codTipo, long limit, long offset) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Intervalo> intervalos = new ArrayList<>();
        try {
//            conn = getConnection();
//            stmt = conn.prepareStatement("SELECT i.codigo as cod_intervalo, it.CODIGO as codigo_tipo_intervalo, i.DATA_HORA_INICIO, i.DATA_HORA_FIM,\n" +
//                    "i.JUSTIFICATIVA_ESTOURO, i.VALIDO, it.nome as nome_tipo_intervalo, it.ICONE, it.TEMPO_RECOMENDADO_MINUTOS, it.TEMPO_ESTOURO_MINUTOS,\n" +
//                    "it.HORARIO_SUGERIDO,i.cod_unidade, it.ativo, ULTIMA_ABERTURA.*,\n" +
//                    "  CASE WHEN I.DATA_HORA_FIM IS NULL AND I.DATA_HORA_INICIO = ULTIMA_ABERTURA.ULTIMO_INICIO THEN\n" +
//                    "  EXTRACT(EPOCH FROM now() - i.DATA_HORA_INICIO)\n" +
//                    "  WHEN I.DATA_HORA_INICIO IS NULL THEN NULL\n" +
//                    "  WHEN I.DATA_HORA_FIM IS NULL AND I.DATA_HORA_INICIO <> ULTIMA_ABERTURA.ULTIMO_INICIO THEN NULL\n" +
//                    "    ELSE EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) END  AS TEMPO_DECORRIDO\n" +
//                    "FROM\n" +
//                    "  INTERVALO I JOIN INTERVALO_TIPO IT ON IT.COD_UNIDADE = I.COD_UNIDADE AND IT.CODIGO = I.COD_TIPO_INTERVALO\n" +
//                    "  JOIN (SELECT COD_UNIDADE, COD_TIPO_INTERVALO AS COD_TIPO_ULTIMO_INICIO, MAX(DATA_HORA_INICIO) AS ULTIMO_INICIO FROM INTERVALO WHERE CPF_COLABORADOR = ? \n" +
//                    "GROUP BY 1,2) AS ULTIMA_ABERTURA ON ULTIMA_ABERTURA.COD_UNIDADE = I.COD_UNIDADE AND ULTIMA_ABERTURA.COD_TIPO_ULTIMO_INICIO = I.COD_TIPO_INTERVALO\n" +
//                    "WHERE I.CPF_COLABORADOR = ? and i.cod_tipo_intervalo::text like ?\n" +
//                    "ORDER BY cod_intervalo DESC " +
//                    "LIMIT ? OFFSET ?;");
//            stmt.setLong(1, cpf);
//            stmt.setLong(2, cpf);
//            stmt.setString(3, codTipo);
//            stmt.setLong(4, limit);
//            stmt.setLong(5, offset);
//            rSet = stmt.executeQuery();
//            while (rSet.next()){
//                intervalos.add(createIntervalo(rSet, conn));
//            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return intervalos;
    }

    @Override
    @NotNull
    public Optional<Long> getVersaoDadosIntervaloByUnidade(@NotNull final Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT VERSAO_DADOS FROM INTERVALO_UNIDADE WHERE COD_UNIDADE = ?");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return Optional.of(rSet.getLong("VERSAO_DADOS"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }

        return Optional.empty();
    }

    @Deprecated
    @Override
    public Long iniciaIntervalo(Long codUnidade, Long cpf, Long codTipo) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            Intervalo intervalo = new Intervalo();
            TipoIntervalo tipoIntervalo = new TipoIntervalo();
            tipoIntervalo.setCodigo(codTipo);
            Colaborador colaborador = new Colaborador();
            colaborador.setCpf(cpf);
            intervalo.setTipo(tipoIntervalo);
            intervalo.setColaborador(colaborador);
            intervalo.setDataHoraInicio(new Date(System.currentTimeMillis()));
            intervalo.setFonteDataHoraInicio(FonteDataHora.SERVIDOR);
            return insertIntervalo(intervalo, codUnidade, conn);
        } finally {
            closeConnection(conn, null, null);
        }
    }

    @Deprecated
    @Override
    public boolean insereFinalizacaoIntervalo (Intervalo intervalo, Long codUnidade) throws SQLException{
        Connection conn = null;
        try {
            conn = getConnection();
            // Seta fontes por questão de compatibilidade.
            intervalo.setFonteDataHoraInicio(FonteDataHora.SERVIDOR);
            intervalo.setFonteDataHoraFim(FonteDataHora.SERVIDOR);
            Intervalo intervaloEmAberto = getIntervaloAberto(intervalo.getColaborador().getCpf(), intervalo.getTipo());
            if (intervaloEmAberto != null) {
                if(intervalo.getCodigo().equals(intervaloEmAberto.getCodigo())){
                    return finalizaIntervaloEmAberto(intervalo);
                }
            } else {
                intervalo.setDataHoraInicio(null);
                intervalo.setDataHoraFim(new Date(System.currentTimeMillis()));
                Long codigo = insertIntervalo(intervalo, codUnidade, conn);
                if (codigo != null) {
                    return true;
                }
            }
        } finally {
            closeConnection(conn, null, null);
        }
        return false;
    }

    @Deprecated
    @Override
    public boolean finalizaIntervaloEmAberto(Intervalo intervalo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE INTERVALO SET DATA_HORA_FIM = ?, FONTE_DATA_HORA_FIM = ?, " +
                    "JUSTIFICATIVA_ESTOURO = ? WHERE CPF_COLABORADOR = ? AND CODIGO = ?;");
            stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setString(2, intervalo.getFonteDataHoraFim().key());
            stmt.setString(3, intervalo.getJustificativaEstouro());
            stmt.setLong(4, intervalo.getColaborador().getCpf());
            stmt.setLong(5, intervalo.getCodigo());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao finalizar o intervalo");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Deprecated
    private Long insertIntervalo(Intervalo intervalo, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO INTERVALO(COD_UNIDADE, COD_TIPO_INTERVALO, CPF_COLABORADOR, " +
                    "DATA_HORA_INICIO, FONTE_DATA_HORA_INICIO, DATA_HORA_FIM, FONTE_DATA_HORA_FIM) VALUES (?,?,?,?,?,?,?) " +
                    "RETURNING CODIGO;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, intervalo.getTipo().getCodigo());
            stmt.setLong(3, intervalo.getColaborador().getCpf());
            if (intervalo.getDataHoraInicio() != null) {
                stmt.setTimestamp(4, DateUtils.toTimestamp(intervalo.getDataHoraInicio()));
                stmt.setString(5, intervalo.getFonteDataHoraInicio().key());
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
                stmt.setNull(5, Types.VARCHAR);
            }
            if (intervalo.getDataHoraFim() != null) {
                stmt.setTimestamp(6, DateUtils.toTimestamp(intervalo.getDataHoraFim()));
                stmt.setString(7, intervalo.getFonteDataHoraInicio().key());
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
                stmt.setNull(7, Types.VARCHAR);
            }
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    private Intervalo createIntervaloAberto(ResultSet rSet) throws SQLException {
        Intervalo intervalo = new Intervalo();
        intervalo.setCodigo(rSet.getLong("CODIGO"));
        intervalo.setDataHoraInicio(rSet.getTimestamp("DATA_HORA_INICIO"));
        intervalo.setValido(rSet.getBoolean("VALIDO"));
        intervalo.setTempoDecorrido(Duration.ofSeconds(DateUtils.secondsBetween(intervalo.getDataHoraInicio().getTime(), System.currentTimeMillis())));
        Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF_COLABORADOR"));
        TipoIntervalo tipoIntervalo = new TipoIntervalo();
        tipoIntervalo.setCodigo(rSet.getLong("COD_TIPO_INTERVALO"));
        intervalo.setTipo(tipoIntervalo);
        intervalo.setColaborador(colaborador);
        String fonteDataHoraInicio = rSet.getString("FONTE_DATA_HORA_INICIO");
//        Setar apenas a fonte do inicio, sendo que não tem como um intervalo em aberto vir com fonte de término
        if (fonteDataHoraInicio != null) {
            intervalo.setFonteDataHoraInicio(FonteDataHora.fromString(fonteDataHoraInicio));
        }
        return intervalo;
    }

    private TipoIntervalo createTipoInvervalo(ResultSet rSet, boolean withCargos, Connection conn) throws SQLException {
        TipoIntervalo tipoIntervalo = new TipoIntervalo();
        tipoIntervalo.setCodigo(rSet.getLong("CODIGO_TIPO_INTERVALO"));
        tipoIntervalo.setNome(rSet.getString("NOME_TIPO_INTERVALO"));
        Unidade unidade = new Unidade();
        unidade.setCodigo(rSet.getLong("COD_UNIDADE"));
        tipoIntervalo.setUnidade(unidade);
        tipoIntervalo.setAtivo(rSet.getBoolean("ATIVO"));
        tipoIntervalo.setHorarioSugerido(rSet.getTime("HORARIO_SUGERIDO"));
        tipoIntervalo.setIcone(Icone.fromString(rSet.getString("ICONE")));
        tipoIntervalo.setTempoLimiteEstouro(Duration.ofMinutes(rSet.getLong("TEMPO_ESTOURO_MINUTOS")));
        tipoIntervalo.setTempoRecomendado(Duration.ofMinutes(rSet.getLong("TEMPO_RECOMENDADO_MINUTOS")));
        if (withCargos) {
            tipoIntervalo.setCargos(getCargosByTipoIntervalo(tipoIntervalo, conn));
        }
        return tipoIntervalo;
    }

    private List<Cargo> getCargosByTipoIntervalo(TipoIntervalo tipoIntervalo, Connection conn) throws SQLException {
        List<Cargo> cargos = new ArrayList<>();
        ResultSet rSet = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT F.* FROM\n" +
                "  INTERVALO_TIPO_CARGO ITC JOIN UNIDADE U ON U.CODIGO = ITC.COD_UNIDADE\n" +
                "JOIN FUNCAO F ON F.cod_emprESA = U.cod_empresa AND F.codigo = ITC.COD_CARGO\n" +
                "WHERE ITC.COD_TIPO_INTERVALO = ? and ITC.COD_UNIDADE = ?");
        stmt.setLong(1, tipoIntervalo.getCodigo());
        stmt.setLong(2, tipoIntervalo.getUnidade().getCodigo());
        rSet = stmt.executeQuery();
        while (rSet.next()) {
            Cargo cargo = new Cargo(rSet.getLong("CODIGO"), rSet.getString("NOME"));
            cargos.add(cargo);
        }
        return cargos;
    }
}