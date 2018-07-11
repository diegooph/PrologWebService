package br.com.zalf.prolog.webservice.permissao;

/**
 * Created by luiz on 2/3/16.
 * Constantes com as permissões
 */
public interface Permissao {
    /**
     * LOCAL = visualização apenas da equipe ao qual o colaborador esta cadastrado.
     */
    long LOCAL = 0;
    /**
     * LOCAL_UNIDADE = visualização de todas as equipes da unidade ao qual o colaborador esta cadastrado
     */
    long LOCAL_UNIDADE = 1;
    /**
     * REGIONAL = visualização de todas as unidades e quipes ao qual o colaborador esta cadastrado
     */
    long REGIONAL = 2;
    /**
     * GERAL = visualização de todas as regionais, unidades e equipes da empresa ao qual o colaborador esta cadastrado
     */
    long GERAL = 3;

    /**
     * VISUALIZAR_TODOS = visualização das produtividades de todos os colaboradores
     */
    long VISUALIZAR_TODOS = 4;

    /**
     * VISUALIZAR_PROPRIOS = visualização de suas próprias produtividades
     */
    long VISUALIZAR_PROPRIOS = 5;

    /**
     * EDITAR = edição de produtividades
     */
    long EDITAR = 6;

    /**
     * UPLOAD = enviar produtividades
     */
    long UPLOAD = 7;

    /**
     * DELETAR = deleção de produtividades
     */
    long DELETAR = 8;

    /**
     * Constantes destinadas ao controle de permissão do setor operacional
     */
    final class Operacional {
        public static final long DISTRIBUICAO = 10;
        public static final long PUXADA = 11;
        public static final long ARMAZEM = 12;
    }
}

