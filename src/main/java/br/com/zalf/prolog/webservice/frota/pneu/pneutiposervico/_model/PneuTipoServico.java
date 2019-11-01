package br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model;

/**
 * Created on 24/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuTipoServico {

    private Long codigo;
    private Long codEmpresa;
    private String nome;
    private boolean statusAtivo;
    private boolean editavel;
    private boolean incrementaVida;

    public PneuTipoServico() {
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(final Long codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public boolean isStatusAtivo() {
        return statusAtivo;
    }

    public void setStatusAtivo(final boolean status) {
        this.statusAtivo = status;
    }

    public boolean isEditavel() {
        return editavel;
    }

    public void setEditavel(final boolean editavel) {
        this.editavel = editavel;
    }

    public boolean isIncrementaVida() {
        return incrementaVida;
    }

    public void setIncrementaVida(boolean incrementaVida) {
        this.incrementaVida = incrementaVida;
    }

    @Override
    public String toString() {
        return "PneuTipoServico{" +
                "codigo=" + codigo +
                ", codEmpresa=" + codEmpresa +
                ", nome='" + nome + '\'' +
                ", statusAtivo=" + statusAtivo +
                ", editavel=" + editavel +
                ", incrementaVida=" + incrementaVida +
                '}';
    }
}
