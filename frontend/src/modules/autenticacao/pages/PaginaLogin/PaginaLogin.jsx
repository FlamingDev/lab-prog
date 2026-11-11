import { Card } from '@/components/ui';
import { CredenciaisDemo } from '@/modules/autenticacao/components/CredenciaisDemo/CredenciaisDemo';
import { FormularioLogin } from '@/modules/autenticacao/components/FormularioLogin/FormularioLogin';
//import { LogoRestaurante } from '@/modules/autenticacao/components/LogoRestaurante/LogoRestaurante';
import './PaginaLogin.css';

const CREDENCIAIS_DEMO = {
  email: '05316187300',
  senha: '12345678',
};

export function PaginaLogin() {
  return (
    <main className="pagina-login">
      <Card className="pagina-login__cartao">
        {/*<LogoRestaurante nomeMarca="Hardees" />*/}

        <header className="pagina-login__cabecalho">
          <h1 className="pagina-login__titulo">Sistema de Gestão de Restaurantes</h1>
          <p className="pagina-login__subtitulo">Entre na sua conta</p>
        </header>

        <FormularioLogin emailPlaceholder={CREDENCIAIS_DEMO.email} />

        <CredenciaisDemo
          email={CREDENCIAIS_DEMO.email}
          senha={CREDENCIAIS_DEMO.senha}
        />
      </Card>
    </main>
  );
}
