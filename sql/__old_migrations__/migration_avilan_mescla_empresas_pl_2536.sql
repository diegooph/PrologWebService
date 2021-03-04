begin transaction ;

delete from integracao.empresa_integracao_sistema where cod_empresa = 2 and recurso_integrado = 'AFERICAO';
delete from integracao.empresa_integracao_sistema where cod_empresa = 2 and recurso_integrado = 'TIPO_VEICULO';
delete from integracao.empresa_integracao_sistema where cod_empresa = 2 and recurso_integrado = 'VEICULOS';

-- Altera as constraints para deferrable para que possamos mudar os dados na ordem que quisermos.
alter table veiculo_data alter constraint fk_veiculo_tipo deferrable initially immediate;
alter table veiculo_data alter constraint fk_veiculo_modelo_veiculo deferrable initially immediate;
alter table pneu_data alter constraint fk_pneu_modelo_banda deferrable initially immediate;
alter table pneu_data alter constraint fk_pneu_modelo deferrable initially immediate;
alter table modelo_banda alter constraint fk_modelo_banda_marca_banda deferrable initially immediate;
alter table movimentacao alter constraint fk_movimentacao_movimentacao_procecsso deferrable initially immediate;
alter table afericao_manutencao_data alter constraint fk_afericao_manutencao_movimentacao_processo deferrable initially immediate;
-- alter table marca_banda alter constraint fk_marca_banda_empresa deferrable initially immediate;

set constraints all deferred;

-- Migra os tipos de veículo.
update veiculo_tipo set nome = concat(nome, ' (2)') where cod_empresa = 2;
update veiculo_tipo set cod_empresa = 2 where cod_empresa = 54;

-- Migra os modelos de veículo.
update modelo_veiculo set nome = concat(nome, ' (2)') where cod_empresa = 2;
update modelo_veiculo set cod_empresa = 2 where cod_empresa = 54;

-- Migra os modelos de pneu e banda.
update modelo_pneu set cod_empresa = 2 where cod_empresa = 54;
update marca_banda set cod_empresa = 2 where cod_empresa = 54;
update modelo_banda set cod_empresa = 2 where cod_empresa = 54;


-- Deleta a única transferência que existe.
delete from veiculo_transferencia_informacoes where cod_processo_transferencia = 265;
delete from veiculo_transferencia_processo where codigo = 265;


-- Migra os dados das unidades.

-- 46, 2, Viamão
-- 291, 54, Viamão
-- Movs.
update movimentacao set cod_unidade = 46 where cod_unidade = 291;
update movimentacao_processo set cod_unidade = 46 where cod_unidade = 291;
-- Aferições.
update afericao_data set cod_unidade = 46 where cod_unidade = 291;
update afericao_manutencao_data set cod_unidade = 46 where cod_unidade = 291;
update afericao_valores set cod_unidade = 46 where cod_unidade = 291;
-- Veículo e pneus.
update veiculo_data set cod_unidade = 46, cod_unidade_cadastro = 46, cod_empresa = 2 where cod_unidade = 291;
update pneu_data set cod_unidade = 46, cod_unidade_cadastro = 46, cod_empresa = 2 where cod_unidade = 291;
update veiculo_pneu set cod_unidade = 46 where cod_unidade = 291;
update pneu_servico_realizado_data set cod_unidade = 46 where cod_unidade = 291;

-- 2, 2, Sapucaia do Sul
-- 318, 54, Sapucaia do Sul
-- Movs.
update movimentacao set cod_unidade = 2 where cod_unidade = 318;
update movimentacao_processo set cod_unidade = 2 where cod_unidade = 318;
-- Aferições.
update afericao_data set cod_unidade = 2 where cod_unidade = 318;
update afericao_manutencao_data set cod_unidade = 2 where cod_unidade = 318;
update afericao_valores set cod_unidade = 2 where cod_unidade = 318;
-- Veículo e pneus.
update veiculo_data set cod_unidade = 2, cod_unidade_cadastro = 2, cod_empresa = 2 where cod_unidade = 318;
update pneu_data set cod_unidade = 2, cod_unidade_cadastro = 2, cod_empresa = 2 where cod_unidade = 318;
update veiculo_pneu set cod_unidade = 2 where cod_unidade = 318;
update pneu_servico_realizado_data set cod_unidade = 2 where cod_unidade = 318;

-- 3, 2, Santa Maria
-- 311, 54, Santa Maria
-- Movs.
update movimentacao set cod_unidade = 3 where cod_unidade = 311;
update movimentacao_processo set cod_unidade = 3 where cod_unidade = 311;
-- Aferições.
update afericao_data set cod_unidade = 3 where cod_unidade = 311;
update afericao_manutencao_data set cod_unidade = 3 where cod_unidade = 311;
update afericao_valores set cod_unidade = 3 where cod_unidade = 311;
-- Veículo e pneus.
update veiculo_data set cod_unidade = 3, cod_unidade_cadastro = 3, cod_empresa = 2 where cod_unidade = 311;
update pneu_data set cod_unidade = 3, cod_unidade_cadastro = 3, cod_empresa = 2 where cod_unidade = 311;
update veiculo_pneu set cod_unidade = 3 where cod_unidade = 311;
update pneu_servico_realizado_data set cod_unidade = 3 where cod_unidade = 311;

-- 4, 2, Santa Cruz do Sul
-- 247, 54, Santa Cruz do Sul
-- Movs.
update movimentacao set cod_unidade = 4 where cod_unidade = 247;
update movimentacao_processo set cod_unidade = 4 where cod_unidade = 247;
-- Aferições.
update afericao_data set cod_unidade = 4 where cod_unidade = 247;
update afericao_manutencao_data set cod_unidade = 4 where cod_unidade = 247;
update afericao_valores set cod_unidade = 4 where cod_unidade = 247;
-- Veículo e pneus.
update veiculo_data set cod_unidade = 4, cod_unidade_cadastro = 4, cod_empresa = 2 where cod_unidade = 247;
update pneu_data set cod_unidade = 4, cod_unidade_cadastro = 4, cod_empresa = 2 where cod_unidade = 247;
update veiculo_pneu set cod_unidade = 4 where cod_unidade = 247;
update pneu_servico_realizado_data set cod_unidade = 4 where cod_unidade = 247;

-- 109, 2, Cascavel
-- 369, 54, Cascavel
-- Movs.
update movimentacao set cod_unidade = 109 where cod_unidade = 369;
update movimentacao_processo set cod_unidade = 109 where cod_unidade = 369;
-- Aferições.
update afericao_data set cod_unidade = 109 where cod_unidade = 369;
update afericao_manutencao_data set cod_unidade = 109 where cod_unidade = 369;
update afericao_valores set cod_unidade = 109 where cod_unidade = 369;
-- Veículo e pneus.
update veiculo_data set cod_unidade = 109, cod_unidade_cadastro = 109, cod_empresa = 2 where cod_unidade = 369;
update pneu_data set cod_unidade = 109, cod_unidade_cadastro = 109, cod_empresa = 2 where cod_unidade = 369;
update veiculo_pneu set cod_unidade = 109 where cod_unidade = 369;
update pneu_servico_realizado_data set cod_unidade = 109 where cod_unidade = 369;


-- Desloga os colaboradores.
delete
from token_autenticacao
where cod_colaborador in (select cd.codigo from colaborador_data cd where cd.cod_empresa = 54);

end transaction ;

-- Em outra transacation, retornarmos as constraints para o estado original.
begin transaction ;

alter table veiculo_data alter constraint fk_veiculo_tipo not deferrable;
alter table veiculo_data alter constraint fk_veiculo_modelo_veiculo not deferrable;
alter table pneu_data alter constraint fk_pneu_modelo_banda not deferrable;
alter table pneu_data alter constraint fk_pneu_modelo not deferrable;
alter table modelo_banda alter constraint fk_modelo_banda_marca_banda not deferrable;
alter table movimentacao alter constraint fk_movimentacao_movimentacao_procecsso not deferrable;
alter table afericao_manutencao_data alter constraint fk_afericao_manutencao_movimentacao_processo not deferrable;

end transaction ;