-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Ao receber os dados da movimentação através do servidor, a function realiza a inserção de uma nova movimentação
-- em movimentacao_origem.
-- Para suprir a nova demanda da tabela movimentacao_origem possuir a coluna cod_diagrama, a function realiza a busca
-- do cod_diagrama do veículo para que seja inserido.
-- Após inserir a nova movimentação, é retornando o cod_movimentacao_realizada.

-- Précondições:
--
-- Histórico:
-- 2019-11-29 -> Function criada (natanrotta - PL-1899).
-- 2020-09-23 -> Adiciona cod_veiculo (thaisksf - PL-3170).
-- 2020-11-23 -> Modifica update de km na function (gustavocnp95 - PL-3290).
-- 2020-12-16 -> Corrige propagação de km na function (gustavocnp95|thaisksf - PL-3367).
create or replace function func_movimentacao_insert_movimentacao_veiculo_origem(f_cod_pneu bigint,
                                                                                f_cod_unidade bigint,
                                                                                f_tipo_origem varchar(255),
                                                                                f_cod_movimentacao bigint,
                                                                                f_placa_veiculo varchar(7),
                                                                                f_posicao_prolog integer)
    returns void
    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo           bigint;
    v_cod_veiculo                bigint;
    v_cod_diagrama_veiculo       bigint;
    v_km_atual                   bigint;
    v_tipo_origem_atual          varchar(255) := (select p.status
                                                  from pneu p
                                                  where p.codigo = f_cod_pneu
                                                    and p.cod_unidade = f_cod_unidade
                                                    and f_tipo_origem in (select p.status
                                                                          from pneu p
                                                                          where p.codigo = f_cod_pneu
                                                                            and p.cod_unidade = f_cod_unidade));
    f_cod_movimentacao_realizada bigint;
begin
    select v.codigo,
           v.cod_tipo,
           v.cod_diagrama,
           v.km
    from veiculo_data v
    where v.placa = f_placa_veiculo
    into strict
        v_cod_veiculo,
        v_cod_tipo_veiculo,
        v_cod_diagrama_veiculo,
        v_km_atual;

    --REALIZA INSERÇÃO DA MOVIMENTAÇÃO ORIGEM
    insert into movimentacao_origem(cod_movimentacao,
                                    tipo_origem,
                                    placa,
                                    km_veiculo,
                                    posicao_pneu_origem,
                                    cod_diagrama,
                                    cod_veiculo)
    values (f_cod_movimentacao,
            v_tipo_origem_atual,
            f_placa_veiculo,
            v_km_atual,
            f_posicao_prolog,
            v_cod_diagrama_veiculo,
            v_cod_veiculo)
    returning cod_movimentacao into f_cod_movimentacao_realizada;

    if (f_cod_movimentacao_realizada <= 0)
    then
        perform throw_generic_error('Erro ao inserir a origem veiculo da movimentação');
    end if;
end
$$;