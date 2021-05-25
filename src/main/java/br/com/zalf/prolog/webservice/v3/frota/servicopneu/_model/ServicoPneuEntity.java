package br.com.zalf.prolog.webservice.v3.frota.servicopneu._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.VeiculoKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.ColaboradorEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private long kmColetadoVeiculoFechamentoServico;
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
    @Column(name = "data_hora_resolucao")
    private LocalDateTime dataHoraResolucao;
    @Column(name = "qt_apontamentos", nullable = false, columnDefinition = "default 1")
    private Integer quantidadeApontamentos;
    @Column(name = "psi_apos_conserto")
    private Double psiAposConserto;
    @Column(name = "cod_alternativa")
    private Long codAlternativa;
    @Column(name = "tempo_realizacao_millis")
    private Long tempoRealizacaoMillis;
    @Column(name = "fechado_automaticamente_movimentacao", nullable = false, columnDefinition = "default false")
    private boolean fechadoMovimentacao;
    @Column(name = "fechado_automaticamente_integracao", nullable = false, columnDefinition = "default false")
    private boolean fechadoIntegracao;
    @Column(name = "fechado_automaticamente_afericao", nullable = false, columnDefinition = "default false")
    private boolean fechadoAfericao;
    @Column(name = "forma_coleta_dados_fechamento")
    private FormaColetaDadosAfericaoEnum formaColetaDadosFechamento;

    @NotNull
    @Override
    public VeiculoKmColetado getVeiculoKmColetado() {
        return VeiculoKmColetado.of(getAfericao().getVeiculo().getCodigo(), kmColetadoVeiculoFechamentoServico);
    }

    public boolean isFechadoAutomaticamente() {
        return fechadoAfericao || fechadoMovimentacao || fechadoIntegracao;
    }
}
