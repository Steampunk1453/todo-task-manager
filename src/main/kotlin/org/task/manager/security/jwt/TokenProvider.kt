package org.task.manager.security.jwt

import io.github.jhipster.config.JHipsterProperties
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.Date
import javax.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

private const val AUTHORITIES_KEY = "auth"

@Component
class TokenProvider(private val jHipsterProperties: JHipsterProperties) {

    private val log = LoggerFactory.getLogger(javaClass)

    private var key: Key? = null

    private var tokenValidityInMilliseconds: Long = 0

    private var tokenValidityInMillisecondsForRememberMe: Long = 0

    @PostConstruct
    fun init() {
        val keyBytes: ByteArray
        val secret = jHipsterProperties.security.authentication.jwt.secret
        keyBytes = if (!StringUtils.isEmpty(secret)) {
            log.warn("Warning: the JWT key used is not Base64-encoded. " + "We recommend using the `jhipster.security.authentication.jwt.base64-secret` key for optimum security.")
            secret.toByteArray(StandardCharsets.UTF_8)
        } else {
            log.debug("Using a Base64-encoded JWT secret key")
            Decoders.BASE64.decode(jHipsterProperties.security.authentication.jwt.base64Secret)
        }
        this.key = Keys.hmacShaKeyFor(keyBytes)
        this.tokenValidityInMilliseconds = 1000 * jHipsterProperties.security.authentication.jwt.tokenValidityInSeconds
        this.tokenValidityInMillisecondsForRememberMe = 1000 * jHipsterProperties.security.authentication.jwt
            .tokenValidityInSecondsForRememberMe
    }

    fun createToken(authentication: Authentication, rememberMe: Boolean): String {
        val authorities = authentication.authorities.asSequence()
            .map { it.authority }
            .joinToString(separator = ",")

        val now = Date().time
        val validity = if (rememberMe) {
            Date(now + this.tokenValidityInMillisecondsForRememberMe)
        } else {
            Date(now + this.tokenValidityInMilliseconds)
        }

        return Jwts.builder()
            .setSubject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims = Jwts.parser()
            .setSigningKey(key)
            .parseClaimsJws(token)
            .body

        val authorities = claims[AUTHORITIES_KEY].toString().splitToSequence(",")
            .mapTo(mutableListOf()) { SimpleGrantedAuthority(it) }

        val principal = User(claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(authToken)
            return true
        } catch (e: JwtException) {
            log.info("Invalid JWT token.")
            log.trace("Invalid JWT token trace.", e)
        } catch (e: IllegalArgumentException) {
            log.info("Invalid JWT token.")
            log.trace("Invalid JWT token trace.", e)
        }

        return false
    }
}
