package br.com.zalf.prolog.webservice.interno.suporte._model;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public final class InternalUnidadeMapper implements RowMapper<InternalUnidade> {

    @NotNull
    @Override
    public InternalUnidade mapRow(@NotNull final ResultSet rs, final int rowNum) throws SQLException {
        final Array pilaresLiberados = rs.getArray("pilares_liberados");
        final OffsetDateTime dataHoraUltimaAtualizacao = rs.getObject("data_hora_ultima_atualizacao",
                                                                      OffsetDateTime.class);
        return InternalUnidade
                .builder()
                .withCodigo(rs.getLong("codigo"))
                .withNome(rs.getString("nome"))
                .withTotalColaboradoresAtivos(rs.getInt("tota_colaboradores_ativos"))
                .withTotalVeiculosAtivos(rs.getInt("tota_veiculos_ativos"))
                .withTimezone(rs.getString("timezone"))
                .withDataHoraCadastro(rs.getObject("data_hora_cadastro", OffsetDateTime.class).toLocalDateTime())
                .withStatusAtivo(rs.getBoolean("status_ativo"))
                .withCodAuxiliar(rs.getString("cod_auxiliar"))
                .withPais(rs.getString("pais"))
                .withEstadoProvincia(rs.getString("estado_provincia"))
                .withCidade(rs.getString("cidade"))
                .withCep(rs.getString("cep"))
                .withEndereco(rs.getString("endereco"))
                .withLatitude(rs.getString("latitude"))
                .withLongitude(rs.getString("longitude"))
                .withCodEmpresa(rs.getLong("cod_empresa"))
                .withNomeEmpresa(rs.getString("nome_empresa"))
                .withCodGrupo(rs.getLong("cod_grupo"))
                .withNomeGrupo(rs.getString("nome_grupo"))
                .withPilaresLiberados((Integer[]) pilaresLiberados.getArray())
                .withDataHoraUltimaAtualizacao(dataHoraUltimaAtualizacao != null
                                                       ? dataHoraUltimaAtualizacao.toLocalDateTime()
                                                       : null)
                .withResponsavelUltimaAtualizacao(rs.getString("responsavel_ultima_atualizacao"))
                .build();
    }
}
