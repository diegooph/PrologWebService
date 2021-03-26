package br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.Serializable;

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
    @EmbeddedId
    private PneuServicoCadastroEntity.PK pk;
    @OneToOne(mappedBy = "pneuServicoCadastro")
    private PneuServicoRealizadoEntity pneuServicoRealizado;

    @NotNull
    public static PneuServicoCadastroEntity createFromPneuServico(
            @NotNull final PneuServicoRealizadoEntity pneuServico) {
        final PK pkCadastro = PK.builder().codPneu(pneuServico.getCodPneu()).build();
        return PneuServicoCadastroEntity.builder()
                .pk(pkCadastro)
                .build();
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Embeddable
    public static class PK implements Serializable {
        @Column(name = "cod_pneu", nullable = false)
        private Long codPneu;
        @Column(name = "cod_servico_realizado", nullable = false)
        private Long codServicoRealizado;
    }
}