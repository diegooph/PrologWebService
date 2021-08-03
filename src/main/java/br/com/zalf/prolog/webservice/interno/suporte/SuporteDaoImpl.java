package br.com.zalf.prolog.webservice.interno.suporte;

import br.com.zalf.prolog.webservice.interno.suporte._model.InternalEmpresa;
import br.com.zalf.prolog.webservice.interno.suporte._model.InternalEmpresaMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

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
                        "e.logo_consta_site_comercial as logo_consta_site_comercial " +
                        "from empresa e " +
                        "order by e.nome",
                new InternalEmpresaMapper());
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
