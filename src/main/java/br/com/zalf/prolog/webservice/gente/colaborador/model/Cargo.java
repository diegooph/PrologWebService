package br.com.zalf.prolog.webservice.gente.colaborador.model;

import br.com.zalf.prolog.webservice.permissao.pilares.FuncaoProLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Função do colaborador
 */
public class Cargo {
	private Long codigo;
	private String nome;
	private List<Pilar> permissoes;
	
	public Cargo() {
		
	}
	
	public Cargo(Long codigo, String nome) {
		this.codigo = codigo;
		this.nome = nome;
	}

	public List<Pilar> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<Pilar> permissoes) {
		this.permissoes = permissoes;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		return "Cargo{" +
				"codigo=" + codigo +
				", nome='" + nome + '\'' +
				", permissões=" + permissoes +
				'}';
	}

	@NotNull
	public static Cargo createDummy() {
		final Cargo cargo = new Cargo();
		cargo.setNome("Cargo Teste");
		cargo.setCodigo(1L);

		// Cria pilar frota.
		final List<Pilar> pilares = new ArrayList<>();
		final Pilar frota = new Pilar();
		frota.setCodigo(Pilares.FROTA);
		frota.setNome("Frota");

		// Cria função de realizar aferição.
		final List<FuncaoProLog> funcoesFrota = new ArrayList<>();
		final FuncaoProLog realizarAfericao = new FuncaoProLog();
		realizarAfericao.setCodigo(Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA);
		funcoesFrota.add(realizarAfericao);

		frota.setFuncoes(funcoesFrota);
		pilares.add(frota);
		cargo.setPermissoes(pilares);
		return cargo;
	}
}