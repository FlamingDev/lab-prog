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

export async function listarCategorias(restauranteId, token) {
  const resposta = await fetch(API_URLS.categorias.base(restauranteId), {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: 'application/json',
    },
  });

  if (!resposta.ok) {
    throw new Error(
      await extrairMensagemErro(resposta, 'Erro ao listar categorias do restaurante.')
    );
  }

  return resposta.json();
}

export async function criarCategoria(restauranteId, dados, token) {
  const resposta = await fetch(API_URLS.categorias.base(restauranteId), {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      nome: dados.nome,
      descricao: dados.descricao || null,
    }),
  });

  if (!resposta.ok) {
    throw new Error(
      await extrairMensagemErro(resposta, 'Erro ao cadastrar categoria.')
    );
  }

  return resposta.json();
}

export async function atualizarCategoria(
  restauranteId,
  categoriaId,
  dados,
  token
) {
  const resposta = await fetch(
    API_URLS.categorias.porId(restauranteId, categoriaId),
    {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        nome: dados.nome,
        descricao: dados.descricao || null,
      }),
    }
  );

  if (!resposta.ok) {
    throw new Error(
      await extrairMensagemErro(resposta, 'Erro ao atualizar categoria.')
    );
  }

  return resposta.json();
}

export async function excluirCategoria(restauranteId, categoriaId, token) {
  const resposta = await fetch(
    API_URLS.categorias.porId(restauranteId, categoriaId),
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
        'Não foi possível excluir a categoria.'
      )
    );
  }
}
