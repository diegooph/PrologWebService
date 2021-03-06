package br.com.zalf.prolog.webservice.v3.fleet.tireservice.servicetype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "pneu_tipo_servico", schema = "public")
public class TireServiceTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;
    @Column(name = "cod_empresa", nullable = false)
    private Long companyId;
    @Column(name = "nome", nullable = false)
    private String name;
    @Column(name = "incrementa_vida", nullable = false)
    private boolean increaseLifeCycle;
    @Column(name = "status_ativo", columnDefinition = "boolean default true", nullable = false)
    private boolean isActive;
    @Column(name = "editavel", columnDefinition = "boolean default true", nullable = false)
    private boolean editable;
    @Column(name = "utilizado_cadastro_pneu", columnDefinition = "boolean default false", nullable = false)
    private boolean usedInTireRegister;
    @Column(name = "cod_colaborador_criacao", nullable = false)
    private Long createdByUserId;
    @Column(name = "data_hora_criacao", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "cod_colaborador_edicao")
    private Long updateByUserId;
    @Column(name = "data_hora_edicao")
    private LocalDateTime updatedAt;
}
