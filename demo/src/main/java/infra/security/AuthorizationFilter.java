package infra.security;

import com.example.demo.restaurant.RestaurantApi;
import com.example.demo.usuario.UsuarioApi;
import com.example.demo.usuario.common.UsuarioRole;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.util.AntPathMatcher;
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
    private final RestaurantApi restaurantApi;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final String RESTAURANTE_PATH =
            "/api/restaurantes/{restauranteId}/**";
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

        // Por enquanto, autoriza qualquer GET a /api/restaurantes/{restauranteId}/**
        // TODO verificar se eh isso memo
        if (!PATH_MATCHER.match(RESTAURANTE_PATH, path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Map<String, String> variables =
                PATH_MATCHER.extractUriTemplateVariables(RESTAURANTE_PATH, path);

        UUID restauranteId;

        boolean autorizado;
        try {
            // valida se é um UUID
            restauranteId = UUID.fromString(variables.get("restauranteId"));
            // valida se é um UUID de um restaurante
            if (!restaurantApi.existePorId(restauranteId)) {
                throw new EntityNotFoundException("Restaurante não encontrado.");
            }
        } catch (Exception e) {
            // Não executa este filtro
            filterChain.doFilter(request, response);
            return;
        }

        var role = consultarRoleNoRestaurante(username, restauranteId);

        //if (!autorizado) {
            // Se não tem permissão para este restaurante, retorna 403
        //    log.warn("Usuário {} sem acesso administrativo ao restaurante {}", username, restauranteId);
        //    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado para este restaurante.");
        //    return;
        //}

        Authentication novoAuth = getNovoAuth(authentication, role);

        SecurityContextHolder.getContext().setAuthentication(novoAuth);
        // Deixa a requisição prosseguir para o controller
        filterChain.doFilter(request, response);
    }

    private Authentication getNovoAuth(Authentication authentication, UsuarioRole role) {
        // Se o usuário nao tem role no restaurante, apenas deixa a req seguir
        if (role == null) return authentication;

        List<GrantedAuthority> authoritiesAtualizadas = new ArrayList<>(authentication.getAuthorities());
        GrantedAuthority roleRestaurante = new SimpleGrantedAuthority("ROLE_" + role.name());
        authoritiesAtualizadas.add(roleRestaurante);

        return new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                authoritiesAtualizadas
        );
    }

    public UsuarioRole consultarRoleNoRestaurante(String username, UUID restauranteId){
        try{
            return usuarioApi.consultarRoleNoRestaurante(username, restauranteId);
        }
        catch(Exception e){
            return null;
        }
    }
}
