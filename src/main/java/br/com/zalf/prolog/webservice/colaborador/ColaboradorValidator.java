package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.colaborador.model.*;
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

    private static void validacaoCpf(Long cpf) throws Exception {
        Preconditions.checkNotNull(cpf, "Você precisa fornecer o CPF");

        if (!ValidationUtils.isValidCpf(String.format("%011d", cpf))) {
            throw new GenericException("CPF inválido", null);
        }
    }

    private static void validacaoMatriculaAmbev(Integer matriculaAmbev) throws Exception {

        if (!verificacaoNumeroPositivo(matriculaAmbev)) {
            throw new GenericException("Matrícula Ambev inválida.\nA matrícula deve ser um número positivo", "A matrícula fornecida é um número negativo");
        }
    }

    private static void validacaoMatriculaTrans(Integer matriculaTrans) throws Exception {

        if (!verificacaoNumeroPositivo(matriculaTrans)) {
            throw new GenericException("Matrícula Transportadora inválida.\nA matrícula deve ser um número positivo", "A matrícula fornecida é um número negativo");
        }
    }

    private static void validacaoDataNascimento(Date dataNascimento) throws Exception {
        Preconditions.checkNotNull(dataNascimento, "Você precisa fornecer a data de nascimento\n");

        if (!verificacaoAno(dataNascimento)) {
            throw new GenericException("Ano de Nascimento inválido", null);
        }
    }

    private static void validacaoDataAdmissao(Date dataAdmissao) throws Exception {
        Preconditions.checkNotNull(dataAdmissao, "Você precisa fornecer a data da admissão");

        if (!verificacaoAno(dataAdmissao)) {
            throw new GenericException("Ano de Admissão inválido", null);
        }

    }

    private static void validacaoNome(String nome) throws Exception {
        Preconditions.checkNotNull(nome, "Você precisa preencher o nome");

        if (!verificacaoCaracteresLetras(nome)) {
            throw new GenericException("Nome inválido.\nO Nome não pode conter números", "O campo 'nome' contém números");
        }
    }

    private static void validacaoSetor(Setor setor) throws Exception {
        Preconditions.checkNotNull(setor, "Você precisa fornecer o Setor");
        Preconditions.checkNotNull(setor.getCodigo(), "Você precisa fornecer o Setor");

        if (!verificacaoNumeroPositivo(setor.getCodigo().intValue())) {
            throw new GenericException("Setor inválido", "O código é negativo");
        }

    }

    private static void validacaoFuncao(Cargo funcao) throws Exception {
        Preconditions.checkNotNull(funcao, "Você precisa fornecer o Cargo");
        Preconditions.checkNotNull(funcao.getCodigo(), "Você precisa fornecer a Cargo");


        if (!verificacaoNumeroPositivo(funcao.getCodigo().intValue())) {
            throw new GenericException("Cargo inválido", "O código é negativo");
        }

    }

    private static void validacaoUnidade(Unidade unidade) throws Exception {
        Preconditions.checkNotNull(unidade, "Você precisa fornecer a Unidade");
        Preconditions.checkNotNull(unidade.getCodigo(), "Você precisa fornecer a Unidade");

        if (!verificacaoNumeroPositivo(unidade.getCodigo().intValue())) {
            throw new GenericException("Setor inválido", "O código é negativo");
        }
    }

    private static void validacaoNivelPermissao(Integer codPermissao) throws Exception {
        Preconditions.checkNotNull(codPermissao, "Você precisa fornecer o Nível de Acesso");

        if (codPermissao < Permissao.LOCAL || codPermissao > Permissao.GERAL) {
            throw new GenericException("Nível de Acesso inválido", "Cód menor que 0 ou maior que 3");
        }

    }

    private static void validacaoEquipe(Equipe equipe) throws Exception {
        Preconditions.checkNotNull(equipe, "Você precisa fornecer a Equipe");
        Preconditions.checkNotNull(equipe.getCodigo(), "Você precisa fornecer a Equipe");

        if (!verificacaoNumeroPositivo((int) equipe.getCodigo().intValue())) {
            throw new GenericException("Equipe inválida", "O código é negativo");
        }
    }

    private static void validacaoPis(String pis) throws Exception {

        if (pis.length() < 11) {
            throw new GenericException("PIS inválido.\nO PIS deve conter 11 dígitos", null);
        } else if (!ValidationUtils.validaPIS(pis)) {
            throw new GenericException("PIS inválido", null);
        }
    }

    private static boolean verificacaoCaracteresLetras(String palavra) {

        if(palavra.matches(".*\\d+.*")){
            return false;
        }else{
            return true;
        }

    }

    private static boolean verificacaoNumeroPositivo(int numero) {

        return numero > 0;
    }

    private static boolean verificacaoAno(Date data) {
        final int anoMinimoPermitido = 1900;
        final int anoMaximoPermitido = 2050;
        SimpleDateFormat ano = new SimpleDateFormat("yyyy");
        final int anoDataNascimento = Integer.parseInt(ano.format(data));

        return anoDataNascimento < anoMaximoPermitido && anoDataNascimento > anoMinimoPermitido;
    }
}
