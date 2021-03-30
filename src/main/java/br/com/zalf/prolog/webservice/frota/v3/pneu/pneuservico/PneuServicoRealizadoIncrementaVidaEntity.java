package br.com.zalf.prolog.webservice.frota.v3.pneu.pneuservico;

import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.frota.v3.pneu._model.PneuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "pneu_servico_realizado_incrementa_vida", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PneuServicoRealizadoIncrementaVidaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_servico_realizado", nullable = false)
    private Long codServicoRealizado;
    @Column(name = "cod_modelo_banda", nullable = false)
    private Long codModeloBanda;
    @Column(name = "vida_nova_pneu", nullable = false)
    private Integer vidaNovaPneu;
    @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
    private String fonteServicoRealizado;

    @NotNull
    public static PneuServicoRealizadoIncrementaVidaEntity createFromPneuServico(
            @NotNull final PneuEntity pneuEntity,
            @NotNull final PneuServicoRealizadoEntity savedServico) {
        return PneuServicoRealizadoIncrementaVidaEntity.builder()
                .codServicoRealizado(savedServico.getCodigo())
                .codModeloBanda(pneuEntity.getCodModeloBanda())
                .vidaNovaPneu(pneuEntity.getVidaAtual())
                .fonteServicoRealizado(PneuServicoRealizado.FONTE_CADASTRO)
                .build();
    }
}