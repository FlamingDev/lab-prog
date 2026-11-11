import './CredenciaisDemo.css';

export function CredenciaisDemo({ email, senha }) {
  return (
    <div className="credenciais-demo">
      <p className="credenciais-demo__titulo">Credenciais de demonstração:</p>
      <p className="credenciais-demo__item">E-mail: {email}</p>
      <p className="credenciais-demo__item">Senha: {senha}</p>
    </div>
  );
}
