-- Cria tabela para internacionalização.
create table types.processo_evolucao_km_type
(
    processo               text    not null
        constraint pk_processo_type
            primary key,
    processo_legivel_pt_br text    not null,
    processo_legivel_es    text    not null,
    ativo                  boolean not null default true
);

-- Insere informações na tabela.
insert into types.processo_evolucao_km_type (processo, processo_legivel_pt_br, processo_legivel_es)
values ('MOVIMENTACAO', 'MOVIMENTAÇÃO', 'MOVIMIENTO'),
       ('AFERICAO', 'AFERIÇÃO', 'MEDIDA'),
       ('FECHAMENTO_SERVICO_PNEU', 'FECHAMENTO SERVICO PNEU', 'CERRAR SERVICIOS NEUMATICOS'),
       ('CHECKLIST', 'CHECKLIST', 'CHECKLIST'),
       ('FECHAMENTO_ITEM_CHECKLIST', 'FECHAMENTO ITEM CHECKLIST', 'CERRAR ELEMENTOS CHECKLIST'),
       ('TRANSFERENCIA_DE_VEICULOS', 'TRANSFERÊNCIA DE VEÍCULOS', 'TRANSFERENCIA DE VEHICULO');

-- Cria view.
create view types.processo_evolucao_km
    (processo, processo_legivel) as
select types.processo_evolucao_km_type.processo,
       f_if((select current_setting('lc_messages'::text) = 'es_es.UTF-8'::text),
            types.processo_evolucao_km_type.processo_legivel_es,
            types.processo_evolucao_km_type.processo_legivel_pt_br) as processo_legivel
from types.processo_evolucao_km_type
where types.processo_evolucao_km_type.ativo = true;

-- Histórico:
-- 2020-09-28 -> Function criada (thaisksf - PL-3172).
create or replace function func_veiculo_relatorio_evolucao_km_consolidado(f_cod_empresa bigint,
                                                                          f_cod_veiculo bigint)
    returns table
            (
                "PROCESSO"                               text,
                "CÓDIGO PROCESSO"                        text,
                "DATA/HORA"                              text,
                "PLACA"                                  text,
                "KM COLETADO"                            text,
                "VARIAÇÃO KM ENTRE COLETAS"              text,
                "KM ATUAL"                               text,
                "DIFERENÇA ENTRE KM ATUAL E KM COLETADO" text
            )
    language plpgsql
as
$$
begin
    return query
        select pek.processo_legivel ::text,
               func.cod_processo ::text,
               to_char(func.data_hora, 'DD/MM/YYYY HH24:MI')::text,
               func.placa ::text,
               func.km_coletado ::text,
               func.variacao_km_entre_coletas :: text,
               func.km_atual ::text,
               func.diferenca_km_atual_km_coletado ::text
        from func_veiculo_busca_evolucao_km_consolidado(
                     f_cod_empresa,
                     f_cod_veiculo,
                     null,
                     null) as func
                 join types.processo_evolucao_km pek on pek.processo = func.processo;
end;
$$;