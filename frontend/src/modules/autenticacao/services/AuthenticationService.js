import { API_URLS } from '@/config/apiUrls';

export async function realizarLogin(emailOuCpf, senha) {
  const resposta = await fetch(API_URLS.autenticacao.login, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      emailOuCpf,
      senha
    })
  });

  if (!resposta.ok) {
    throw new Error('E-mail ou senha inválidos.');
  }

  const dados = await resposta.json();

  return dados.token;
}