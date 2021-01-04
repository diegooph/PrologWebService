package br.com.zalf.prolog.webservice.gente.empresa._model;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created on 2020-11-24
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(schema = "public", name = "empresa")
@Data
public class EmpresaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "logo_thumbnail_url")
    private String logoThumbnailUrl;

    @Column(name = "data_hora_cadastro", nullable = false)
    private LocalDateTime dataHoraCadastro = Now.getLocalDateTimeUtc();

    @Column(name = "cod_auxiliar")
    private String codAuxiliar;
}
