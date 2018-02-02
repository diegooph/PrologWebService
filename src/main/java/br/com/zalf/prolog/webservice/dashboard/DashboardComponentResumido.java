package br.com.zalf.prolog.webservice.dashboard;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DashboardComponentResumido {
    private Integer codigoComponente;
    private String titulo;
    private String subtitulo;
    private String descricao;
    private String urlEndpointDados;

    private int codPilarProLog;
    private int qtdBlocosHorizontais;
    private int qtdBlocosVerticais;
    private int ordem;

    public DashboardComponentResumido() {

    }

    public Integer getCodigoComponente() {
        return codigoComponente;
    }

    public void setCodigoComponente(Integer codigoComponente) {
        this.codigoComponente = codigoComponente;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrlEndpointDados() {
        return urlEndpointDados;
    }

    public void setUrlEndpointDados(String urlEndpointDados) {
        this.urlEndpointDados = urlEndpointDados;
    }

    public int getCodPilarProLog() {
        return codPilarProLog;
    }

    public void setCodPilarProLog(int codPilarProLog) {
        this.codPilarProLog = codPilarProLog;
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

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }
}