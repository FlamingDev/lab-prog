import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { PaginaLogin } from '@/modules/autenticacao/pages/PaginaLogin/PaginaLogin';
import { PaginaEstoque } from '@/modules/estoque/pages/PaginaEstoque/PaginaEstoque';
import { RotaPrivada } from './RotaPrivada';
import { RotaPublica } from './RotaPublica';

export function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Rotas públicas (acessíveis apenas para quem AINDA NÃO se autenticou) */}
        <Route element={<RotaPublica />}>
          <Route path="/login" element={<PaginaLogin />} />
        </Route>

        {/* Rotas privadas (exigem autenticação; se não estiver logado, redireciona para /login) */}
        <Route element={<RotaPrivada />}>
          <Route path="/" element={<PaginaEstoque />} />
          <Route path="/estoque" element={<PaginaEstoque />} />
        </Route>

        {/* Qualquer rota desconhecida redireciona para a raiz */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
