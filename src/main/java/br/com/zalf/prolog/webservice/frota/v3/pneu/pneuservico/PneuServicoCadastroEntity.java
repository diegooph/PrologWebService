package br.com.zalf.prolog.webservice.frota.v3.pneu.pneuservico;

import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "pneu_servico_cadastro", schema = "public")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PneuServicoCadastroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false, unique = true, updatable = false)
    private Long codigo;
    @Column(name = "cod_pneu", nullable = false)
    private Long codPneu;
    @Column(name = "cod_servico_realizado", nullable = false)
    private Long codServicoRealizado;
    @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
    private String fonteServicoRealizado;
    @OneToOne(mappedBy = "pneuServicoCadastro")
    @JoinColumns(@JoinColumn(name = "cod_servico_realizado", referencedColumnName = "codigo"))
    private PneuServicoRealizadoEntity pneuServicoRealizado;

    @NotNull
    public static PneuServicoCadastroEntity createFromPneuServico(
            @NotNull final PneuServicoRealizadoEntity pneuServico) {
        return PneuServicoCadastroEntity.builder()
                .codPneu(pneuServico.getCodigo())
                .fonteServicoRealizado(PneuServicoRealizado.FONTE_CADASTRO)
                .build();
    }
}