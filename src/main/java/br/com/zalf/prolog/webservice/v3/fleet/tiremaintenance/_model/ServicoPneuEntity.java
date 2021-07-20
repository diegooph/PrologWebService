package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import br.com.zalf.prolog.webservice.v3.LocalDateTimeUtcAttributeConverter;
import br.com.zalf.prolog.webservice.v3.fleet.afericao._model.AfericaoAlternativaEntity;
import br.com.zalf.prolog.webservice.v3.fleet.afericao._model.AfericaoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.afericao.valores._model.AfericaoPneuValorEntity;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.VeiculoKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.ColaboradorEntity;
import br.com.zalf.prolog.webservice.v3.fleet.pneu._model.PneuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "afericao_manutencao")
public final class ServicoPneuEntity implements EntityKmColetado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @Column(name = "km_momento_conserto")
    private Long kmColetadoVeiculoFechamentoServico;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_afericao", nullable = false)
    private AfericaoEntity afericao;
    @Column(name = "tipo_servico", nullable = false)
    private TipoServico tipoServico;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo")
    private PneuEntity pneu;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpf_mecanico", referencedColumnName = "cpf")
    private ColaboradorEntity mecanico;
    @Convert(converter = LocalDateTimeUtcAttributeConverter.class)
    @Column(name = "data_hora_resolucao")
    private LocalDateTime dataHoraResolucao;
    @Column(name = "qt_apontamentos", nullable = false, columnDefinition = "default 1")
    private Integer quantidadeApontamentos;
    @Column(name = "psi_apos_conserto")
    private Double psiAposConserto;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_alternativa", referencedColumnName = "codigo")
    private AfericaoAlternativaEntity alternativa;
    @Column(name = "tempo_realizacao_millis")
    private Long tempoRealizacaoMillis;
    @Column(name = "fechado_automaticamente_movimentacao", nullable = false, columnDefinition = "default false")
    private Boolean fechadoMovimentacao;
    @Column(name = "fechado_automaticamente_integracao", nullable = false, columnDefinition = "default false")
    private Boolean fechadoIntegracao;
    @Column(name = "fechado_automaticamente_afericao", nullable = false, columnDefinition = "default false")
    private Boolean fechadoAfericao;
    @Column(name = "forma_coleta_dados_fechamento")
    private FormaColetaDadosAfericaoEnum formaColetaDadosFechamento;

    @NotNull
    @Override
    public VeiculoKmColetado getVeiculoKmColetado() {
        return VeiculoKmColetado.of(getAfericao().getVeiculo().getId(), kmColetadoVeiculoFechamentoServico);
    }

    @Transient
    @NotNull
    public ServicoPneuStatus getStatus() {
        return !isFechadoAutomaticamente() && mecanico == null ? ServicoPneuStatus.ABERTO : ServicoPneuStatus.FECHADO;
    }

    public boolean isFechadoAutomaticamente() {
        return fechadoAfericao || fechadoMovimentacao || fechadoIntegracao;
    }

    @NotNull
    public Optional<AfericaoPneuValorEntity> getValorAfericaoRelatedToPneu() {
        return afericao.getValoresAfericao().stream()
                .filter(valor -> Objects.equals(valor.getPneu(), pneu))
                .findFirst();
    }
}
