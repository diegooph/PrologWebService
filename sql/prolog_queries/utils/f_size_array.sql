-- Sobre:
--
-- Function auxiliar para buscar o tamanho de arrays mesmo estes sendo nulos. O 'array_length' retorna null caso o
-- array que ele está operando é null, isso pode acabar gerando possíveis bugs na lógica, pois nem sempre é tratada
-- essa situação nas implementações.
--
-- Para cenários onde o array recebido é null, a function irá retornar zero. Caso o array estiver vazio a function irá
-- retornar zero e para o caso de o array possuir elementos, o retorno será a contagem de elementos do array.
--
-- Histórico:
-- 2020-04-06 -> Function criada (diogenesvanzella - PLI-114).
create function f_size_array(anyarray) returns integer
    language plpgsql
as
$$
begin
    return coalesce(array_length($1, 1), 0);
end;
$$;