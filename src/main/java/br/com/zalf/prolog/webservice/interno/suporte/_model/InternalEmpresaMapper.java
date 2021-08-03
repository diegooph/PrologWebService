package br.com.zalf.prolog.webservice.interno.suporte._model;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public final class InternalEmpresaMapper implements RowMapper<InternalEmpresa> {

    @NotNull
    @Override
    public InternalEmpresa mapRow(@NotNull final ResultSet rs, final int rowNum) throws SQLException {
        final OffsetDateTime dataHoraCadastro = rs.getObject("data_hora_cadastro", OffsetDateTime.class);
        return InternalEmpresa
                .builder()
                .withCodigo(rs.getLong("codigo"))
                .withNome(rs.getString("nome"))
                .withLogoThumbnailUrl(rs.getString("logo_thumbnail_url"))
                .withDataHoraCadastro(dataHoraCadastro != null ? dataHoraCadastro.toLocalDateTime() : null)
                .withCodAuxiliar(rs.getString("cod_auxiliar"))
                .withStatusAtivo(rs.getBoolean("status_ativo"))
                .withLogoConstaSiteComercial(rs.getBoolean("logo_consta_site_comercial"))
                .build();
    }
}
