package br.com.zalf.prolog.webservice.interno.suporte;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class SuporteDaoImpl {
    @NotNull
    private final JdbcTemplate jdbcTemplate;

    public void alteraImagemLogoEmpresa(@NotNull final Long codEmpresa,
                                        @NotNull final String urlImagem) {
        jdbcTemplate.update("update empresa set logo_thumbnail_url = ? where codigo = ?", urlImagem, codEmpresa);
    }
}
