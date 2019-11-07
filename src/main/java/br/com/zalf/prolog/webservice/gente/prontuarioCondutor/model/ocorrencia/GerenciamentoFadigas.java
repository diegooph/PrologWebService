package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia;

/**
 * Created on 2019-11-07
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class GerenciamentoFadigas extends Ocorrencia {
    private double celular;
    private double consumoAlimento;
    private double fumando;
    private double oclusao;
    private double semCinto;

    public GerenciamentoFadigas() {
    }

    public double getCelular() {
        return celular;
    }

    public void setCelular(final double celular) {
        this.celular = celular;
    }

    public double getConsumoAlimento() {
        return consumoAlimento;
    }

    public void setConsumoAlimento(final double consumoAlimento) {
        this.consumoAlimento = consumoAlimento;
    }

    public double getFumando() {
        return fumando;
    }

    public void setFumando(final double fumando) {
        this.fumando = fumando;
    }

    public double getOclusao() {
        return oclusao;
    }

    public void setOclusao(final double oclusao) {
        this.oclusao = oclusao;
    }

    public double getSemCinto() {
        return semCinto;
    }

    public void setSemCinto(final double semCinto) {
        this.semCinto = semCinto;
    }
}