-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Ao receber os dados da movimentação através do servidor, a function realiza a inserção de uma nova movimentação
-- em movimentacao_destino.
-- Para suprir a nova demanda da tabela movimentacao_destino possuir a coluna de cod_diagrama, a function realiza
-- a busca do cod_diagrama do veículo para que seja inserido.
-- Após inserir a nova movimentação, é retornando o cod_movimentacao_realizada.

-- Précondições:
--
-- Histórico:
-- 2019-11-29 -> Function criada (natanrotta - PL-1899).
-- 2020-09-23 -> Adiciona cod_veiculo (thaisksf - PL-3170).
-- 2020-12-15 -> Adiciona lógica para propagação de km (thaisksf - PL-3124).
create or replace function func_movimentacao_insert_movimentacao_veiculo_destino(f_cod_movimentacao bigint,
                                                                                 f_tipo_destino varchar(255),
                                                                                 f_placa_veiculo varchar(255),
                                                                                 f_posicao_prolog bigint)
    returns void
    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo           bigint;
    v_cod_diagrama_veiculo       bigint;
    v_cod_movimentacao_realizada bigint;
    v_cod_veiculo                bigint;
    v_km_atual                   bigint;
begin
    select v.codigo,
           v.cod_tipo,
           v.cod_diagrama,
           v.km
    from veiculo_data v
    where v.placa = f_placa_veiculo
    into strict v_cod_veiculo,
        v_cod_tipo_veiculo,
        v_cod_diagrama_veiculo,
        v_km_atual;

    --REALIZA INSERÇÃO DA MOVIMENTAÇÃO DESTINO.
    insert into movimentacao_destino(cod_movimentacao,
                                     tipo_destino,
                                     placa,
                                     km_veiculo,
                                     posicao_pneu_destino,
                                     cod_motivo_descarte,
                                     url_imagem_descarte_1,
                                     url_imagem_descarte_2,
                                     url_imagem_descarte_3,
                                     cod_recapadora_destino,
                                     cod_coleta,
                                     cod_diagrama,
                                     cod_veiculo)
    values (f_cod_movimentacao,
            f_tipo_destino,
            f_placa_veiculo,
            v_km_atual,
            f_posicao_prolog,
            null,
            null,
            null,
            null,
            null,
            null,
            v_cod_diagrama_veiculo,
            v_cod_veiculo)
    returning cod_movimentacao into v_cod_movimentacao_realizada;

    if (v_cod_movimentacao_realizada <= 0)
    then
        perform throw_generic_error('Erro ao inserir o destino veiculo da movimentação');
    end if;
end
$$;