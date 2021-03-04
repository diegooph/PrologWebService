--Adiciona estepes na tabela pneu_ordem_nomenclatura_unidade, e seus respectivos nomes, para todas as unidades da empresa

BEGIN TRANSACTION ;
insert into pneu_ordem_nomenclatura_unidade
  select vt.codigo, vt.cod_unidade, 900, 'Z1'
  from veiculo_tipo vt
  where vt.cod_unidade in (select u.codigo from unidade u where u.cod_empresa = 9 /*COD_EMPRESA_AQUI*/);

insert into pneu_ordem_nomenclatura_unidade
  select vt.codigo, vt.cod_unidade, 901, 'Z2'
  from veiculo_tipo vt
  where vt.cod_unidade in (select u.codigo from unidade u where u.cod_empresa = 9 /*COD_EMPRESA_AQUI*/);

insert into pneu_ordem_nomenclatura_unidade
  select vt.codigo, vt.cod_unidade, 902, 'Z3'
  from veiculo_tipo vt
  where vt.cod_unidade in (select u.codigo from unidade u where u.cod_empresa = 9 /*COD_EMPRESA_AQUI*/);

insert into pneu_ordem_nomenclatura_unidade
  select vt.codigo, vt.cod_unidade, 903, 'Z4'
  from veiculo_tipo vt
  where vt.cod_unidade in (select u.codigo from unidade u where u.cod_empresa = 9 /*COD_EMPRESA_AQUI*/);

insert into pneu_ordem_nomenclatura_unidade
  select vt.codigo, vt.cod_unidade, 904, 'Z5'
  from veiculo_tipo vt
  where vt.cod_unidade in (select u.codigo from unidade u where u.cod_empresa = 9 /*COD_EMPRESA_AQUI*/);
END TRANSACTION ;