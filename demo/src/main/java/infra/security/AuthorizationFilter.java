package infra.security;

import com.example.demo.usuario.UsuarioApi;
import com.example.demo.usuario.common.UsuarioRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {
    private final UsuarioApi usuarioApi;

    // Define o padrão com o placeholder desejado
    private static final UriTemplate RESTAURANTE_URI_TEMPLATE =
            new UriTemplate("/api/restaurante/{restauranteId}/**");
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        // Se usuário ainda não se autenticou, não executa este filtro
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // identificador do usuario
        String username = authentication.getName();
        // metodo http
        String method = request.getMethod();
        // url
        String path = request.getRequestURI();

        logger.info("Usuário: " + username);
        logger.info("Método: " + method);
        logger.info("Endpoint: " + path);

        // Se a URL não envolve restaurante, não executa este filtro
        if (!RESTAURANTE_URI_TEMPLATE.matches(path)){
            filterChain.doFilter(request, response);
            return;
        }

        // descobrir restaurante
        Map<String, String> variables = RESTAURANTE_URI_TEMPLATE.match(path);
        UUID restauranteId = UUID.fromString(variables.get("restauranteId"));
        // consultar permissão
        boolean autorizado = consultarRoleNoRestaurante(username, restauranteId);
        // decidir
        if (!autorizado) {
            // Se não tem permissão para este restaurante, retorna 403
            log.warn("Usuário {} sem acesso administrativo ao restaurante {}", username, restauranteId);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado para este restaurante.");
            return;
        }

        Authentication novoAuth = getNovoAuth(authentication);

        SecurityContextHolder.getContext().setAuthentication(novoAuth);

        // Deixa a requisição prosseguir para o controller
        filterChain.doFilter(request, response);
    }

    private Authentication getNovoAuth(Authentication authentication) {
        List<GrantedAuthority> authoritiesAtualizadas = new ArrayList<>(authentication.getAuthorities());

        // Adiciona a autoridade de ADMIN
        GrantedAuthority roleRestaurante = new SimpleGrantedAuthority("ROLE_ADMIN");
        authoritiesAtualizadas.add(roleRestaurante);

        return new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                authoritiesAtualizadas
        );
    }

    public boolean consultarRoleNoRestaurante(String username, UUID restauranteId){
        // Por enquanto, somente usuarios Admin podem tomar ações sobre o restaurante
        return usuarioApi.consultarRoleNoRestaurante(username, restauranteId) == UsuarioRole.ADMIN;
    }
}
