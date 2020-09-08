package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.data;

/**
 * Created on 10/16/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FilialUnidadeAvilanProLog {
    private int codFilialAvilan;
    private int codUnidadeAvilan;
    private Long codUnidadeProLog;

    public int getCodFilialAvilan() {
        return codFilialAvilan;
    }

    public void setCodFilialAvilan(int codFilialAvilan) {
        this.codFilialAvilan = codFilialAvilan;
    }

    public int getCodUnidadeAvilan() {
        return codUnidadeAvilan;
    }

    public void setCodUnidadeAvilan(int codUnidadeAvilan) {
        this.codUnidadeAvilan = codUnidadeAvilan;
    }

    public Long getCodUnidadeProLog() {
        return codUnidadeProLog;
    }

    public void setCodUnidadeProLog(Long codUnidadeProLog) {
        this.codUnidadeProLog = codUnidadeProLog;
    }
}