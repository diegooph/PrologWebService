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