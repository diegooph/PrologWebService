package br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Optional;
import java.util.Set;

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
@Table(schema = "public", name = "veiculo_transferencia_processo")
public final class TransferenciaVeiculoProcessoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_unidade_origem", nullable = false)
    private Long codUnidadeOrigem;
    @Column(name = "cod_unidade_destino", nullable = false)
    private Long codUnidadeDestino;
    @Column(name = "cod_unidade_colaborador", nullable = false)
    private Long codUnidadeColaborador;
    @OneToMany(mappedBy = "transferenciaVeiculoProcesso", fetch = FetchType.LAZY)
    private Set<TransferenciaVeiculoInformacaoEntity> transferenciaVeiculoInformacoes;

    @NotNull
    public Optional<TransferenciaVeiculoInformacaoEntity> getInformacoesTransferenciaVeiculo(
            @NotNull final Long codVeiculo) {
        return transferenciaVeiculoInformacoes
                .stream()
                .filter(info -> info.getCodVeiculo().equals(codVeiculo))
                .findFirst();
    }
}
