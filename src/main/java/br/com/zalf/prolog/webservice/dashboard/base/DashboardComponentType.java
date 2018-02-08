package br.com.zalf.prolog.webservice.dashboard.base;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class DashboardComponentType {
    @NotNull
    private Integer codigo;
    @NotNull
    private String nome;
    @NotNull
    private String descricao;
    private int maximoBlocosHorizontais;
    private int maximoBlocosVerticais;
    private int minimoBlocosHorizontais;
    private int minimoBlocosVerticais;

    public DashboardComponentType(@NotNull Integer codigo, @NotNull String nome, @NotNull String descricao) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
    }

    @NotNull
    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(@NotNull Integer codigo) {
        this.codigo = codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    public void setNome(@NotNull String nome) {
        this.nome = nome;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NotNull String descricao) {
        this.descricao = descricao;
    }

    public int getMaximoBlocosHorizontais() {
        return maximoBlocosHorizontais;
    }

    public void setMaximoBlocosHorizontais(int maximoBlocosHorizontais) {
        this.maximoBlocosHorizontais = maximoBlocosHorizontais;
    }

    public int getMaximoBlocosVerticais() {
        return maximoBlocosVerticais;
    }

    public void setMaximoBlocosVerticais(int maximoBlocosVerticais) {
        this.maximoBlocosVerticais = maximoBlocosVerticais;
    }

    public int getMinimoBlocosHorizontais() {
        return minimoBlocosHorizontais;
    }

    public void setMinimoBlocosHorizontais(int minimoBlocosHorizontais) {
        this.minimoBlocosHorizontais = minimoBlocosHorizontais;
    }

    public int getMinimoBlocosVerticais() {
        return minimoBlocosVerticais;
    }

    public void setMinimoBlocosVerticais(int minimoBlocosVerticais) {
        this.minimoBlocosVerticais = minimoBlocosVerticais;
    }

    @Override
    public String toString() {
        return "DashboardComponentType{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", maximoBlocosHorizontais=" + maximoBlocosHorizontais +
                ", maximoBlocosVerticais=" + maximoBlocosVerticais +
                ", minimoBlocosHorizontais=" + minimoBlocosHorizontais +
                ", minimoBlocosVerticais=" + minimoBlocosVerticais +
                '}';
    }
}