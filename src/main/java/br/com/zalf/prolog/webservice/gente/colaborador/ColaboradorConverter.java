package br.com.zalf.prolog.webservice.gente.colaborador;

import br.com.zalf.prolog.webservice.gente.colaborador.model.*;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 17/06/2020
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class ColaboradorConverter {
    private ColaboradorConverter() {
        throw new IllegalStateException(ColaboradorConverter.class.getSimpleName()
                + "cannot be instantiated!");
    }

    @NotNull
    public static ColaboradorListagem createColaboradorListagem(@NotNull final ResultSet rSet) throws SQLException {
        return new ColaboradorListagem(
                rSet.getLong("CODIGO"),
                rSet.getString("NOME_COLABORADOR"),
                rSet.getLong("CPF"),
                rSet.getLong("COD_REGIONAL"),
                rSet.getString("NOME_REGIONAL"),
                rSet.getLong("COD_UNIDADE"),
                rSet.getString("NOME_UNIDADE"),
                rSet.getLong("COD_FUNCAO"),
                rSet.getString("NOME_FUNCAO"),
                rSet.getLong("COD_EQUIPE"),
                rSet.getString("NOME_EQUIPE"),
                rSet.getLong("COD_SETOR"),
                rSet.getString("NOME_SETOR"),
                rSet.getInt("MATRICULA_AMBEV"),
                rSet.getInt("MATRICULA_TRANS"),
                rSet.getDate("DATA_NASCIMENTO"),
                rSet.getBoolean("STATUS_ATIVO"));
    }

    @NotNull
    public static Colaborador createColaborador(final ResultSet rSet) throws SQLException {
        final Colaborador c = new Colaborador();
        c.setCodigo(rSet.getLong("CODIGO"));
        c.setAtivo(rSet.getBoolean("STATUS_ATIVO"));

        final Cargo cargo = new Cargo();
        cargo.setCodigo(rSet.getLong("COD_FUNCAO"));
        cargo.setNome(rSet.getString("NOME_FUNCAO"));
        c.setFuncao(cargo);

        final Empresa empresa = new Empresa();
        empresa.setCodigo(rSet.getLong("COD_EMPRESA"));
        empresa.setNome(rSet.getString("NOME_EMPRESA"));
        empresa.setLogoThumbnailUrl(rSet.getString("LOGO_THUMBNAIL_URL"));
        c.setEmpresa(empresa);

        final Regional regional = new Regional();
        regional.setCodigo(rSet.getLong("COD_REGIONAL"));
        regional.setNome(rSet.getString("NOME_REGIONAL"));
        c.setRegional(regional);

        final Unidade unidade = new Unidade();
        unidade.setCodigo(rSet.getLong("COD_UNIDADE"));
        unidade.setNome(rSet.getString("NOME_UNIDADE"));
        c.setUnidade(unidade);

        final Equipe equipe = new Equipe();
        equipe.setCodigo(rSet.getLong("COD_EQUIPE"));
        equipe.setNome(rSet.getString("NOME_EQUIPE"));
        c.setEquipe(equipe);

        final Setor setor = new Setor();
        setor.setCodigo(rSet.getLong("COD_SETOR"));
        setor.setNome(rSet.getString("NOME_SETOR"));
        c.setSetor(setor);

        c.setCpf(rSet.getLong("CPF"));
        c.setPis(rSet.getString("PIS"));
        c.setDataNascimento(rSet.getDate("DATA_NASCIMENTO"));
        c.setNome(rSet.getString("NOME_COLABORADOR"));
        final int matriculaAmbev = rSet.getInt("MATRICULA_AMBEV");
        if (!rSet.wasNull()) {
            c.setMatriculaAmbev(matriculaAmbev);
        }
        final int matriculaTrans = rSet.getInt("MATRICULA_TRANS");
        if (!rSet.wasNull()) {
            c.setMatriculaTrans(matriculaTrans);
        }
        c.setDataAdmissao(rSet.getDate("DATA_ADMISSAO"));
        c.setDataDemissao(rSet.getDate("DATA_DEMISSAO"));
        c.setCodPermissao(rSet.getInt("PERMISSAO"));
        c.setTzUnidade(rSet.getString("TZ_UNIDADE"));

        if (rSet.getString("NUMERO_TELEFONE") != null) {
            c.setTelefone(new ColaboradorTelefone(
                    rSet.getString("SIGLA_ISO2"),
                    rSet.getInt("PREFIXO_PAIS"),
                    rSet.getString("NUMERO_TELEFONE")));
        }

        c.setEmail(rSet.getString("EMAIL"));

        return c;
    }

    @NotNull
    public static Cargo createFuncao(final ResultSet rSet) throws SQLException {
        final Cargo f = new Cargo();
        f.setCodigo(rSet.getLong("CODIGO_CARGO"));
        f.setNome(rSet.getString("NOME_CARGO"));
        return f;
    }
}
