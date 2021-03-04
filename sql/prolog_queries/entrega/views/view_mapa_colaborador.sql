-- Sobre:
--
-- View responsável por buscar os mapas apenas de colaboradores que estejam cadastrados no Prolog.
-- São trazidos os mapas de motoristas e também os de ajudantes.
--
-- Para identificar se um mapa tem um colaborador vinculado no Prolog, é preciso que o código da unidade do colaborador
-- e a matrícula ambev tenham um correspondente na tabela mapa.
--
-- Perceba que um mapa pode retornar mais de uma vez, sendo o máximo de 3: uma para o motorista e outras duas para os
-- dois ajudantes.
--
-- Histórico:
-- 2019-02-15 -> View criada (thaisksf - PL-985).
-- 2020-10-17 -> View otimizada — alterado ordem dos joins e usado 'union all' ao invés de 'union' (luizfp - PL-3199).
-- 2020-10-23 -> Correção: existem casos em que podem vir dados duplicados, precisamos manter com 'union'
--               (luizfp - PL-3199).
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
union
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
union
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

