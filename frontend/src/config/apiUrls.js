/**
 * URL base do backend Spring Boot.
 * Pode ser sobrescrita via variável de ambiente VITE_API_BASE_URL no arquivo .env
 */
export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

/**
 * Centralizador de endpoints da API do backend.
 * Rotas estáticas são definidas como strings e rotas com parâmetros (UUID) como funções.
 */
export const API_URLS = {
  autenticacao: {
    login: `${API_BASE_URL}/api/auth/login`,
    cadastro: `${API_BASE_URL}/api/auth/cadastro`,
  },

  usuarios: {
    testeAutenticacao: `${API_BASE_URL}/api/users/teste`,
  },

  restaurantes: {
    base: `${API_BASE_URL}/api/restaurantes`,
    meus: `${API_BASE_URL}/api/restaurantes/meus`,
    porId: (restauranteId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}`,
    horarios: (restauranteId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/horarios`,
    abrirTemporariamente: (restauranteId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/abrir-temporariamente`,
    fecharTemporariamente: (restauranteId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/fechar-temporariamente`,
  },

  ingredientes: {
    base: (restauranteId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/ingredientes`,
    porId: (restauranteId, ingredienteId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/ingredientes/${ingredienteId}`,
    movimentacoes: (restauranteId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/ingredientes/movimentacoes`,
    ingredientesDaOpcao: (restauranteId, opcaoProdutoId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/ingredientes/opcoes/${opcaoProdutoId}`,
  },

  produtos: {
    base: (restauranteId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/produtos`,
    porId: (restauranteId, produtoId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/produtos/${produtoId}`,
    ingredientesDoProduto: (restauranteId, produtoId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/produtos/${produtoId}/ingredientes`,
  },

  cardapios: {
    base: (restauranteId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/cardapios`,
    porId: (restauranteId, cardapioId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/cardapios/${cardapioId}`,
    categorias: (restauranteId, cardapioId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/cardapios/${cardapioId}/categorias`,
    categoriaPorId: (restauranteId, cardapioId, categoriaId) =>
      `${API_BASE_URL}/api/restaurantes/${restauranteId}/cardapios/${cardapioId}/categorias/${categoriaId}`,
  },
};
