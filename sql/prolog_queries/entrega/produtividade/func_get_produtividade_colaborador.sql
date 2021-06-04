create or replace function func_get_produtividade_colaborador(f_mes integer, f_ano integer, f_cpf bigint)
    returns table
            (
                cod_unidade                integer,
                matricula_ambev            integer,
                data                       date,
                cpf                        bigint,
                nome_colaborador           character varying,
                data_nascimento            date,
                funcao                     character varying,
                cod_funcao                 bigint,
                nome_equipe                character varying,
                fator                      real,
                cargaatual                 character varying,
                entrega                    character varying,
                mapa                       integer,
                placa                      character varying,
                cxcarreg                   real,
                cxentreg                   real,
                qthlcarregados             real,
                qthlentregues              real,
                qtnfcarregadas             integer,
                qtnfentregues              integer,
                entregascompletas          integer,
                entregasnaorealizadas      integer,
                entregasparciais           integer,
                kmprevistoroad             real,
                kmsai                      integer,
                kmentr                     integer,
                tempoprevistoroad          bigint,
                hrsai                      timestamp without time zone,
                hrentr                     timestamp without time zone,
                tempo_rota                 bigint,
                tempointerno               bigint,
                hrmatinal                  time without time zone,
                apontamentos_ok            bigint,
                total_tracking             bigint,
                tempo_largada              bigint,
                meta_tracking              real,
                meta_tempo_rota_mapas      real,
                meta_caixa_viagem          real,
                meta_dev_hl                real,
                meta_dev_nf                real,
                meta_dev_pdv               real,
                meta_dispersao_km          real,
                meta_dispersao_tempo       real,
                meta_jornada_liquida_mapas real,
                meta_raio_tracking         real,
                meta_tempo_interno_mapas   real,
                meta_tempo_largada_mapas   real,
                meta_tempo_rota_horas      bigint,
                meta_tempo_interno_horas   bigint,
                meta_tempo_largada_horas   bigint,
                meta_jornada_liquida_horas bigint,
                rm_numero_viagens          smallint,
                diferenca_eld_soma_total   boolean,
                valor_rota                 real,
                valor_recarga              real,
                valor_diferenca_eld        double precision,
                valor_as                   real,
                valor                      double precision
            )
    language sql
as
$$
select *
from view_produtividade_extrato_com_total vpe
where vpe.data between func_get_data_inicio_produtividade(f_ano, f_mes, f_cpf, null) and
    func_get_data_fim_produtividade(f_ano, f_mes, f_cpf, null)
  and vpe.cod_unidade = (select c.cod_unidade from colaborador c where c.cpf = f_cpf)
  and vpe.cpf = f_cpf
order by vpe.data
$$;