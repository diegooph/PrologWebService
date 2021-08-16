package br.com.zalf.prolog.webservice.interno.suporte;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.interno.PrologInternalUser;
import br.com.zalf.prolog.webservice.interno.suporte._model.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class SuporteDaoImpl {
    @NotNull
    private final JdbcTemplate jdbcTemplate;

    public void insertEmpresa(@NotNull final String nomeEmpresa,
                              @Nullable final String urlImagem) {
        final int updateCount = jdbcTemplate.update(
                "insert into empresa(nome, logo_thumbnail_url) values (?, ?)",
                nomeEmpresa,
                urlImagem);
        if (updateCount != 1) {
            throw new IllegalStateException();
        }
    }

    @NotNull
    public List<InternalEmpresa> getTodasEmpresas() {
        return jdbcTemplate.query(
                "select " +
                        "e.codigo as codigo," +
                        "e.nome as nome," +
                        "e.logo_thumbnail_url as logo_thumbnail_url," +
                        "e.data_hora_cadastro as data_hora_cadastro," +
                        "e.cod_auxiliar as cod_auxiliar," +
                        "e.status_ativo as status_ativo," +
                        "e.logo_consta_site_comercial as logo_consta_site_comercial, " +
                        "e.data_hora_ultima_atualizacao as data_hora_ultima_atualizacao, " +
                        "e.responsavel_ultima_atualizacao as responsavel_ultima_atualizacao " +
                        "from empresa e " +
                        "order by e.nome",
                new InternalEmpresaMapper());
    }

    @NotNull
    public InternalEmpresa getEmpresa(@NotNull final Long codEmpresa) {
        final InternalEmpresa empresa = DataAccessUtils.singleResult(jdbcTemplate.query(
                "select " +
                        "e.codigo as codigo," +
                        "e.nome as nome," +
                        "e.logo_thumbnail_url as logo_thumbnail_url," +
                        "e.data_hora_cadastro as data_hora_cadastro," +
                        "e.cod_auxiliar as cod_auxiliar," +
                        "e.status_ativo as status_ativo," +
                        "e.logo_consta_site_comercial as logo_consta_site_comercial, " +
                        "e.data_hora_ultima_atualizacao as data_hora_ultima_atualizacao, " +
                        "e.responsavel_ultima_atualizacao as responsavel_ultima_atualizacao " +
                        "from empresa e " +
                        "where e.codigo = ?",
                new InternalEmpresaMapper(),
                codEmpresa));
        if (empresa == null) {
            throw new IllegalStateException("Erro ao buscar empresa com código: " + codEmpresa);
        }
        return empresa;
    }

    public void updateEmpresa(@NotNull final InternalEmpresa empresa,
                              @NotNull final PrologInternalUser user) {
        final int updateCount = jdbcTemplate.update(
                "update empresa set " +
                        "nome = ?," +
                        "cod_auxiliar = ?," +
                        "status_ativo = ?," +
                        "logo_consta_site_comercial = ?, " +
                        "data_hora_ultima_atualizacao = ?, " +
                        "responsavel_ultima_atualizacao = ? " +
                        "where codigo = ?",
                empresa.getNome(),
                empresa.getCodAuxiliar(),
                empresa.isStatusAtivo(),
                empresa.isLogoConstaSiteComercial(),
                Now.getOffsetDateTimeUtc(),
                user.getUsername(),
                empresa.getCodigo());
        if (updateCount != 1) {
            throw new IllegalStateException();
        }
    }

    public void updateImagemLogoEmpresa(@NotNull final Long codEmpresa,
                                        @NotNull final String urlImagem,
                                        @NotNull final PrologInternalUser user) {
        final int updateCount = jdbcTemplate.update(
                "update empresa set " +
                        "logo_thumbnail_url = ?, " +
                        "data_hora_ultima_atualizacao = ?, " +
                        "responsavel_ultima_atualizacao = ? " +
                        "where codigo = ?",
                urlImagem,
                Now.getOffsetDateTimeUtc(),
                user.getUsername(),
                codEmpresa);
        if (updateCount != 1) {
            throw new IllegalStateException();
        }
    }

    public void insertUnidade(@NotNull final InternalUnidadeInsert unidade) {
        final String sql = "insert into unidade(" +
                "nome," +
                "cod_empresa," +
                "cod_regional," +
                "timezone," +
                "cod_auxiliar," +
                "pais," +
                "estado_provincia," +
                "cidade," +
                "cep," +
                "endereco," +
                "latitude_unidade," +
                "longitude_unidade) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) returning codigo;";
        final Object[] params = new Object[]{
                unidade.getNome(),
                unidade.getCodEmpresa(),
                unidade.getCodGrupo(),
                unidade.getTimezone(),
                unidade.getCodAuxiliar(),
                unidade.getPais(),
                unidade.getEstadoProvincia(),
                unidade.getCidade(),
                unidade.getCep(),
                unidade.getEndereco(),
                unidade.getLatitude(),
                unidade.getLongitude()};

        final Long codUnidade = jdbcTemplate.queryForObject(sql, params, Long.class);
        if (codUnidade == null || codUnidade <= 0) {
            throw new IllegalStateException();
        }

        insertOrUpdatePilaresLiberados(codUnidade, unidade.getPilaresLiberados());
    }

    @NotNull
    public List<InternalUnidade> getTodasUnidades() {
        return jdbcTemplate.query(
                "select " +
                        "u.codigo as codigo," +
                        "u.nome as nome," +
                        "(select count(*) " +
                        "from colaborador c " +
                        "where c.cod_unidade = u.codigo " +
                        "and c.status_ativo) as tota_colaboradores_ativos," +
                        "(select count(*) " +
                        "from veiculo v " +
                        "where v.cod_unidade = u.codigo " +
                        "and v.status_ativo) as tota_veiculos_ativos," +
                        "u.data_hora_cadastro as data_hora_cadastro," +
                        "u.status_ativo as status_ativo," +
                        "u.cod_auxiliar as cod_auxiliar," +
                        "u.timezone as timezone," +
                        "u.pais as pais, " +
                        "u.estado_provincia as estado_provincia, " +
                        "u.cidade as cidade, " +
                        "u.cep as cep, " +
                        "u.endereco as endereco, " +
                        "u.latitude_unidade as latitude, " +
                        "u.longitude_unidade as longitude, " +
                        "e.codigo as cod_empresa, " +
                        "e.nome as nome_empresa, " +
                        "r.codigo as cod_grupo, " +
                        "r.regiao as nome_grupo, " +
                        "(select array_agg(upp.cod_pilar::integer) " +
                        "from unidade_pilar_prolog upp " +
                        "where upp.cod_unidade = u.codigo) as pilares_liberados, " +
                        "u.data_hora_ultima_atualizacao as data_hora_ultima_atualizacao, " +
                        "u.responsavel_ultima_atualizacao as responsavel_ultima_atualizacao " +
                        "from unidade u " +
                        "join empresa e on u.cod_empresa = e.codigo " +
                        "join regional r on u.cod_regional = r.codigo " +
                        "order by u.nome",
                new InternalUnidadeMapper());
    }

    @NotNull
    public InternalUnidade getUnidade(@NotNull final Long codUnidade) {
        final InternalUnidade unidade = DataAccessUtils.singleResult(jdbcTemplate.query(
                "select " +
                        "u.codigo as codigo," +
                        "u.nome as nome," +
                        "(select count(*) " +
                        "from colaborador c " +
                        "where c.cod_unidade = u.codigo " +
                        "and c.status_ativo) as tota_colaboradores_ativos," +
                        "(select count(*) " +
                        "from veiculo v " +
                        "where v.cod_unidade = u.codigo " +
                        "and v.status_ativo) as tota_veiculos_ativos," +
                        "u.data_hora_cadastro as data_hora_cadastro," +
                        "u.status_ativo as status_ativo," +
                        "u.cod_auxiliar as cod_auxiliar," +
                        "u.timezone as timezone," +
                        "u.pais as pais, " +
                        "u.estado_provincia as estado_provincia, " +
                        "u.cidade as cidade, " +
                        "u.cep as cep, " +
                        "u.endereco as endereco, " +
                        "u.latitude_unidade as latitude, " +
                        "u.longitude_unidade as longitude, " +
                        "e.codigo as cod_empresa, " +
                        "e.nome as nome_empresa, " +
                        "r.codigo as cod_grupo, " +
                        "r.regiao as nome_grupo, " +
                        "(select array_agg(upp.cod_pilar::integer) " +
                        "from unidade_pilar_prolog upp " +
                        "where upp.cod_unidade = u.codigo) as pilares_liberados, " +
                        "u.data_hora_ultima_atualizacao as data_hora_ultima_atualizacao, " +
                        "u.responsavel_ultima_atualizacao as responsavel_ultima_atualizacao " +
                        "from unidade u " +
                        "join empresa e on u.cod_empresa = e.codigo " +
                        "join regional r on u.cod_regional = r.codigo " +
                        "where u.codigo = ?",
                new InternalUnidadeMapper(),
                codUnidade));
        if (unidade == null) {
            throw new IllegalStateException("Erro ao buscar unidade com código: " + codUnidade);
        }
        return unidade;
    }

    public void updateUnidade(@NotNull final InternalUnidade unidade,
                              @NotNull final PrologInternalUser user) {
        final int updateCount = jdbcTemplate.update(
                "update unidade set " +
                        "nome = ?," +
                        "cod_regional = ?," +
                        "timezone = ?, " +
                        "cod_auxiliar = ?, " +
                        "pais = ?, " +
                        "estado_provincia = ?, " +
                        "cidade = ?, " +
                        "cep = ?, " +
                        "endereco = ?, " +
                        "latitude_unidade = ?, " +
                        "longitude_unidade = ?, " +
                        "data_hora_ultima_atualizacao = ?, " +
                        "responsavel_ultima_atualizacao = ? " +
                        "where codigo = ?",
                unidade.getNome(),
                unidade.getCodGrupo(),
                unidade.getTimezone(),
                unidade.getCodAuxiliar(),
                unidade.getPais(),
                unidade.getEstadoProvincia(),
                unidade.getCidade(),
                unidade.getCep(),
                unidade.getEndereco(),
                unidade.getLatitude(),
                unidade.getLongitude(),
                Now.getOffsetDateTimeUtc(),
                user.getUsername(),
                unidade.getCodigo());
        if (updateCount != 1) {
            throw new IllegalStateException();
        }

        insertOrUpdatePilaresLiberados(unidade.getCodigo(), unidade.getPilaresLiberados());
    }

    private void insertOrUpdatePilaresLiberados(@NotNull final Long codUnidade,
                                                @NotNull final Integer[] pilaresLiberados) {
        jdbcTemplate.update("delete from unidade_pilar_prolog where cod_unidade = ?", codUnidade);

        final int[] insertsCount = jdbcTemplate.batchUpdate(
                "insert into unidade_pilar_prolog(cod_unidade, cod_pilar) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(@NotNull final PreparedStatement ps, final int i)
                            throws SQLException {
                        ps.setLong(1, codUnidade);
                        ps.setLong(2, pilaresLiberados[i]);
                    }

                    @Override
                    public int getBatchSize() {
                        return pilaresLiberados.length;
                    }
                });

        final boolean tudoOk = IntStream
                .of(insertsCount)
                .allMatch(result -> result == 1);
        if (!tudoOk) {
            throw new IllegalStateException();
        }
    }
}
