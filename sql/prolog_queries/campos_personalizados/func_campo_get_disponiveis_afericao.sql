-- Sobre:
-- Busca os campos personalizados que estão disponíveis para preenchimento durante um processo de aferição na
-- unidade. Note que para um campo estar disponível para uso no processo de aferição na unidade, além de estar
-- cadastrado na empresa para a funcionalidade de aferição, também precisa estar vinculado à unidade na tabela:
-- "aferição_campo_personalizado_unidade".
--
-- Histórico:
-- 2020-09-17 -> Function criada (gustavocnp95 - PL-2909).
-- 2020-10-27 -> Adiciona tipo do processo coleta de aferição (didivz - PL-3254).
create or replace function func_campo_get_disponiveis_afericao(f_cod_unidade bigint,
                                                               f_tipo_processo_coleta_afericao text)
    returns table
            (
                cod_campo                                bigint,
                cod_empresa                              bigint,
                cod_funcao_prolog_agrupamento            smallint,
                cod_tipo_campo                           smallint,
                nome_campo                               text,
                descricao_campo                          text,
                texto_auxilio_preenchimento_campo        text,
                preenchimento_obrigatorio_campo          boolean,
                mensagem_caso_campo_nao_preenchido_campo text,
                permite_selecao_multipla_campo           boolean,
                opcoes_selecao_campo                     text[],
                ordem_exibicao                           smallint
            )
    language sql
as
$$
select cpe.codigo                              as cod_campo,
       cpe.cod_empresa                         as cod_empresa,
       cpe.cod_funcao_prolog_agrupamento       as cod_funcao_prolog_agrupamento,
       cpe.cod_tipo_campo                      as cod_tipo_campo,
       cpe.nome::text                          as nome_campo,
       cpe.descricao::text                     as descricao_campo,
       cpe.texto_auxilio_preenchimento::text   as texto_auxilio_preenchimento_campo,
       acpu.preenchimento_obrigatorio          as preenchimento_obrigatorio_campo,
       acpu.mensagem_caso_campo_nao_preenchido as mensagem_caso_campo_nao_preenchido_campo,
       cpe.permite_selecao_multipla            as permite_selecao_multipla_campo,
       cpe.opcoes_selecao                      as opcoes_selecao_campo,
       acpu.ordem_exibicao                     as ordem_exibicao
from campo_personalizado_empresa cpe
         join afericao_campo_personalizado_unidade acpu
              on cpe.codigo = acpu.cod_campo
where cpe.status_ativo = true
  and acpu.habilitado_para_uso = true
  and acpu.cod_unidade = f_cod_unidade
  and acpu.tipo_processo_coleta_afericao = f_tipo_processo_coleta_afericao
order by acpu.ordem_exibicao;
$$;