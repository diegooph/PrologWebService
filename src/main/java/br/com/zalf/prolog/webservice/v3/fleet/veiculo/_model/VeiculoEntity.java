package br.com.zalf.prolog.webservice.v3.fleet.veiculo._model;

import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.acoplamento._model.AcoplamentoAtualEntity;
import br.com.zalf.prolog.webservice.v3.fleet.acoplamento._model.AcoplamentoProcessoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo.diagrama._model.DiagramaEntity;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo.modelo._model.ModeloVeiculoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo.tipoveiculo._model.TipoVeiculoEntity;
import br.com.zalf.prolog.webservice.v3.general.branch._model.UnidadeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Optional;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_unidade_cadastro", referencedColumnName = "codigo")
    private UnidadeEntity unidadeEntityCadastro;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_unidade", referencedColumnName = "codigo")
    private UnidadeEntity unidadeEntity;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_diagrama", referencedColumnName = "codigo")
    private DiagramaEntity diagramaEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_tipo", referencedColumnName = "codigo")
    private TipoVeiculoEntity tipoVeiculoEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_modelo", referencedColumnName = "codigo")
    private ModeloVeiculoEntity modeloVeiculoEntity;
    @Column(name = "data_hora_cadastro", nullable = false, columnDefinition = "timestamp with time zone default now()")
    private OffsetDateTime dataHoraCadatro;
    @Column(name = "foi_editado", nullable = false, columnDefinition = "boolean default false")
    private boolean foiEditado;
    @Column(name = "motorizado", nullable = false)
    private boolean motorizado;
    @Column(name = "possui_hubodometro", nullable = false, columnDefinition = "boolean default false")
    private boolean possuiHobodometro;
    @Enumerated(EnumType.STRING)
    @Column(name = "origem_cadastro", nullable = false)
    private OrigemAcaoEnum origemCadastro;
    @Column(name = "acoplado", nullable = false)
    private boolean acoplado;
    @Formula(value = "(select count(*) from veiculo_pneu vp where vp.cod_veiculo = codigo)")
    private Integer qtdPneusAplicados;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "veiculo_acoplamento_atual",
               joinColumns = @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo"),
               inverseJoinColumns = @JoinColumn(name = "cod_processo", referencedColumnName = "codigo"))
    private AcoplamentoProcessoEntity acoplamentoProcessoEntity;

    @NotNull
    public Optional<AcoplamentoProcessoEntity> getAcoplamentoProcessoEntity() {
        return Optional.ofNullable(acoplamentoProcessoEntity);
    }

    @Nullable
    public Short getPosicaoAcopladoAtual() {
        if (acoplamentoProcessoEntity == null) {
            return null;
        }

        return acoplamentoProcessoEntity.getAcoplamentoAtualEntities()
                .stream()
                .filter(acoplamentoAtualEntity -> acoplamentoAtualEntity.getCodVeiculoAcoplamentoAtual().equals(codigo))
                .map(AcoplamentoAtualEntity::getCodPosicao)
                .findFirst()
                .orElse(null);
    }
}
