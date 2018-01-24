package br.com.zalf.prolog.webservice.dashboard.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * Classe abstrata que contém os métodos genéricos a todos os <br>Componentes</br>.
 * <p>
 * Um componente é qualquer tipo de informação que pode ser extratificada através de relatórios
 * dos dados persistentes no banco de dados. A <br>Dashboard</br> será composta de um conjunto de
 * componentes, estes serão dispostos de forma a serem um resumo de um conjunto maior de informações.
 * <p>
 * Todos os componentes devem estender de <code>{@link DashboardComponent}</code>, assim conterão as
 * informações básicas para ser exibido.
 * <p>
 * Cada componente poderá conter informações extras para exibir as informações personalizadas
 * para o usuário da aplicação.
 */
public abstract class DashboardComponent {
    @NotNull
    private String titulo;
    @Nullable
    private String subtitulo;
    @NotNull
    private String descricao;
    @NotNull
    private String urlEndpointDados;
    @NotNull
    private Integer codTipoComponente;
    private int qtdBlocosHorizontais;
    private int qtdBlocosVerticais;
    private int ordemExibicao;

    public DashboardComponent(@NotNull String titulo,
                              @Nullable String subtitulo,
                              @NotNull String descricao,
                              @NotNull String urlEndpointDados,
                              @NotNull Integer codTipoComponente,
                              int qtdBlocosHorizontais,
                              int qtdBlocosVerticais,
                              int ordemExibicao) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.descricao = descricao;
        this.urlEndpointDados = urlEndpointDados;
        this.codTipoComponente = codTipoComponente;
        this.qtdBlocosHorizontais = qtdBlocosHorizontais;
        this.qtdBlocosVerticais = qtdBlocosVerticais;
        this.ordemExibicao = ordemExibicao;
    }

    @NotNull
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(@NotNull String titulo) {
        this.titulo = titulo;
    }

    @Nullable
    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(@Nullable String subtitulo) {
        this.subtitulo = subtitulo;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NotNull String descricao) {
        this.descricao = descricao;
    }

    @NotNull
    public String getUrlEndpointDados() {
        return urlEndpointDados;
    }

    public void setUrlEndpointDados(@NotNull String urlEndpointDados) {
        this.urlEndpointDados = urlEndpointDados;
    }

    @NotNull
    public Integer getCodTipoComponente() {
        return codTipoComponente;
    }

    public void setCodTipoComponente(@NotNull Integer codTipoComponente) {
        this.codTipoComponente = codTipoComponente;
    }

    public int getQtdBlocosHorizontais() {
        return qtdBlocosHorizontais;
    }

    public void setQtdBlocosHorizontais(int qtdBlocosHorizontais) {
        this.qtdBlocosHorizontais = qtdBlocosHorizontais;
    }

    public int getQtdBlocosVerticais() {
        return qtdBlocosVerticais;
    }

    public void setQtdBlocosVerticais(int qtdBlocosVerticais) {
        this.qtdBlocosVerticais = qtdBlocosVerticais;
    }

    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(int ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }
}
