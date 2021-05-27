package br.com.zalf.prolog.webservice.v3.frota.afericao.valores._model;

import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created on 2021-05-25
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "afericao_valores", schema = "public")
@Data
@AssociationOverrides(value = {
        @AssociationOverride(name = "pk.afericao",
                             joinColumns = @JoinColumn(name = "cod_afericao",
                                                       referencedColumnName = "codigo",
                                                       nullable = false)),
        @AssociationOverride(name = "pk.pneu",
                             joinColumns = @JoinColumn(name = "cod_pneu",
                                                       referencedColumnName = "codigo",
                                                       nullable = false))
})
public class AfericaoPneuValorEntity {

    @Id
    @Column(name = "cod_afericao")
    private Long codAfericao;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_afericao", referencedColumnName = "codigo")
    private AfericaoEntity afericao;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo")
    private PneuEntity pneu;

    @Id
    @Column(name = "cod_pneu")
    private Long codPneu;

    @Column(name = "psi")
    private Double psi;
    @Column(name = "posicao")
    private Integer posicao;
    @Column(name = "vida_momento_afericao")
    private Integer vidaMomentoAfericao;
}
