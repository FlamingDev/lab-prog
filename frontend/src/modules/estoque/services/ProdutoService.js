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

export async function listarProdutos(restauranteId, token) {
  const resposta = await fetch(API_URLS.produtos.base(restauranteId), {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: 'application/json',
    },
  });

  if (!resposta.ok) {
    throw new Error(
      await extrairMensagemErro(resposta, 'Erro ao listar produtos e fichas técnicas.')
    );
  }

  return resposta.json();
}

export async function criarProduto(restauranteId, dados, token) {
  const resposta = await fetch(API_URLS.produtos.base(restauranteId), {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      nome: dados.nome,
      ingredientes: dados.ingredientes.map((item) => ({
        ingredienteId: item.ingredienteId,
        quantidade: Number(item.quantidade),
      })),
    }),
  });

  if (!resposta.ok) {
    throw new Error(
      await extrairMensagemErro(resposta, 'Erro ao cadastrar produto e ficha técnica.')
    );
  }

  return resposta.json();
}

export async function atualizarNomeProduto(restauranteId, produtoId, nome, token) {
  const resposta = await fetch(API_URLS.produtos.porId(restauranteId, produtoId), {
    method: 'PATCH',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ nome }),
  });

  if (!resposta.ok) {
    throw new Error(
      await extrairMensagemErro(resposta, 'Erro ao atualizar nome do produto.')
    );
  }

  return resposta.json();
}

export async function atualizarFichaTecnicaProduto(
  restauranteId,
  produtoId,
  ingredientes,
  token
) {
  const resposta = await fetch(
    API_URLS.produtos.ingredientesDoProduto(restauranteId, produtoId),
    {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        ingredientes: ingredientes.map((item) => ({
          ingredienteId: item.ingredienteId,
          quantidade: Number(item.quantidade),
        })),
      }),
    }
  );

  if (!resposta.ok) {
    throw new Error(
      await extrairMensagemErro(resposta, 'Erro ao atualizar ficha técnica do produto.')
    );
  }

  return resposta.json();
}
