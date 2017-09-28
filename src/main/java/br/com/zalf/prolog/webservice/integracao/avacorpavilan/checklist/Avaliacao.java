package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

/**
 * Created on 9/28/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class Avaliacao {
    protected String tipo;
    protected String usuario;
    protected String data;
    protected Long codigo;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }
}
