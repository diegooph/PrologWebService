package br.com.zalf.prolog.webservice.interno.suporte;

import br.com.zalf.prolog.webservice.interno.suporte._model.InternalEmpresa;
import br.com.zalf.prolog.webservice.interno.suporte._model.InternalEmpresaMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
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
                        "e.logo_consta_site_comercial as logo_consta_site_comercial " +
                        "from empresa e " +
                        "where e.codigo = ?",
                new InternalEmpresaMapper(),
                codEmpresa));
        if (empresa == null) {
            throw new IllegalStateException("Erro ao buscar empresa com código: " + codEmpresa);
        }
        return empresa;
    }

    public void updateEmpresa(@NotNull final InternalEmpresa empresa) {
        final int updateCount = jdbcTemplate.update(
                "update empresa set " +
                        "nome = ?," +
                        "cod_auxiliar = ?," +
                        "status_ativo = ?," +
                        "logo_consta_site_comercial = ? " +
                        "where codigo = ?",
                empresa.getNome(),
                empresa.getCodAuxiliar(),
                empresa.isStatusAtivo(),
                empresa.isLogoConstaSiteComercial(),
                empresa.getCodigo());
        if (updateCount != 1) {
            throw new IllegalStateException();
        }
    }

    public void updateImagemLogoEmpresa(@NotNull final Long codEmpresa,
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
