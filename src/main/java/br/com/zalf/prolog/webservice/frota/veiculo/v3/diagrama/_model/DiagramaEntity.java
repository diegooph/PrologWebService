package br.com.zalf.prolog.webservice.frota.veiculo.v3.diagrama._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "veiculo_diagrama")
public class DiagramaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Short codigo;
    @Column(name = "nome", nullable = false)
    private String nome;
    @Column(name = "url_imagem")
    private String urlImagem;
    @Column(name = "motorizado", nullable = false)
    private boolean motorizado;
}
