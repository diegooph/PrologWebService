package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.data;

/**
 * Created on 10/11/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TipoVeiculoAvilanProLog {
    private String codigoAvilan;
    private String descricao;
    private Long codProLog;

    public String getCodigoAvilan() {
        return codigoAvilan;
    }

    public void setCodigoAvilan(String codigoAvilan) {
        this.codigoAvilan = codigoAvilan;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getCodProLog() {
        return codProLog;
    }

    public void setCodProLog(Long codProLog) {
        this.codProLog = codProLog;
    }
}