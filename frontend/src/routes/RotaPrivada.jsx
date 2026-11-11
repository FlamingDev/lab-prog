import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '@/modules/autenticacao/contexts/AuthContext';

export function RotaPrivada() {
  const { estaAutenticado } = useAuth();
  const localizacao = useLocation();

  if (!estaAutenticado) {
    return <Navigate to="/login" state={{ origem: localizacao }} replace />;
  }

  return <Outlet />;
}
