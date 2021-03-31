package br.com.zalf.prolog.webservice.frota.v3.pneu._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Created on 2021-03-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "pneu", schema = "public")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PneuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_empresa", nullable = false)
    private Long codEmpresa;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @Column(name = "codigo_cliente", nullable = false)
    private String codigoCliente;
    @Column(name = "cod_modelo", nullable = false)
    private Long codModelo;
    @Column(name = "cod_dimensao", nullable = false)
    private Long codDimensao;
    @Column(name = "pressao_recomendada", nullable = false)
    private Double pressaoRecomendada;
    @Column(name = "pressao_atual")
    private Double pressaoAtual;
    @Column(name = "altura_sulco_interno")
    private Double alturaSulcoInterno;
    @Column(name = "altura_sulco_central_interno")
    private Double alturaSulcoCentralInterno;
    @Column(name = "altura_sulco_central_externo")
    private Double alturaSulcoCentralExterno;
    @Column(name = "altura_sulco_externo")
    private Double alturaSulcoExterno;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPneu status;
    @Column(name = "vida_atual")
    private Integer vidaAtual;
    @Column(name = "vida_total")
    private Integer vidaTotal;
    @Column(name = "cod_modelo_banda")
    private Long codModeloBanda;
    @Column(name = "dot", length = 20)
    private String dot;
    @Column(name = "valor", nullable = false)
    private BigDecimal valor;
    @Column(name = "data_hora_cadastro", columnDefinition = "timestamp with time zone default now()")
    private OffsetDateTime dataHoraCadastro;
    @Column(name = "pneu_novo_nunca_rodado", columnDefinition = "boolean default false", nullable = false)
    private boolean pneuNovoNuncaRodado;
    @Column(name = "cod_unidade_cadastro", nullable = false)
    private Long codUnidadeCadastro;
    @Enumerated(EnumType.STRING)
    @Column(name = "origem_cadastro", nullable = false)
    private OrigemAcaoEnum origemCadastro;

    public boolean isRecapado() {
        return vidaAtual > 1;
    }

    @NotNull
    public Integer getVidaAnterior() {
        return this.vidaAtual - 1;
    }
}
