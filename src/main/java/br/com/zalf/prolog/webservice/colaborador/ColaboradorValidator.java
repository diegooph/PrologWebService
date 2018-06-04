package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.colaborador.model.*;
import br.com.zalf.prolog.webservice.commons.util.GenericUtils;
import br.com.zalf.prolog.webservice.commons.util.ValidationUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.permissao.Permissao;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ColaboradorValidator {

    public static void validacaoAtributosColaborador(@NotNull final Colaborador colaborador) throws GenericException {
        try {
            validacaoRegional(colaborador.getRegional().getCodigo());
            validacaoCpf(colaborador.getCpf());
            validacaoMatriculaAmbev(colaborador.getMatriculaAmbev());
            validacaoMatriculaTrans(colaborador.getMatriculaTrans());
            validacaoDataNascimento(colaborador.getDataNascimento());
            validacaoDataAdmissao(colaborador.getDataAdmissao());
            validacaoNome(colaborador.getNome());
            validacaoSetor(colaborador.getSetor());
            validacaoFuncao(colaborador.getFuncao());
            validacaoUnidade(colaborador.getUnidade());
            validacaoNivelPermissao(colaborador.getCodPermissao());
            validacaoEquipe(colaborador.getEquipe());
            validacaoPis(colaborador.getPis());
        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), null);
        }
    }

    private static void validacaoRegional (Long regional){
        Preconditions.checkNotNull(regional, "Você precisa selecionar a Regional");
    }



    private static void validacaoCpf(Long cpf) throws Exception {
        Preconditions.checkNotNull(cpf, "Você precisa fornecer o CPF");

        if (!ValidationUtils.isValidCpf(String.format("%011d", cpf))) {
            throw new GenericException("CPF inválido", null);
        }
    }

    private static void validacaoMatriculaAmbev(Integer matriculaAmbev) throws Exception {
        Preconditions.checkNotNull(matriculaAmbev, "Você precisa fornecer a Matrícula Ambev");
        if (GenericUtils.verificaNumeroNegativo(matriculaAmbev)) {
            throw new GenericException("Matrícula Ambev inválida.\nA Matrícula deve ser um número positivo", "A matrícula fornecida é um número negativo");
        }
    }

    private static void validacaoMatriculaTrans(Integer matriculaTrans) throws Exception {
        Preconditions.checkNotNull(matriculaTrans, "Você precisa fornecer a Matrícula Transportadora");
        if (GenericUtils.verificaNumeroNegativo(matriculaTrans)) {
            throw new GenericException("Matrícula Transportadora inválida.\nA Matrícula deve ser um número positivo", "A matrícula fornecida é um número negativo");
        }
    }

    private static void validacaoDataNascimento(Date dataNascimento) throws Exception {
        Preconditions.checkNotNull(dataNascimento, "Você precisa fornecer a data de nascimento\n");

        if (GenericUtils.verificaAno(dataNascimento)) {
            throw new GenericException("Ano de Nascimento inválido", null);
        }
    }

    private static void validacaoDataAdmissao(Date dataAdmissao) throws Exception {
        Preconditions.checkNotNull(dataAdmissao, "Você precisa fornecer a data da admissão");

        if (GenericUtils.verificaAno(dataAdmissao)) {
            throw new GenericException("Ano de Admissão inválido", null);
        }

    }

    private static void validacaoNome(String nome) throws Exception {
        Preconditions.checkNotNull(nome, "Você precisa fornecer o nome");

        if (!GenericUtils.verificaContemApenasLetras(nome)) {
            throw new GenericException("Nome inválido\nO Nome não pode conter números", "O campo 'nome' contém números");
        }
    }

    private static void validacaoSetor(Setor setor) throws Exception {
        Preconditions.checkNotNull(setor, "Você precisa selecionar o Setor");
        Preconditions.checkNotNull(setor.getCodigo(), "Você precisa selecionar o Setor");

        if (GenericUtils.verificaNumeroNegativo(setor.getCodigo().intValue())) {
            throw new GenericException("Setor inválido", "O código é negativo");
        }

    }

    private static void validacaoFuncao(Cargo funcao) throws Exception {
        Preconditions.checkNotNull(funcao, "Você precisa selecionar o Cargo");
        Preconditions.checkNotNull(funcao.getCodigo(), "Você precisa selecionar a Cargo");


        if (GenericUtils.verificaNumeroNegativo(funcao.getCodigo().intValue())) {
            throw new GenericException("Cargo inválido", "O código é negativo");
        }

    }

    private static void validacaoUnidade(Unidade unidade) throws Exception {
        Preconditions.checkNotNull(unidade, "Você precisa selecionar a Unidade");
        Preconditions.checkNotNull(unidade.getCodigo(), "Você precisa fornecer a Unidade");

        if (GenericUtils.verificaNumeroNegativo(unidade.getCodigo().intValue())) {
            throw new GenericException("Setor inválido", "O código é negativo");
        }
    }

    private static void validacaoNivelPermissao(Integer codPermissao) throws Exception {
        Preconditions.checkNotNull(codPermissao, "Você precisa selecionar o Nível de Acesso");

        if (codPermissao < Permissao.LOCAL || codPermissao > Permissao.GERAL) {
            throw new GenericException("Nível de Acesso inválido", "Cód menor que 0 ou maior que 3");
        }

    }

    private static void validacaoEquipe(Equipe equipe) throws Exception {
        Preconditions.checkNotNull(equipe, "Você precisa selecionar a Equipe");
        Preconditions.checkNotNull(equipe.getCodigo(), "Você precisa selecionar a Equipe");

        if (GenericUtils.verificaNumeroNegativo((int) equipe.getCodigo().intValue())) {
            throw new GenericException("Equipe inválida", "O código é negativo");
        }
    }

    private static void validacaoPis(String pis) throws Exception {
        Preconditions.checkNotNull(pis, "Você precisa fornecer o PIS");
        if (pis.length() < 11) {
            throw new GenericException("PIS inválido\nO PIS deve conter 11 dígitos", null);
        } else if (!ValidationUtils.validaPIS(pis)) {
            throw new GenericException("PIS inválido", null);
        }
    }




}
