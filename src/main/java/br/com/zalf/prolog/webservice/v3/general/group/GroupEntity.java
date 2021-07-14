package br.com.zalf.prolog.webservice.v3.general.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "regional", schema = "public")
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "regiao", nullable = false)
    @NotNull
    private String name;
}
