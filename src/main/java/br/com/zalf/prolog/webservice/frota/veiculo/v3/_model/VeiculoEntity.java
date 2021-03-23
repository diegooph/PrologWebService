package br.com.zalf.prolog.webservice.frota.veiculo.v3._model;

import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "veiculo")
public class VeiculoEntity {
    @Column(name = "cod_eixos", nullable = false, columnDefinition = "bigint default 1")
    private final Long codEixos = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_unidade_cadastro", nullable = false)
    private Long codUnidadeCadastro;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @Column(name = "cod_empresa", nullable = false)
    private Long codEmpresa;
    @Column(name = "placa", length = 7, nullable = false)
    private String placa;
    @Column(name = "identificador_frota", length = 15)
    private String identificadorFrota;
    @Column(name = "km", nullable = false)
    private Long km;
    @Column(name = "status_ativo", nullable = false)
    private boolean statusAtivo;
    @Column(name = "cod_diagrama", nullable = false)
    private Long codDiagrama;
    @Column(name = "cod_tipo", nullable = false)
    private Long codTipo;
    @Column(name = "cod_modelo", nullable = false)
    private Long codModelo;
    @Column(name = "data_hora_cadastro", nullable = false, columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime dataHoraCadatro;
    @Column(name = "foi_editado", nullable = false, columnDefinition = "boolean default false")
    private boolean foiEditado;
    @Column(name = "motorizado", nullable = false)
    private boolean motorizado;
    @Column(name = "possui_hubodometro", nullable = false, columnDefinition = "boolean default false")
    private boolean possuiHobodometro;
    @Enumerated(EnumType.STRING)
    @Column(name = "origem_cadastro", nullable = false)
    private OrigemAcaoEnum origemCadastro;
}
