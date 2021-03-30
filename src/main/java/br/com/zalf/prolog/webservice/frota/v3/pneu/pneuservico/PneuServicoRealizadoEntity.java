package br.com.zalf.prolog.webservice.frota.v3.pneu.pneuservico;

import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.frota.v3.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.v3.pneu.pneuservico.tiposervico.PneuTipoServicoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "pneu_servico_realizado", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PneuServicoRealizadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_tipo_servico", nullable = false)
    private Long codTipoServico;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @Column(name = "cod_pneu", nullable = false)
    private Long codPneu;
    @Column(name = "custo", nullable = false)
    private BigDecimal custo;
    @Column(name = "vida", nullable = false)
    private Integer vida;
    @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
    private String fonteServicoRealizado;

    @NotNull
    public static PneuServicoRealizadoEntity createPneuServicoForCadastro(
            @NotNull final PneuTipoServicoEntity pneuTipoServico,
            @NotNull final PneuEntity pneu,
            @NotNull final BigDecimal valorBanda) {
        return PneuServicoRealizadoEntity.builder()
                .codTipoServico(pneuTipoServico.getCodigo())
                .codUnidade(pneu.getCodUnidade())
                .codPneu(pneu.getCodigo())
                .custo(valorBanda)
                .vida(getVidaServicoFromPneu(pneu))
                .fonteServicoRealizado(PneuServicoRealizado.FONTE_CADASTRO)
                .build();
    }

    @NotNull
    private static Integer getVidaServicoFromPneu(@NotNull final PneuEntity pneu) {
        return pneu.getVidaAtual() - 1;
    }
}
