package br.com.zalf.prolog.webservice.frota.veiculo.model;

/**
 * Created by jean on 25/05/16.
 */
public class TipoVeiculo {

    private Long codigo;
    private String nome;

    public TipoVeiculo(long codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public TipoVeiculo() {
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof TipoVeiculo))
            return false;

        if (obj == this)
            return true;

        final TipoVeiculo tipoVeiculo = (TipoVeiculo) obj;
        return !(codigo == null || tipoVeiculo.codigo == null) && codigo.equals(tipoVeiculo.codigo);
    }

    @Override
    public String toString() {
        return "TipoVeiculo{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                '}';
    }
}
