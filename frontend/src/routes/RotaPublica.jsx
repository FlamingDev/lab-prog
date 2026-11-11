import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/modules/autenticacao/contexts/AuthContext';

export function RotaPublica() {
  const { estaAutenticado } = useAuth();

  if (estaAutenticado) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}
