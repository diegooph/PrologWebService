package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created on 2021-04-23
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "recapadora")
public final class RecapadoraEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "nome")
    private String nome;
    @Column(name = "cod_empresa")
    private long codEmpresa;
    @Column(name = "ativa")
    private boolean ativa;
    @Column(name = "data_hora_cadastro", nullable = false)
    private LocalDateTime dataHoraCadastro;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpf_cadastro", referencedColumnName = "cpf")
    private ColaboradorEntity colaboradorCadastro;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpf_alteracao_status", referencedColumnName = "cpf")
    private ColaboradorEntity colaboradorAlteracaoStatus;
}
