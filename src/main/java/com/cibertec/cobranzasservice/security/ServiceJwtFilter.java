package com.cibertec.cobranzasservice.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
@Component @Order(-100)
public class ServiceJwtFilter extends OncePerRequestFilter {
 @Value("${jwt.secret}") private String secret; private SecretKey key;
 @PostConstruct public void init(){byte[] raw=secret.getBytes(StandardCharsets.UTF_8);if(raw.length<32)throw new IllegalStateException("jwt.secret requiere al menos 32 bytes");key=Keys.hmacShaKeyFor(raw);}
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException {
  if("OPTIONS".equalsIgnoreCase(request.getMethod())){chain.doFilter(request,response);return;}
  String header=request.getHeader("Authorization");if(header==null||!header.startsWith("Bearer ")){response.sendError(401,"JWT requerido");return;}
  try {Claims claims=Jwts.parser().verifyWith(key).build().parseSignedClaims(header.substring(7)).getPayload();String role=claims.get("rol",String.class);
   if(claims.getSubject()==null||claims.getSubject().isBlank()||!("ADMINISTRADOR".equals(role)||"OPERADOR_CAJA".equals(role))){response.sendError(401,"JWT invalido");return;}
  }catch(RuntimeException ex){response.sendError(401,"JWT invalido o expirado");return;} chain.doFilter(request,response);
 }
}
