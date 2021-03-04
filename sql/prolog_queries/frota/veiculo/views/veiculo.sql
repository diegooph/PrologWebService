-- View simples selecionando apenas os veículos não deletados.
--
-- Histórico:
-- 2019-10-04 -> Adiciona cod_empresa (luizfp - PL-2276).
-- 2020-02-25 -> Adiciona cod_diagrama (wvinim - PL-1965).
-- 2020-09-04 -> Adiciona identificador_frota e foi_editado (luiz_fp - PL-3096).
-- 2020-11-09 -> Adiciona possui_hubodometro (steinert999 - PL-3223).
-- 2020-11-10 -> Adiciona motorizado (steiner999 - PL-3223).
-- 2020-11-20 -> Adiciona flag acoplado (thaisksf - PL-3320).
create or replace view veiculo
as
select v.codigo,
       v.placa,
       v.identificador_frota,
       v.cod_unidade,
       v.cod_empresa,
       v.km,
       v.status_ativo,
       v.cod_diagrama,
       v.cod_tipo,
       v.cod_modelo,
       v.cod_eixos,
       v.data_hora_cadastro,
       v.cod_unidade_cadastro,
       v.foi_editado,
       v.possui_hubodometro,
       v.motorizado,
       v.acoplado
from veiculo_data v
where v.deletado = false;