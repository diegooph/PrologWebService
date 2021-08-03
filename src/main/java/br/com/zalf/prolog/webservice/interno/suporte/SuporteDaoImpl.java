package br.com.zalf.prolog.webservice.interno.suporte;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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

    public void alteraImagemLogoEmpresa(@NotNull final Long codEmpresa,
                                        @NotNull final String urlImagem) {
        final int updateCount = jdbcTemplate.update(
                "update empresa set logo_thumbnail_url = ? where codigo = ?",
                urlImagem,
                codEmpresa);
        if (updateCount != 1) {
            throw new IllegalStateException();
        }
    }
}
