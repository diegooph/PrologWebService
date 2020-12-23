package br.com.zalf.prolog.webservice.frota.veiculo.model;

/**
 * Created by jean on 25/05/16.
 */
public class TipoVeiculo {
    private Long codEmpresa;
    private Long codDiagrama;
    private Long codigo;
    private String nome;
    private String codAuxiliar;
    private boolean motorizado;

    public TipoVeiculo(final Long codEmpresa,
                       final Long codDiagrama,
                       final Long codigo,
                       final String nome,
                       final String codAuxiliar) {
        this.codEmpresa = codEmpresa;
        this.codDiagrama = codDiagrama;
        this.codigo = codigo;
        this.nome = nome;
        this.codAuxiliar = codAuxiliar;
    }

    public TipoVeiculo(final Long codEmpresa,
                       final Long codDiagrama,
                       final Long codigo,
                       final String nome,
                       final String codAuxiliar,
                       final boolean motorizado) {
        this.codEmpresa = codEmpresa;
        this.codDiagrama = codDiagrama;
        this.codigo = codigo;
        this.nome = nome;
        this.codAuxiliar = codAuxiliar;
        this.motorizado = motorizado;
    }

    public TipoVeiculo() {

    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof TipoVeiculo)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        final TipoVeiculo tipoVeiculo = (TipoVeiculo) obj;
        return !(codigo == null || tipoVeiculo.codigo == null) && codigo.equals(tipoVeiculo.codigo);
    }

    public Long getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(final Long codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public Long getCodDiagrama() {
        return codDiagrama;
    }

    public void setCodDiagrama(final Long codDiagrama) {
        this.codDiagrama = codDiagrama;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public String getCodAuxiliar() { return codAuxiliar; }

    public void setCodAuxiliar(final String codAuxiliar) { this.codAuxiliar = codAuxiliar; }

    public boolean isMotorizado() {
        return motorizado;
    }

    public void setMotorizado(final boolean motorizado) {
        this.motorizado = motorizado;
    }
}