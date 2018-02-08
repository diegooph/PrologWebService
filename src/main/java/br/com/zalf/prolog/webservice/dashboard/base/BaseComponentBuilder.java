package br.com.zalf.prolog.webservice.dashboard.base;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class BaseComponentBuilder implements DashboardComponentBuilder {
    @NotNull
    protected Integer codigo;
    @NotNull
    protected String titulo;
    @Nullable
    protected String subtitulo;
    @NotNull
    protected String descricao;
    @NotNull
    protected String urlEndpointDados;
    @NotNull
    protected Integer codTipoComponente;
    protected int qtdBlocosHorizontais;
    protected int qtdBlocosVerticais;
    protected int ordemExibicao;

    @Override
    public DashboardComponentBuilder withCodigo(@NotNull Integer codigo) {
        this.codigo = codigo;
        return this;
    }

    @Override
    public DashboardComponentBuilder withTitulo(@NotNull String titulo) {
        this.titulo = titulo;
        return this;
    }

    @Override
    public DashboardComponentBuilder withSubtitulo(@Nullable String subtitulo) {
        this.subtitulo = subtitulo;
        return this;
    }

    @Override
    public DashboardComponentBuilder withDescricao(@NotNull String descricao) {
        this.descricao = descricao;
        return this;
    }

    @Override
    public DashboardComponentBuilder withUrlEndpointDados(@NotNull String urlEndpointDados) {
        this.urlEndpointDados = urlEndpointDados;
        return this;
    }

    @Override
    public DashboardComponentBuilder withCodTipoComponente(@NotNull Integer codTipoComponente) {
        this.codTipoComponente = codTipoComponente;
        return this;
    }

    @Override
    public DashboardComponentBuilder withQtdBlocosHorizontais(int qtdBlocosHorizontais) {
        this.qtdBlocosHorizontais = qtdBlocosHorizontais;
        return this;
    }

    @Override
    public DashboardComponentBuilder withQtdBlocosVerticais(int qtdBlocosVerticais) {
        this.qtdBlocosVerticais = qtdBlocosVerticais;
        return this;
    }

    @Override
    public DashboardComponentBuilder withOrdemExibicao(int ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
        return this;
    }

    protected void ensureNotNullValues() {
        Preconditions.checkNotNull(codigo, "codigo deve ser instanciado com 'withCodigo'");
        Preconditions.checkNotNull(titulo, "titulo deve ser instanciada com 'withTitulo'");
        Preconditions.checkNotNull(descricao, "descricao deve ser instanciada com 'withDescricao'");
        Preconditions.checkNotNull(urlEndpointDados, "urlEndpointDados deve ser instanciada com 'withUrlEndpointDados'");
        Preconditions.checkNotNull(codTipoComponente, "codTipoComponente deve ser instanciada com 'withCodTipoComponente'");
    }
}