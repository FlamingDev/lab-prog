import { API_URLS } from '@/config/apiUrls';

/**
 * Consulta GET /api/restaurantes/meus e retorna o primeiro restaurante encontrado do usuário logado.
 */
export async function buscarPrimeiroRestauranteDoUsuario(token) {
  const resposta = await fetch(API_URLS.restaurantes.meus, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: 'application/json',
    },
  });

  if (!resposta.ok) {
    throw new Error('Não foi possível consultar os restaurantes do usuário.');
  }

  const restaurantes = await resposta.json();
  if (!Array.isArray(restaurantes) || restaurantes.length === 0) {
    return null;
  }

  return restaurantes[0];
}
