package br.com.zalf.prolog.webservice.v3.fleet.tire._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

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
    private Integer altura;
    @Column(name = "largura", nullable = false)
    private Integer largura;
    @Column(name = "aro", nullable = false)
    private Double aro;

    @NotNull
    public String getDimensaoUserFriendly() {
        return largura + "/" + altura + " R" + aro;
    }
}
