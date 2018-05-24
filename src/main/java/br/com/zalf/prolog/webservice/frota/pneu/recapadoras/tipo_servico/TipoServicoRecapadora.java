package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico;

/**
 * Created on 24/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TipoServicoRecapadora {

    private Long codigo;
    private Long codEmpresa;
    private String nome;
    private boolean status;
    private boolean editavel;

    public TipoServicoRecapadora() {
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(final boolean status) {
        this.status = status;
    }

    public boolean isEditavel() {
        return editavel;
    }

    public void setEditavel(final boolean editavel) {
        this.editavel = editavel;
    }

    @Override
    public String toString() {
        return "TipoServicoRecapadora{" +
                "codigo=" + codigo +
                ", codEmpresa=" + codEmpresa +
                ", nome='" + nome + '\'' +
                ", status=" + status +
                ", editavel=" + editavel +
                '}';
    }
}
