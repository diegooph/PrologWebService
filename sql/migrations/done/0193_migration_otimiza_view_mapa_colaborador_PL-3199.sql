create index idx_mapa_filtro_mapa
    on mapa (cod_unidade, matricmotorista, matricajud1, matricajud2);

-- Antes:
-- Planning Time: 2.254 ms
-- Execution Time: 140486.882 ms

-- Depois:
-- Planning Time: 1.755 ms
-- Execution Time: 1939.865 ms

-- Para otimizar essa view foi criado o index 'idx_mapa_filtro_mapa', alterado 'union' para 'union all' e alterado a
-- ordem dos joins.
create or replace view view_mapa_colaborador as
select m.mapa        as mapa,
       c.cpf         as cpf,
       c.cod_unidade as cod_unidade
from unidade_funcao_produtividade ufp
         join colaborador c
              on c.cod_unidade = ufp.cod_unidade
                  and c.cod_funcao = ufp.cod_funcao_motorista
         join mapa m
              on m.cod_unidade = c.cod_unidade
                  and m.matricmotorista = c.matricula_ambev
union all
select m.mapa        as mapa,
       c.cpf         as cpf,
       c.cod_unidade as cod_unidade
from unidade_funcao_produtividade ufp
         join colaborador c
              on c.cod_unidade = ufp.cod_unidade
                  and c.cod_funcao = ufp.cod_funcao_ajudante
         join mapa m
              on m.cod_unidade = c.cod_unidade
                  and m.matricajud1 = c.matricula_ambev
union all
select m.mapa        as mapa,
       c.cpf         as cpf,
       c.cod_unidade as cod_unidade
from unidade_funcao_produtividade ufp
         join colaborador c
              on c.cod_unidade = ufp.cod_unidade
                  and c.cod_funcao = ufp.cod_funcao_ajudante
         join mapa m
              on m.cod_unidade = c.cod_unidade
                  and m.matricajud2 = c.matricula_ambev;

comment on view view_mapa_colaborador
    is 'View utilizada para linkar os mapas relizados por cada colaborador';
