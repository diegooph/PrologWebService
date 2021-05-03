-- Sobre:
--
-- Se o veículo possuir aferições, todas as que possuem serviço em aberto desse veículo são deletadas logicamente.
-- As OSs de checklist que estiverem em aberto também serão deletadas. Mesmo se já possuírem algum item resolvido.
-- Também atualizamos as informações na tabela de integração.
--
-- Apesar dessa function atualizar o veículo, como a empresa dele muda, optamos por não gerar um histórico de edição.
-- Consideramos o seguinte cenário: o veículo está na empresa A e foi editado no dia 1. Ficou na empresa A até o dia 10
-- tendo checklists e aferições realizadas (o KM foi registrado nos próprios processos). No dia 11 foi transferido para
-- a empresa B. Caso salvássemos um histórico nesse momento, ele só seria útil se no futuro o veículo voltasse para a
-- empresa A e se, nessa volta, fosse atribuído diferentes valores de cod_tipo, cod_modelo ou cod_diagrama. Isso porque,
-- se fossem atribuídos os mesmos vínculos, nada seria perdido (o KM ainda poderia ser diferente mas ele não é
-- relevante neste caso já que um veículo só pode ser transferido sem pneus, o que restringe o impacto negativo de
-- perder KM).
-- Por tudo isso, resolvemos deixar passar este cenário.
create or replace function suporte.func_veiculo_transfere_veiculo_entre_empresas(f_placa_veiculo varchar(7),
                                                                                 f_cod_empresa_origem bigint,
                                                                                 f_cod_unidade_origem bigint,
                                                                                 f_cod_empresa_destino bigint,
                                                                                 f_cod_unidade_destino bigint,
                                                                                 f_cod_modelo_veiculo_destino bigint,
                                                                                 f_cod_tipo_veiculo_destino bigint,
                                                                                 out veiculo_transferido text)
    returns text
    language plpgsql
    security definer
as
$$
begin
    perform suporte.func_historico_salva_execucao();
    raise exception 'O veiculo pode ser cadastrado com a mesma placa na nova empresa. Não é necessário utilizar esse procedimento.';
end
$$;