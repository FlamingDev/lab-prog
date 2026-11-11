import { useState } from 'react';
import { Button, InputField } from '@/components/ui';
import './FormularioLogin.css';
import { realizarLogin } from '@/modules/autenticacao/services/AuthenticationService';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/modules/autenticacao/contexts/AuthContext';

export function FormularioLogin({ emailPlaceholder = 'breno.vidigal@a.com' }) {
  const [username, setUsername] = useState('');
  const [senha, setSenha] = useState('');
  const { salvarSessao } = useAuth();

  const navigate = useNavigate();


  async function aoSubmeter(evento) {
    evento.preventDefault();
    try {
      var token = await realizarLogin(username, senha)
      salvarSessao(token);
      navigate('/');
    }
    catch (erro) {
      alert("Ocorreu um erro: " + erro)
    }
  }

  return (
    <form className="formulario-login" onSubmit={aoSubmeter} noValidate>
      <InputField
        id="campo-username"
        label="E-mail ou CPF"
        type="text"
        placeholder={emailPlaceholder}
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        autoComplete="username"
      />

      <InputField
        id="campo-senha"
        label="Senha"
        type="password"
        placeholder="Digite sua senha"
        value={senha}
        onChange={(e) => setSenha(e.target.value)}
        autoComplete="current-password"
      />

      <div className="formulario-login__acoes">
        <Button type="submit" variant="primario" larguraTotal>
          Entrar
        </Button>
      </div>
    </form>
  );
}
