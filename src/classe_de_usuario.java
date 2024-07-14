import org.springframework.stereotype.Service;
@Service
public class classe_de_usuario {
    // class contents


    @Component
    public class JwtTokenUtil {
        //...
    }

    @Component
    public class AuthenticationFilter extends OncePerRequestFilter {
        //...
    }

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig extends WebSecurityConfigurerAdapter {
        //...
    }


    // Classe de usuário
    @Entity
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String username;
        private String password;
        private String role;

        // Getters e setters
    }

    // Repositório de usuários
    public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByUsername(String username);
    }

    // Serviço de autenticação
    @Service
    public class AuthService {
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private JwtTokenUtil jwtTokenUtil;

        public String authenticate(String username, String password) {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent() && user.get().getPassword().equals(password)) {
                return jwtTokenUtil.generateToken(user.get());
            } else {
                throw new AuthenticationException("Invalid username or password");
            }
        }
    }

    // Utilitário de token JWT
    @Component
    public class JwtTokenUtil {
        @Value("${jwt.secret}")
        private String secret;
        @Value("${jwt.expiration}")
        private Long expiration;

        public String generateToken(User user) {
            String token = Jwts.builder()
                    .setSubject(user.getUsername())
                    .claim("role", user.getRole())
                    .setExpiration(Duration.ofMinutes(expiration))
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();
            return token;
        }

        public boolean validateToken(String token) {
            try {
                Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    // Filtro de autenticação
    @Component
    public class AuthenticationFilter extends OncePerRequestFilter {
        @Autowired
        private JwtTokenUtil jwtTokenUtil;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String token = request.getHeader("Authorization");
            if (token != null && jwtTokenUtil.validateToken(token)) {
                Authentication authentication = jwtTokenUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        }
    }

    // Configuração de segurança
    @Configuration
    @EnableWebSecurity
    public class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        private AuthService authService;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .addFilter(new AuthenticationFilter())
                    .authorizeRequests()
                    .antMatchers("/topicos").hasRole("USER")
                    .anyRequest().permitAll()
                    .and()
                    .httpBasic();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(new UserDetailsService() {
                @Override
                public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                    Optional<User> user = authService.getUserByUsername(username);
                    if (user.isPresent()) {
                        return new User(user.get().getUsername(), user.get().getPassword(), getAuthorities(user.get().getRole()));
                    } else {
                        throw new UsernameNotFoundException("User not found");
                    }
                }
            });
        }

        private List<GrantedAuthority> getAuthorities(String role) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));
            return authorities;
        }
    }
}
