// package buy01.user_service;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.security.Keys;

// import jakarta.servlet.FilterChain;
// import buy01.user_service.security.JwtAuthFilter;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import org.springframework.mock.web.MockHttpServletRequest;
// import org.springframework.mock.web.MockHttpServletResponse;
// import org.springframework.security.core.context.SecurityContextHolder;

// import java.nio.charset.StandardCharsets;
// import java.util.Date;

// import javax.crypto.SecretKey;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// class JwtAuthFilterTest {

//     private JwtAuthFilter filter;

//     private FilterChain filterChain;

//     private MockHttpServletRequest request;

//     private MockHttpServletResponse response;

//     private static final String SECRET_KEY =
//             "mysecretkeymysecretkeymysecretkey123456789012345678901234567890";

//     private SecretKey key() {
//         return Keys.hmacShaKeyFor(
//                 SECRET_KEY.getBytes(StandardCharsets.UTF_8)
//         );
//     }

//     @BeforeEach
//     void setUp() {

//         filter = new JwtAuthFilter();

//         filterChain = mock(FilterChain.class);

//         request = new MockHttpServletRequest();

//         response = new MockHttpServletResponse();

//         SecurityContextHolder.clearContext();
//     }

//     @Test
//     void requestWithoutAuthorization_shouldContinueChain()
//             throws Exception {

//         filter.doFilter(
//                 request,
//                 response,
//                 filterChain
//         );

//         verify(filterChain).doFilter(
//                 request,
//                 response
//         );

//         assertNull(
//                 SecurityContextHolder
//                         .getContext()
//                         .getAuthentication()
//         );
//     }

//     @Test
//     void requestWithNonBearerToken_shouldContinueChain()
//             throws Exception {

//         request.addHeader(
//                 "Authorization",
//                 "Basic abc123"
//         );

//         filter.doFilter(
//                 request,
//                 response,
//                 filterChain
//         );

//         verify(filterChain).doFilter(
//                 request,
//                 response
//         );

//         assertNull(
//                 SecurityContextHolder
//                         .getContext()
//                         .getAuthentication()
//         );
//     }

//     @Test
//     void validClientToken_shouldAuthenticateUser()
//             throws Exception {

//         String token = Jwts.builder()
//                 .subject("user-123")
//                 .claim("role", "CLIENT")
//                 .issuedAt(new Date())
//                 .expiration(
//                         new Date(
//                                 System.currentTimeMillis()
//                                         + 60000
//                         )
//                 )
//                 .signWith(key())
//                 .compact();

//         request.addHeader(
//                 "Authorization",
//                 "Bearer " + token
//         );

//         filter.doFilter(
//                 request,
//                 response,
//                 filterChain
//         );

//         assertNotNull(
//                 SecurityContextHolder
//                         .getContext()
//                         .getAuthentication()
//         );

//         assertEquals(
//                 "user-123",
//                 SecurityContextHolder
//                         .getContext()
//                         .getAuthentication()
//                         .getPrincipal()
//         );

//         assertTrue(
//                 SecurityContextHolder
//                         .getContext()
//                         .getAuthentication()
//                         .getAuthorities()
//                         .stream()
//                         .anyMatch(a ->
//                                 a.getAuthority()
//                                         .equals("ROLE_CLIENT")
//                         )
//         );

//         verify(filterChain).doFilter(
//                 request,
//                 response
//         );
//     }

//     @Test
//     void validSellerToken_shouldAuthenticateSeller()
//             throws Exception {

//         String token = Jwts.builder()
//                 .subject("seller-123")
//                 .claim("role", "SELLER")
//                 .issuedAt(new Date())
//                 .expiration(
//                         new Date(
//                                 System.currentTimeMillis()
//                                         + 60000
//                         )
//                 )
//                 .signWith(key())
//                 .compact();

//         request.addHeader(
//                 "Authorization",
//                 "Bearer " + token
//         );

//         filter.doFilter(
//                 request,
//                 response,
//                 filterChain
//         );

//         assertNotNull(
//                 SecurityContextHolder
//                         .getContext()
//                         .getAuthentication()
//         );

//         assertTrue(
//                 SecurityContextHolder
//                         .getContext()
//                         .getAuthentication()
//                         .getAuthorities()
//                         .stream()
//                         .anyMatch(a ->
//                                 a.getAuthority()
//                                         .equals("ROLE_SELLER")
//                         )
//         );
//     }

//     @Test
//     void invalidRole_shouldNotAuthenticate()
//             throws Exception {

//         String token = Jwts.builder()
//                 .subject("user-123")
//                 .claim("role", "ADMIN")
//                 .issuedAt(new Date())
//                 .expiration(
//                         new Date(
//                                 System.currentTimeMillis()
//                                         + 60000
//                         )
//                 )
//                 .signWith(key())
//                 .compact();

//         request.addHeader(
//                 "Authorization",
//                 "Bearer " + token
//         );

//         filter.doFilter(
//                 request,
//                 response,
//                 filterChain
//         );

//         assertNull(
//                 SecurityContextHolder
//                         .getContext()
//                         .getAuthentication()
//         );

//         verify(filterChain).doFilter(
//                 request,
//                 response
//         );
//     }

//     @Test
//     void malformedToken_shouldNotAuthenticate()
//             throws Exception {

//         request.addHeader(
//                 "Authorization",
//                 "Bearer invalid-token"
//         );

//         filter.doFilter(
//                 request,
//                 response,
//                 filterChain
//         );

//         assertNull(
//                 SecurityContextHolder
//                         .getContext()
//                         .getAuthentication()
//         );

//         verify(filterChain).doFilter(
//                 request,
//                 response
//         );
//     }
// }