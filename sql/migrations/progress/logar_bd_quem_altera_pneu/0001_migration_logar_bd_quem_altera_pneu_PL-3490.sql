alter table pneu_data
    add column cod_colaborador_cadastro bigint;

alter table pneu_data
    add column cod_colaborador_ultima_atualizacao bigint;

create or replace view pneu as
select p.codigo_cliente,
       p.cod_modelo,
       p.cod_dimensao,
       p.pressao_recomendada,
       p.pressao_atual,
       p.altura_sulco_interno,
       p.altura_sulco_central_interno,
       p.altura_sulco_externo,
       p.cod_unidade,
       p.status,
       p.vida_atual,
       p.vida_total,
       p.cod_modelo_banda,
       p.altura_sulco_central_externo,
       p.dot,
       p.valor,
       p.data_hora_cadastro,
       p.pneu_novo_nunca_rodado,
       p.codigo,
       p.cod_empresa,
       p.cod_unidade_cadastro,
       p.origem_cadastro,
       p.cod_colaborador_cadastro,
       p.cod_colaborador_ultima_atualizacao
from pneu_data p
where p.deletado = false;

create or replace function func_pneu_atualiza(f_cod_cliente text,
                                              f_cod_modelo bigint,
                                              f_cod_dimensao bigint,
                                              f_cod_modelo_banda bigint,
                                              f_dot text,
                                              f_valor numeric,
                                              f_vida_total int,
                                              f_pressao_recomendada double precision,
                                              f_cod_original_pneu bigint,
                                              f_cod_unidade bigint,
                                              f_cod_colaborador_responsavel_edicao bigint)
    returns void
    language plpgsql
as
$$
declare
v_cod_cliente         text;
    v_cod_modelo          bigint;
    v_cod_dimensao        bigint;
    v_cod_modelo_banda    bigint;
    v_dot                 text;
    v_valor               numeric;
    v_vida_total          int;
    v_pressao_recomendada double precision;
    v_cod_unidade         bigint;
begin
select codigo_cliente,
       cod_modelo,
       cod_dimensao,
       cod_modelo_banda,
       coalesce(dot, ''),
       valor,
       vida_total,
       pressao_recomendada,
       cod_unidade
into strict v_cod_cliente,
        v_cod_modelo,
        v_cod_dimensao,
        v_cod_modelo_banda,
        v_dot,
        v_valor,
        v_vida_total,
        v_pressao_recomendada,
        v_cod_unidade
from pneu
where codigo = f_cod_original_pneu;

if v_cod_cliente != f_cod_cliente
        or v_cod_modelo != f_cod_modelo
        or v_cod_dimensao != f_cod_dimensao
        or v_cod_modelo_banda != f_cod_modelo_banda
        or v_dot != f_dot
        or v_valor != f_valor
        or v_vida_total != f_vida_total
        or v_pressao_recomendada != f_pressao_recomendada
        or v_cod_unidade != f_cod_unidade
    then
update pneu
set codigo_cliente                     = f_cod_cliente,
    cod_modelo                         = f_cod_modelo,
    cod_dimensao                       = f_cod_dimensao,
    cod_modelo_banda                   = f_cod_modelo_banda,
    dot                                = f_dot,
    valor                              = f_valor,
    vida_total                         = f_vida_total,
    pressao_recomendada                = f_pressao_recomendada,
    cod_colaborador_ultima_atualizacao = f_cod_colaborador_responsavel_edicao
where codigo = f_cod_original_pneu
  and cod_unidade = f_cod_unidade;
end if;
end
$$;