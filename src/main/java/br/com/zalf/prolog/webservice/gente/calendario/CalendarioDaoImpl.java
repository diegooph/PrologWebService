package br.com.zalf.prolog.webservice.gente.calendario;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Equipe;
import br.com.zalf.prolog.webservice.gente.empresa.EmpresaDao;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CalendarioDaoImpl extends DatabaseConnection implements CalendarioDao {
    private static final String TAG = CalendarioDaoImpl.class.getSimpleName();

    /**
     * Busca dos eventos, foi fracionada a busca em 5 partes, sendo elas:
     * 1 - Busca os eventos exclusivos para uma unidade + função + equipe
     * 2 - Busca os eventos exclusivos para uma unidade + função, independente da equipe
     * 3 - Busca os eventos exclusivos para uma unidade + equipe, independente da função
     * 4 - Busca os eventos exclusivos para uma unidade, independente da função e da equipe
     * 5 - Faz um union com todos os resultados
     */
    private static final String BUSCA_EVENTOS = "SELECT CAL.CODIGO, CAL.DESCRICAO, CAL.DATA AT TIME ZONE ? AS DATA, CAL.LOCAL FROM "
            + "COLABORADOR C JOIN CALENDARIO CAL ON "
            + "CAL.COD_UNIDADE = C.COD_UNIDADE "
            + "AND CAL.COD_FUNCAO = C.COD_FUNCAO "
            + "AND CAL.COD_EQUIPE = C.COD_EQUIPE "
            + "WHERE C.CPF=? "
            + "UNION "
            + "SELECT CAL.CODIGO, CAL.DESCRICAO, CAL.DATA AT TIME ZONE ? AS DATA, CAL.LOCAL FROM "
            + "COLABORADOR C JOIN CALENDARIO CAL ON "
            + "CAL.COD_UNIDADE = C.COD_UNIDADE "
            + "AND CAL.COD_FUNCAO = C.COD_FUNCAO "
            + "AND CAL.COD_EQUIPE IS NULL "
            + "WHERE C.CPF=? "
            + "UNION "
            + "SELECT CAL.CODIGO, CAL.DESCRICAO, CAL.DATA AT TIME ZONE ? AS DATA, CAL.LOCAL FROM "
            + "COLABORADOR C JOIN CALENDARIO CAL ON "
            + "CAL.COD_UNIDADE = C.COD_UNIDADE "
            + "AND CAL.COD_FUNCAO IS NULL "
            + "AND CAL.COD_EQUIPE = C.COD_EQUIPE "
            + "WHERE C.CPF=? "
            + "UNION "
            + "SELECT CAL.CODIGO, CAL.DESCRICAO, CAL.DATA AT TIME ZONE ? AS DATA, CAL.LOCAL FROM "
            + "COLABORADOR C JOIN CALENDARIO CAL ON "
            + "CAL.COD_UNIDADE = C.COD_UNIDADE "
            + "AND CAL.COD_FUNCAO IS NULL "
            + "AND CAL.COD_EQUIPE IS NULL "
            + "WHERE C.CPF=? ";

    public CalendarioDaoImpl() {

    }

    @Override
    public List<Evento> getEventosByCpf(final Long cpf) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Evento> listEvento = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_EVENTOS);
            final String zoneId = TimeZoneManager.getZoneIdForCpf(cpf, conn).getId();
            stmt.setString(1, zoneId);
            stmt.setLong(2, cpf);
            stmt.setString(3, zoneId);
            stmt.setLong(4, cpf);
            stmt.setString(5, zoneId);
            stmt.setLong(6, cpf);
            stmt.setString(7, zoneId);
            stmt.setLong(8, cpf);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Evento evento = new Evento();
                evento.setData(rSet.getObject("DATA", LocalDateTime.class));
                evento.setDescricao(rSet.getString("DESCRICAO"));
                evento.setCodigo(rSet.getLong("CODIGO"));
                evento.setLocal(rSet.getString("LOCAL"));
                listEvento.add(evento);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return listEvento;
    }

    @Override
    @Deprecated
    public List<Evento> getAll(final long dataInicial,
                               final long dataFinal,
                               final Long codEmpresa,
                               final String codUnidade,
                               final String nomeEquipe,
                               final String codFuncao) throws SQLException {
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        final List<Evento> eventos = new ArrayList<>();
        final EmpresaDao empresaDao = Injection.provideEmpresaDao();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "  (SELECT c.data, c.codigo, c.descricao, c.local, coalesce(c.cod_funcao, c.cod_funcao, -1) as cod_funcao, " +
                    "     f.nome as funcao, " +
                    "     coalesce(c.cod_unidade, c.cod_unidade, -1) as cod_unidade, u.nome as unidade, " +
                    "     coalesce(c.cod_equipe, c.cod_equipe, -1) as cod_equipe, eq.nome as equipe " +
                    "   FROM calendario c " +
                    "     join unidade u on u.codigo = c.cod_unidade " +
                    "     join empresa e on e.codigo = u.cod_empresa " +
                    "     left join funcao f on f.codigo = c.cod_funcao " +
                    "     left join equipe eq on eq.cod_unidade = c.cod_unidade and eq.codigo = c.cod_equipe " +
                    "   WHERE E.CODIGO = ? " +
                    "         AND C.data::DATE BETWEEN (? AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(c.cod_unidade))) " +
                    "         and (? AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(c.cod_unidade)))) as f " +
                    "WHERE F.cod_unidade::TEXT LIKE ? " +
                    "      AND F.cod_equipe::TEXT LIKE ? " +
                    "      AND F.cod_funcao::TEXT LIKE ?;");
            stmt.setLong(1, codEmpresa);
            stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
            stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
            stmt.setString(4, codUnidade);
            if (nomeEquipe.equals("%")) {
                stmt.setString(5, "%");
            } else {
                stmt.setString(5,
                        String.valueOf(empresaDao.getCodEquipeByCodUnidadeByNome(Long.parseLong(codUnidade), nomeEquipe)));
            }
            stmt.setString(6, codFuncao);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Evento evento = new Evento();
                evento.setCodigo(rSet.getLong("CODIGO"));
                evento.setData(rSet.getObject("data", LocalDateTime.class));
                evento.setDescricao(rSet.getString("descricao"));
                evento.setLocal(rSet.getString("local"));

                final Unidade unidade = new Unidade();
                unidade.setCodigo(rSet.getLong("cod_unidade"));
                unidade.setNome(rSet.getString("unidade"));
                evento.setUnidade(unidade);

                final Cargo cargoTreinamento = new Cargo();
                cargoTreinamento.setCodigo(rSet.getLong("cod_funcao"));
                if (cargoTreinamento.getCodigo() == -1) {
                    cargoTreinamento.setNome("Todas");
                } else {
                    cargoTreinamento.setNome(rSet.getString("funcao"));
                }
                evento.setFuncao(cargoTreinamento);

                final Equipe equipeTreinamento = new Equipe();
                equipeTreinamento.setCodigo(rSet.getLong("cod_equipe"));
                if (equipeTreinamento.getCodigo() == -1) {
                    equipeTreinamento.setNome("Todas");
                } else {
                    equipeTreinamento.setNome(rSet.getString("equipe"));
                }
                evento.setEquipe(equipeTreinamento);
                eventos.add(evento);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return eventos;
    }

    @Override
    @Deprecated
    public boolean delete(final Long codUnidade, final Long codEvento) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM calendario WHERE cod_unidade = ? AND codigo = ?");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codEvento);
            final int count = stmt.executeUpdate();
            if (count > 0) {
                return true;
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return false;
    }

    @Override
    @Deprecated
    public AbstractResponse insert(final Evento evento,
                                   final String codUnidade,
                                   final String codFuncao,
                                   final String nomeEquipe) throws
            SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final EmpresaDao empresaDao = Injection.provideEmpresaDao();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO calendario(data, descricao, cod_unidade, cod_funcao, " +
                    "cod_equipe, local) " +
                    "VALUES (?,?,?,?,?,?) returning codigo");
            stmt.setObject(1, evento.getData().atZone(ZoneId.systemDefault()).toOffsetDateTime());
            stmt.setString(2, evento.getDescricao());
            stmt.setLong(3, Long.parseLong(codUnidade));

            if (codFuncao.equals("%")) {
                stmt.setNull(4, Types.BIGINT);
            } else {
                stmt.setLong(4, Long.parseLong(codFuncao));
            }

            if (nomeEquipe.equals("%")) {
                stmt.setNull(5, Types.BIGINT);
            } else {
                stmt.setLong(5, empresaDao.getCodEquipeByCodUnidadeByNome(Long.parseLong(codUnidade), nomeEquipe));
            }
            stmt.setString(6, evento.getLocal());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return ResponseWithCod.ok("Evento inserido com sucesso", rSet.getLong("codigo"));
            } else {
                return Response.error("Erro ao inserir o evento");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    @Deprecated
    public boolean update(final Evento evento,
                          final String codUnidade,
                          final String codFuncao,
                          final String nomeEquipe) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        final EmpresaDao empresaDao = Injection.provideEmpresaDao();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE CALENDARIO SET DATA = ?, DESCRICAO = ?, " +
                    " COD_UNIDADE = ?, COD_FUNCAO = ?, COD_EQUIPE = ?, LOCAL = ? WHERE " +
                    " CODIGO = ? AND COD_UNIDADE = ? ");
            stmt.setObject(1, evento.getData().atZone(ZoneId.systemDefault()).toOffsetDateTime());
            stmt.setString(2, evento.getDescricao());
            stmt.setLong(3, Long.parseLong(codUnidade));
            if (codFuncao.equals("%")) {
                stmt.setNull(4, Types.BIGINT);
            } else {
                stmt.setLong(4, Long.parseLong(codFuncao));
            }

            if (nomeEquipe.equals("%")) {
                stmt.setNull(5, Types.BIGINT);
            } else {
                stmt.setLong(5, empresaDao.getCodEquipeByCodUnidadeByNome(Long.parseLong(codUnidade), nomeEquipe));
            }
            stmt.setString(6, evento.getLocal());
            stmt.setLong(7, evento.getCodigo());
            stmt.setLong(8, Long.parseLong(codUnidade));
            final int count = stmt.executeUpdate();
            return count > 0;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

}