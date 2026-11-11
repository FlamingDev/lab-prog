import { API_URLS } from '@/config/apiUrls';

async function extrairMensagemErro(resposta, mensagemPadrao) {
  try {
    const corpo = await resposta.json();
    if (corpo?.validacoes?.length > 0) {
      return corpo.validacoes.map((v) => `${v.campo}: ${v.mensagem}`).join('\n');
    }
    if (corpo?.mensagem) {
      return corpo.mensagem;
    }
  } catch {
    // Caso a resposta de erro não seja JSON
  }
  return mensagemPadrao;
}

export async function listarIngredientes(restauranteId, token) {
  const resposta = await fetch(API_URLS.ingredientes.base(restauranteId), {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: 'application/json',
    },
  });

  if (!resposta.ok) {
    throw new Error(
      await extrairMensagemErro(resposta, 'Erro ao listar ingredientes do estoque.')
    );
  }

  return resposta.json();
}

export async function criarIngrediente(restauranteId, dados, token) {
  const resposta = await fetch(API_URLS.ingredientes.base(restauranteId), {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      nome: dados.nome,
      unidade: dados.unidade,
      custoUnidade: Number(dados.custoUnidade),
      quantidadeInicial: Number(dados.quantidadeInicial || 0),
    }),
  });

  if (!resposta.ok) {
    throw new Error(
      await extrairMensagemErro(resposta, 'Erro ao cadastrar ingrediente.')
    );
  }

  return resposta.json();
}

export async function atualizarIngrediente(
  restauranteId,
  ingredienteId,
  dados,
  token
) {
  const resposta = await fetch(
    API_URLS.ingredientes.porId(restauranteId, ingredienteId),
    {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        nome: dados.nome,
        custoUnidade: Number(dados.custoUnidade),
      }),
    }
  );

  if (!resposta.ok) {
    throw new Error(
      await extrairMensagemErro(resposta, 'Erro ao atualizar ingrediente.')
    );
  }

  return resposta.json();
}

export async function movimentarEstoqueIngrediente(
  restauranteId,
  ingredienteId,
  operacao,
  quantidade,
  token
) {
  const resposta = await fetch(
    API_URLS.ingredientes.movimentacoes(restauranteId),
    {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        ingredienteId,
        operacao,
        quantidade: Number(quantidade),
      }),
    }
  );

  if (!resposta.ok) {
    throw new Error(
      await extrairMensagemErro(resposta, 'Erro ao movimentar estoque do ingrediente.')
    );
  }
}

export async function excluirIngrediente(restauranteId, ingredienteId, token) {
  const resposta = await fetch(
    API_URLS.ingredientes.porId(restauranteId, ingredienteId),
    {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!resposta.ok) {
    throw new Error(
      await extrairMensagemErro(
        resposta,
        'Não foi possível excluir o ingrediente (verifique se ele está vinculado a algum produto).'
      )
    );
  }
}
