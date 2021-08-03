package br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
public final class RetreaderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;
    @Column(name = "nome")
    private String name;
    @Column(name = "cod_empresa")
    private long companyId;
    @Column(name = "ativa")
    private boolean isActive;
}
