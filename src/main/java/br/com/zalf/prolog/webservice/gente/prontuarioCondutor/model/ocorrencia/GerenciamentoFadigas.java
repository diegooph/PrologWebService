package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia;

/**
 * Created on 2019-11-07
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class GerenciamentoFadigas extends Ocorrencia {
    private int celular;
    private int consumoAlimento;
    private int fumando;
    private int oclusao;
    private int semCinto;

    public GerenciamentoFadigas() {
    }

    public int getCelular() {
        return celular;
    }

    public void setCelular(final int celular) {
        this.celular = celular;
    }

    public int getConsumoAlimento() {
        return consumoAlimento;
    }

    public void setConsumoAlimento(final int consumoAlimento) {
        this.consumoAlimento = consumoAlimento;
    }

    public int getFumando() {
        return fumando;
    }

    public void setFumando(final int fumando) {
        this.fumando = fumando;
    }

    public int getOclusao() {
        return oclusao;
    }

    public void setOclusao(final int oclusao) {
        this.oclusao = oclusao;
    }

    public int getSemCinto() {
        return semCinto;
    }

    public void setSemCinto(final int semCinto) {
        this.semCinto = semCinto;
    }
}