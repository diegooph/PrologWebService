package br.com.zalf.prolog.commons.login;

import java.util.Date;

/**
 * Created by luiz on 1/4/16.
 * Classe utilizada para verificar a versão do app no botão "Atualizar"
 */
public class AppVersion {
    /**
     * Código da versão, única e sequencial
     */
    private int versionCode;
    /**
     * Nome da versão exibida
     */
    private String versionName;
    /**
     * Data em que foi liberada a atualização na loja
     */
    private Date dataLiberacao;

    public AppVersion() {
    }

    public AppVersion(int versionCode, String versionName, Date dataLiberacao) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.dataLiberacao = dataLiberacao;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Date getDataLiberacao() {
        return dataLiberacao;
    }

    public void setDataLiberacao(Date dataLiberacao) {
        this.dataLiberacao = dataLiberacao;
    }
}
