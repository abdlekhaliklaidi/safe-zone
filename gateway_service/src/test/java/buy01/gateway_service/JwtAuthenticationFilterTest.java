// package buy01.gateway_service;

// import buy01.gateway_service.dto.UserVerificationResponse;
// import buy01.gateway_service.service.UserBlacklistService;
// import buy01.gateway_service.service.UserServiceClient;
// import buy01.gateway_service.security.JwtService;
// import buy01.gateway_service.security.JwtAuthenticationFilter;
// import io.jsonwebtoken.Claims;


// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.security.Keys;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;

// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import org.springframework.cloud.gateway.filter.GatewayFilterChain;
// import org.springframework.core.io.buffer.DataBufferUtils;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
// import org.springframework.mock.web.server.MockServerWebExchange;
// import org.springframework.web.server.ServerWebExchange;

// import reactor.core.publisher.Mono;
// import reactor.test.StepVerifier;

// import java.nio.charset.StandardCharsets;
// import java.util.Date;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class JwtAuthenticationFilterTest {

//     @Mock
//     private JwtService jwtService;

//     @Mock
//     private UserBlacklistService userBlacklistService;

//     @Mock
//     private UserServiceClient userServiceClient;

//     @Mock
//     private GatewayFilterChain chain;

//     private JwtAuthenticationFilter filter;

//     @BeforeEach
//     void setUp() {
//         filter = new JwtAuthenticationFilter(
//                 jwtService,
//                 userBlacklistService,
//                 userServiceClient
//         );
//     }

//     // =========================================================
//     // ORDER
//     // =========================================================

//     @Test
//     void getOrder_shouldReturnMinusOne() {

//         assertEquals(-1, filter.getOrder());
//     }

//     // =========================================================
//     // OPTIONS
//     // =========================================================

//     @Test
//     void filter_shouldAllowOptionsRequest() {

//         MockServerWebExchange exchange =
//                 exchange(HttpMethod.OPTIONS, "/api/products");

//         when(chain.filter(exchange))
//                 .thenReturn(Mono.empty());

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         verify(chain).filter(exchange);

//         verifyNoInteractions(jwtService);
//         verifyNoInteractions(userBlacklistService);
//         verifyNoInteractions(userServiceClient);
//     }

//     // =========================================================
//     // PUBLIC LOGIN
//     // =========================================================

//     @Test
//     void filter_shouldAllowLoginWithoutToken() {

//         MockServerWebExchange exchange =
//                 exchange(HttpMethod.POST, "/api/auth/login");

//         when(chain.filter(exchange))
//                 .thenReturn(Mono.empty());

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         verify(chain).filter(exchange);

//         verifyNoInteractions(jwtService);
//     }

//     // =========================================================
//     // PUBLIC REGISTER
//     // =========================================================

//     @Test
//     void filter_shouldAllowRegisterWithoutToken() {

//         MockServerWebExchange exchange =
//                 exchange(HttpMethod.POST, "/api/auth/register");

//         when(chain.filter(exchange))
//                 .thenReturn(Mono.empty());

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         verify(chain).filter(exchange);
//         verifyNoInteractions(jwtService);
//     }

//     // =========================================================
//     // PUBLIC PRODUCTS GET
//     // =========================================================

//     @Test
//     void filter_shouldAllowGetProductsWithoutToken() {

//         MockServerWebExchange exchange =
//                 exchange(HttpMethod.GET, "/api/products");

//         when(chain.filter(exchange))
//                 .thenReturn(Mono.empty());

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         verify(chain).filter(exchange);
//         verifyNoInteractions(jwtService);
//     }

//     @Test
//     void filter_shouldAllowGetProductByIdWithoutToken() {

//         MockServerWebExchange exchange =
//                 exchange(HttpMethod.GET, "/api/products/123");

//         when(chain.filter(exchange))
//                 .thenReturn(Mono.empty());

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         verify(chain).filter(exchange);
//         verifyNoInteractions(jwtService);
//     }

//     // =========================================================
//     // PUBLIC UPLOADS
//     // =========================================================

//     @Test
//     void filter_shouldAllowGetUploadsWithoutToken() {

//         MockServerWebExchange exchange =
//                 exchange(HttpMethod.GET, "/api/uploads/image.jpg");

//         when(chain.filter(exchange))
//                 .thenReturn(Mono.empty());

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         verify(chain).filter(exchange);
//     }

//     // =========================================================
//     // PUBLIC MEDIA
//     // =========================================================

//     @Test
//     void filter_shouldAllowGetMediaWithoutToken() {

//         MockServerWebExchange exchange =
//                 exchange(HttpMethod.GET, "/api/media/image.jpg");

//         when(chain.filter(exchange))
//                 .thenReturn(Mono.empty());

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         verify(chain).filter(exchange);
//     }

//     // =========================================================
//     // PUBLIC AVATAR
//     // =========================================================

//     @Test
//     void filter_shouldAllowPublicAvatarPost() {

//         MockServerWebExchange exchange =
//                 exchange(
//                         HttpMethod.POST,
//                         "/api/media/avatars/public"
//                 );

//         when(chain.filter(exchange))
//                 .thenReturn(Mono.empty());

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         verify(chain).filter(exchange);
//     }

//     // =========================================================
//     // MISSING AUTHORIZATION
//     // =========================================================

//     @Test
//     void filter_shouldRejectMissingAuthorization() {

//         MockServerWebExchange exchange =
//                 exchange(HttpMethod.POST, "/api/products");

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         assertEquals(
//                 HttpStatus.UNAUTHORIZED,
//                 exchange.getResponse().getStatusCode()
//         );

//         verifyNoInteractions(jwtService);
//         verifyNoInteractions(chain);
//     }

//     // =========================================================
//     // INVALID AUTH HEADER
//     // =========================================================

//     @Test
//     void filter_shouldRejectInvalidAuthorizationHeader() {

//         MockServerWebExchange exchange =
//                 exchange(HttpMethod.POST, "/api/products");

//         exchange.getRequest()
//                 .mutate()
//                 .header("Authorization", "Basic abc")
//                 .build();

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         assertEquals(
//                 HttpStatus.UNAUTHORIZED,
//                 exchange.getResponse().getStatusCode()
//         );

//         verifyNoInteractions(jwtService);
//     }

//     // =========================================================
//     // INVALID JWT
//     // =========================================================

//     @Test
//     void filter_shouldRejectInvalidJwt() {

//         MockServerWebExchange exchange =
//                 exchange(HttpMethod.POST, "/api/products");

//         exchange.getRequest()
//                 .mutate()
//                 .header(
//                         "Authorization",
//                         "Bearer invalid-token"
//                 )
//                 .build();

//         when(jwtService.validateToken("invalid-token"))
//                 .thenReturn(false);

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         assertEquals(
//                 HttpStatus.UNAUTHORIZED,
//                 exchange.getResponse().getStatusCode()
//         );

//         verify(jwtService)
//                 .validateToken("invalid-token");

//         verify(jwtService, never())
//                 .extractClaims(any());

//         verifyNoInteractions(userBlacklistService);
//         verifyNoInteractions(userServiceClient);
//     }

//     // =========================================================
//     // BLACKLISTED USER
//     // =========================================================

//     @Test
//     void filter_shouldRejectBlacklistedUser() {

//         MockServerWebExchange exchange =
//                 authenticatedExchange(
//                         HttpMethod.POST,
//                         "/api/products",
//                         "valid-token"
//                 );

//         mockValidJwt();

//         when(userBlacklistService.isBlacklisted("user-123"))
//                 .thenReturn(Mono.just(true));

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         assertEquals(
//                 HttpStatus.UNAUTHORIZED,
//                 exchange.getResponse().getStatusCode()
//         );

//         verify(userBlacklistService)
//                 .isBlacklisted("user-123");

//         verifyNoInteractions(userServiceClient);
//         verifyNoInteractions(chain);
//     }

//     // =========================================================
//     // USER DOES NOT EXIST
//     // =========================================================

//     @Test
//     void filter_shouldRejectWhenUserDoesNotExist() {

//         MockServerWebExchange exchange =
//                 authenticatedExchange(
//                         HttpMethod.POST,
//                         "/api/products",
//                         "valid-token"
//                 );

//         mockValidJwt();

//         when(userBlacklistService.isBlacklisted("user-123"))
//                 .thenReturn(Mono.just(false));

//         when(userServiceClient.getUserVerification("user-123"))
//                 .thenReturn(
//                         Mono.just(
//                                 new UserVerificationResponse(
//                                         false,
//                                         null
//                                 )
//                         )
//                 );

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         assertEquals(
//                 HttpStatus.UNAUTHORIZED,
//                 exchange.getResponse().getStatusCode()
//         );

//         verify(userServiceClient)
//                 .getUserVerification("user-123");

//         verifyNoInteractions(chain);
//     }

//     // =========================================================
//     // VALID USER - DB ROLE
//     // =========================================================

//     @Test
//     void filter_shouldForwardRequestWithUserHeaders() {

//         MockServerWebExchange exchange =
//                 authenticatedExchange(
//                         HttpMethod.POST,
//                         "/api/products",
//                         "valid-token"
//                 );

//         mockValidJwt();

//         when(userBlacklistService.isBlacklisted("user-123"))
//                 .thenReturn(Mono.just(false));

//         when(userServiceClient.getUserVerification("user-123"))
//                 .thenReturn(
//                         Mono.just(
//                                 new UserVerificationResponse(
//                                         true,
//                                         "SELLER"
//                                 )
//                         )
//                 );

//         when(chain.filter(any(ServerWebExchange.class)))
//                 .thenReturn(Mono.empty());

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         verify(chain).filter(argThat(ex -> {

//             ServerHttpRequest request =
//                     ex.getRequest();

//             return "user-123".equals(
//                         request.getHeaders().getFirst("X-User-Id")
//                     )
//                     && "john".equals(
//                         request.getHeaders().getFirst("X-Username")
//                     )
//                     && "SELLER".equals(
//                         request.getHeaders().getFirst("X-Role")
//                     );
//         }));
//     }

//     // =========================================================
//     // ROLE FALLBACK TO JWT
//     // =========================================================

//     @Test
//     void filter_shouldUseJwtRoleWhenDatabaseRoleIsNull() {

//         MockServerWebExchange exchange =
//                 authenticatedExchange(
//                         HttpMethod.POST,
//                         "/api/products",
//                         "valid-token"
//                 );

//         mockValidJwt();

//         when(userBlacklistService.isBlacklisted("user-123"))
//                 .thenReturn(Mono.just(false));

//         when(userServiceClient.getUserVerification("user-123"))
//                 .thenReturn(
//                         Mono.just(
//                                 new UserVerificationResponse(
//                                         true,
//                                         null
//                                 )
//                         )
//                 );

//         when(chain.filter(any(ServerWebExchange.class)))
//                 .thenReturn(Mono.empty());

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         verify(chain).filter(argThat(ex ->
//                 "SELLER".equals(
//                         ex.getRequest()
//                                 .getHeaders()
//                                 .getFirst("X-Role")
//                 )
//         ));
//     }

//     // =========================================================
//     // BLACKLIST FALSE
//     // =========================================================

//     @Test
//     void filter_shouldContinueWhenUserIsNotBlacklisted() {

//         MockServerWebExchange exchange =
//                 authenticatedExchange(
//                         HttpMethod.PUT,
//                         "/api/products/1",
//                         "valid-token"
//                 );

//         mockValidJwt();

//         when(userBlacklistService.isBlacklisted("user-123"))
//                 .thenReturn(Mono.just(false));

//         when(userServiceClient.getUserVerification("user-123"))
//                 .thenReturn(
//                         Mono.just(
//                                 new UserVerificationResponse(
//                                         true,
//                                         "CLIENT"
//                                 )
//                         )
//                 );

//         when(chain.filter(any(ServerWebExchange.class)))
//                 .thenReturn(Mono.empty());

//         StepVerifier.create(
//                 filter.filter(exchange, chain)
//         )
//         .verifyComplete();

//         verify(chain).filter(any(ServerWebExchange.class));
//     }

//     // =========================================================
//     // HELPERS
//     // =========================================================

//     private MockServerWebExchange exchange(
//             HttpMethod method,
//             String path) {

//         MockServerHttpRequest request =
//                 MockServerHttpRequest
//                         .method(method, path)
//                         .build();

//         return MockServerWebExchange.from(request);
//     }

//     private MockServerWebExchange authenticatedExchange(
//             HttpMethod method,
//             String path,
//             String token) {

//         MockServerHttpRequest request =
//                 MockServerHttpRequest
//                         .method(method, path)
//                         .header(
//                                 "Authorization",
//                                 "Bearer " + token
//                         )
//                         .build();

//         return MockServerWebExchange.from(request);
//     }

//     private void mockValidJwt() {

//         Claims claims = mock(Claims.class);

//         when(jwtService.validateToken("valid-token"))
//                 .thenReturn(true);

//         when(jwtService.extractClaims("valid-token"))
//                 .thenReturn(claims);

//         when(claims.getSubject())
//                 .thenReturn("john");

//         when(claims.get("userId", String.class))
//                 .thenReturn("user-123");

//         when(claims.get("role", String.class))
//                 .thenReturn("SELLER");
//     }
// }