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