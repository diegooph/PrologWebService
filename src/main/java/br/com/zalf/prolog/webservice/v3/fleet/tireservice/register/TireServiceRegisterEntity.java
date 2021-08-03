package br.com.zalf.prolog.webservice.v3.fleet.tireservice.register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pneu_servico_cadastro", schema = "public")
public class TireServiceRegisterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;
    @Column(name = "cod_pneu", nullable = false)
    private Long tireId;
    @Column(name = "cod_servico_realizado", nullable = false)
    private Long tireServiceId;
    @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
    private String tireServiceOrigin;
}