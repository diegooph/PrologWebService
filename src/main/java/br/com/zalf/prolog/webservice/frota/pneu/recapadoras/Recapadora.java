package br.com.zalf.prolog.webservice.frota.pneu.recapadoras;

/**
 * Created on 13/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class Recapadora {

    private long codigo;
    private long codEmpresa;
    private String nome;
    private boolean isAtiva;

    public Recapadora() {
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(final long codigo) {
        this.codigo = codigo;
    }

    public long getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(final long codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public boolean isAtiva() {
        return isAtiva;
    }

    public void setAtiva(final boolean ativa) {
        isAtiva = ativa;
    }

    @Override
    public String toString() {
        return "Recapadora{" +
                "codigo=" + codigo +
                ", codEmpresa=" + codEmpresa +
                ", nome='" + nome + '\'' +
                ", isAtiva=" + isAtiva +
                '}';
    }
}
