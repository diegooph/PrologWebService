package br.com.zalf.prolog.webservice.v3.frota.veiculo.tipoveiculo._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "veiculo_tipo")
public class TipoVeiculoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_empresa", nullable = false)
    private short codEmpresa;
    @Column(name = "nome", nullable = false)
    private String nome;
    @Column(name = "status_ativo", nullable = false)
    private boolean statusAtivo;
    @Column(name = "cod_diagrama", nullable = false)
    private Short codDiagrama;
    @Column(name = "cod_auxiliar")
    private String codAuxiliar;
}
