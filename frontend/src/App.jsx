import { AuthProvider } from './modules/autenticacao/contexts/AuthContext';
import { AppRoutes } from './routes/AppRoutes';

export function App() {
  return (
    <AuthProvider>
      <AppRoutes />
    </AuthProvider>
  );
}

export default App;
