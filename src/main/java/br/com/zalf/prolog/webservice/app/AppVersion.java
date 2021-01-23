package br.com.zalf.prolog.webservice.app;

import java.util.Date;

public class AppVersion {
    private int versionCode;
    private String versionName;
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
