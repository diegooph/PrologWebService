package br.com.zalf.prolog.webservice.v3.frota.pneu._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created on 2021-05-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "dimensao_pneu", schema = "public")
@Builder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DimensaoPneuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "altura", nullable = false)
    private Double altura;
    @Column(name = "largura", nullable = false)
    private Double largura;
    @Column(name = "aro", nullable = false)
    private Double aro;
    @Column(name = "cod_empresa", nullable = false)
    private Long codEmpresa;
    @Column(name = "cod_auxiliar", nullable = false)
    private Long codAuxiliar;
    @Column(name = "status_ativo", nullable = false)
    private boolean statusAtivo;
    @Column(name = "data_hora_cadastro", nullable = false)
    private LocalDateTime dataHoraCadastro;
    @Column(name = "cod_colaborador_cadastro", nullable = false)
    private Long codColaboradorCadastro;
    @Column(name = "data_hora_ultima_atualizacao", nullable = false)
    private LocalDateTime dataHoraUltimaAtualizacao;
    @Column(name = "cod_colaborador_ultima_atualizacao", nullable = false)
    private Long codColaboradorUltimaAtualizacao;

    @NotNull
    public String getDimensaoUserFriendly() {
        return largura + "/" + altura + " R" + aro;
    }
}
