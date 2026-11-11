import { createContext, useContext, useMemo, useState } from 'react';
import { jwtDecode } from 'jwt-decode';

const CHAVE_TOKEN_STORAGE = 'token';

const AuthContext = createContext(null);

/**
 * Retorna true se o token for decodificável e ainda não tiver expirado.
 */
function tokenEstaValido(token) {
  if (!token) return false;

  try {
    const { exp } = jwtDecode(token);
    if (exp === undefined) return true;
    return exp * 1000 > Date.now(); // exp vem em segundos
  } catch {
    return false; // token malformado
  }
}

function extrairUsuarioDoToken(token) {
  if (!tokenEstaValido(token)) return null;

  try {
    const { sub } = jwtDecode(token);
    const identificador = sub || 'Usuário';
    return {
      identificador,
      inicial: identificador.trim().charAt(0).toUpperCase(),
    };
  } catch {
    return null; // token malformado
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem(CHAVE_TOKEN_STORAGE));

  const usuarioLogado = useMemo(() => extrairUsuarioDoToken(token), [token]);
  const estaAutenticado = Boolean(usuarioLogado);

  function salvarSessao(novoToken) {
    localStorage.setItem(CHAVE_TOKEN_STORAGE, novoToken);
    setToken(novoToken);
  }

  function encerrarSessao() {
    localStorage.removeItem(CHAVE_TOKEN_STORAGE);
    setToken(null);
  }

  return (
    <AuthContext.Provider
      value={{ token, usuarioLogado, estaAutenticado, salvarSessao, encerrarSessao }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const contexto = useContext(AuthContext);
  if (!contexto) {
    throw new Error('useAuth deve ser utilizado dentro de um AuthProvider.');
  }
  return contexto;
}