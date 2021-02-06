-- Veículo.
select vd.row_log -> 'codigo' as cod_veiculo,
       vd.data_hora_utc       as data_hora_log,
       vd.operacao            as operacao,
       (jsonb_populate_record(NULL::veiculo_data, vd.row_log)).*
from audit.veiculo_data_audit vd
-- where vd.row_log ->> 'placa' = 'FZM3126'
order by vd.row_log -> 'codigo', vd.data_hora_utc asc;


-- Colaborador.
select cd.row_log -> 'codigo' as cod_colaborador,
       cd.data_hora_utc       as data_hora_log,
       cd.operacao            as operacao,
       (jsonb_populate_record(NULL::colaborador_data, cd.row_log)).*
from audit.colaborador_data_audit cd
where cd.row_log ->> 'cod_empresa' = '2'
order by cd.row_log -> 'codigo', cd.data_hora_utc asc;

-- Veículo Pneu.
select vpa.row_log -> 'placa'    as placa,
       vpa.row_log -> 'cod_pneu' as cod_pneu,
       vpa.row_log -> 'posicao'  as posicao,
       vpa.data_hora_utc         as data_hora_log,
       vpa.operacao              as operacao,
       (jsonb_populate_record(NULL::veiculo_pneu, vpa.row_log)).*
from audit.veiculo_pneu_audit vpa
where vpa.row_log ->> 'cod_pneu' = ?;


-- Unidade.
select vpa.row_log -> 'nome'        as nome,
       vpa.row_log -> 'codigo'      as codigo,
       vpa.row_log -> 'cod_empresa' as cod_empresa,
       vpa.data_hora_utc            as data_hora_log,
       vpa.operacao                 as operacao,
       vpa.pg_username              as pg_username,
       (jsonb_populate_record(NULL::unidade, vpa.row_log)).*
from audit.unidade_audit vpa
where (vpa.row_log ->> 'codigo')::bigint = ?;