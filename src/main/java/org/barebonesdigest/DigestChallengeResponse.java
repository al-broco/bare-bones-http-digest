package org.barebonesdigest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Describes an <code>Authorization</code> HTTP request header. Once the client has received a
 * HTTP Digest challenge from the server this header should be included in all subsequent requests
 * to authorize the client.
 *
 * @see <a href="https://tools.ietf.org/html/rfc2617#section-3.2.2">RFC 2617, "HTTP Digest Access
 * Authentication", Section 3.2.2, "The Authorization Request Header"</a>
 */
public class DigestChallengeResponse {
  /**
   * The name of the HTTP request header ({@value #HTTP_HEADER__AUTHORIZATION}).
   */
  public static final String HTTP_HEADER__AUTHORIZATION = "Authorization";

  private final MessageDigest md5;

  private String algorithm;
  private String username;
  private String password;
  private String clientNonce;
  private String nonce;
  private int nonceCount;
  private String quotedOpaque;
  private String uri;
  private String realm;
  private String requestMethod;

  public DigestChallengeResponse() {
    try {
      this.md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      // TODO find out if this can happen
      throw new RuntimeException(e);
    }

    this.nonceCount(1);
  }

  /**
   * Creates a digest challenge response, setting the ealm` and the values values of the `nonce`,
   * `opaque`, and `algorithm` directives based on a challenge.
   *
   * @param challenge the challenge
   * @return a response to the challenge.
   */
  public static DigestChallengeResponse responseTo(DigestChallenge challenge) {
    return new DigestChallengeResponse().challenge(challenge);
  }


  /**
   * Sets the `algorithm` directive, which must be the same as the `algorithm` directive of the
   * challenge. The only value currently supported is "MD5".
   *
   * @param algorithm the value of the algorithm directive
   * @return this object so that setting directives can be easily chained
   * @see <a href="https://tools.ietf.org/html/rfc2617#section-3.2.2">Section 3.2.2 of RFC 2617</a>
   */
  public DigestChallengeResponse algorithm(String algorithm) {
    if (!"MD5".equals(algorithm)) {
      throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
    }

    this.algorithm = algorithm;
    return this;
  }

  /**
   * Sets the `username` to use for authentication.
   *
   * @return this object so that setting directives can be easily chained
   * @see <a href="https://tools.ietf.org/html/rfc2617#section-3.2.2">Section 3.2.2 of RFC 2617</a>
   */
  public DigestChallengeResponse username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Sets the password to use for authentication.
   *
   * @param password the password
   * @return this object so that setting directives can be easily chained
   */
  public DigestChallengeResponse password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Sets the `cnonce` directive, which is a random string generated by the client that will be
   * included in the challenge response hash.
   * <p/>
   * There is normally no need to manually set the client nonce since it will have a default value
   * of a randomly generated string.
   *
   * @param clientNonce the unquoted value of the `cnonce`directive
   * @return this object so that setting directives can be easily chained
   * @see <a href="https://tools.ietf.org/html/rfc2617#section-3.2.2">Section 3.2.2 of RFC 2617</a>
   */
  public DigestChallengeResponse clientNonce(String clientNonce) {
    this.clientNonce = clientNonce;
    return this;
  }

  /**
   * Sets the `nonce` directive, which must be the same as the `nonce` directive of the
   * challenge.
   * <p/>
   * Setting the `nonce` directive resets the nonce count to one.
   *
   * @param nonce the unquoted value of the nonce directive
   * @return this object so that setting directives can be easily chained
   * @see <a href="https://tools.ietf.org/html/rfc2617#section-3.2.2">Section 3.2.2 of RFC 2617</a>
   */
  public DigestChallengeResponse nonce(String nonce) {
    this.nonce = nonce;
    resetNonceCount();
    return this;
  }

  /**
   * Sets the integer representation of the `nonce-count` directive, which indicates how many times
   * this a challenge response with this nonce has been used.
   * <p>
   * This is useful when using a challenge response from a previous challenge when sending a
   * request. For each time a challenge response is used, the nonce count should be increased by
   * one.
   *
   * @param nonceCount integer representation of the `nonce-count`directive
   * @return this object so that setting directives can be easily chained
   * @see #resetNonceCount()
   * @see #incrementNonceCount()
   * @see <a href="https://tools.ietf.org/html/rfc2617#section-3.2.2">Section 3.2.2 of RFC 2617</a>
   */
  public DigestChallengeResponse nonceCount(int nonceCount) {
    this.nonceCount = nonceCount;
    return this;
  }

  /**
   * Sets the `opaque` directive, which must be the same as the `opaque` directive of the
   * challenge.
   *
   * @param quotedOpaque the quoted value of the `opaque` directive, or {@code null} if no
   *                     `opaque` directive should be included in the challenge response
   * @return this object so that setting directives can be easily chained
   * @see <a href="https://tools.ietf.org/html/rfc2617#section-3.2.2">Section 3.2.2 of RFC 2617</a>
   */
  public DigestChallengeResponse quotedOpaque(String quotedOpaque) {
    this.quotedOpaque = quotedOpaque;
    return this;
  }

  public DigestChallengeResponse uri(String uri) {
    this.uri = uri;
    return this;
  }

  public DigestChallengeResponse realm(String realm) {
    this.realm = realm;
    return this;
  }

  public DigestChallengeResponse requestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
    return this;
  }

  public DigestChallengeResponse challenge(DigestChallenge challenge) {
    return nonce(challenge.getNonce()).quotedOpaque(challenge.getOpaqueQuoted())
        .realm(challenge.getRealm())
        .algorithm(challenge.getAlgorithm());
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getClientNonce() {
    if (clientNonce == null) {
      synchronized (this) {
        if (clientNonce == null) {
          clientNonce = generateRandomNonce();
        }
      }
    }

    return clientNonce;
  }

  public String getNonce() {
    return nonce;
  }

  public int getNonceCount() {
    return nonceCount;
  }

  public void resetNonceCount() {
    nonceCount(1);
  }

  public void incrementNonceCount() {
    nonceCount(nonceCount + 1);
  }

  public String getQuotedOpaque() {
    return quotedOpaque;
  }

  public String getUri() {
    return uri;
  }

  public String getRealm() {
    return realm;
  }

  public String getRequestMethod() {
    return requestMethod;
  }

  public String getHeaderValue() {
    // TODO: verify that all values are set

    String response = calculateResponse();

    StringBuilder result = new StringBuilder();
    result.append("Digest ");

    // Username is defined in Section 3.2.2 of RFC 2617
    // username         = "username" "=" username-value
    // username-value   = quoted-string
    result.append("username=");
    result.append(quoteString(username));
    result.append(",");

    // Realm is defined in RFC 2617, Section 1.2
    // realm       = "realm" "=" realm-value
    // realm-value = quoted-string
    // TODO: Unnecessary to quote and then unquote string value
    result.append("realm=");
    result.append(quoteString(realm));
    result.append(",");

    // nonce             = "nonce" "=" nonce-value
    // nonce-value       = quoted-string
    // TODO: Unnecessary to quote and then unquote string value
    result.append("nonce=");
    result.append(quoteString(nonce));
    result.append(",");

    // digest-uri       = "uri" "=" digest-uri-value
    // digest-uri-value = request-uri   ; As specified by HTTP/1.1
    result.append("uri=");
    result.append(quoteString(uri));
    result.append(",");

    // Response is defined in RFC 2617, Section 3.2.2 and 3.2.2.1
    // response         = "response" "=" request-digest
    result.append("response=");
    result.append(response);
    result.append(",");

    // Cnonce is defined in RFC 2617, Section 3.2.2
    // cnonce           = "cnonce" "=" cnonce-value
    // cnonce-value     = nonce-value
    // Must be present if qop is specified, must not if qop is unspecified
    // TODO: don't include if qop is unspecified
    result.append("cnonce=");
    result.append(quoteString(clientNonce));
    result.append(",");

    // Opaque and algorithm are explained in Section 3.2.2 of RFC 2617:
    // "The values of the opaque and algorithm fields must be those supplied
    // in the WWW-Authenticate response header for the entity being
    // requested."

    if (quotedOpaque != null) {
      result.append("opaque=");
      result.append(quotedOpaque);
      result.append(",");
    }

    if (algorithm != null) {
      result.append("algorithm=");
      result.append(algorithm);
      result.append(",");
    }

    // TODO Verify that server supports auth
    // TODO Also support auth-int
    result.append("qop=auth");
    result.append(",");

    // Nonce count is defined in RFC 2617, Section 3.2.2
    // nonce-count      = "nc" "=" nc-value
    // nc-value         = 8LHEX (lower case hex)
    // Must be present if qop is specified, must not if qop is unspecified
    result.append("nc=");
    result.append(String.format("%08x", nonceCount));

    return result.toString();
  }

  private String calculateResponse() {
    // TODO: Below calculation is for the case where qop is present, if not qop is calculated
    // differently
    String a1 = calculateA1();
    String a2 = calculateA2();

    String secret = calculateMd5(a1);
    String data = joinWithColon(nonce,
        String.format("%08x", nonceCount),
        clientNonce,
        "auth",
        calculateMd5(a2));

    return "\"" + calculateMd5(secret + ":" + data) + "\"";
  }

  private String calculateA1() {
    // TODO: Below calculation is for if algorithm is MD5 or unspecified
    // TODO: Support MD5-sess algorithm
    return joinWithColon(username, realm, password);
  }

  private String calculateA2() {
    // TODO: Below calculation if if qop is auth or unspecified
    // TODO: Support auth-int qop
    return joinWithColon(requestMethod, uri);
  }

  private String joinWithColon(String... parts) {
    StringBuilder result = new StringBuilder();

    for (String part : parts) {
      if (result.length() > 0) {
        result.append(":");
      }
      result.append(part);
    }

    return result.toString();
  }

  private String calculateMd5(String string) {
    md5.reset();
    // TODO find out which encoding to use
    md5.update(string.getBytes());
    return encodeHexString(md5.digest());
  }

  private static String encodeHexString(byte[] bytes) {
    StringBuilder result = new StringBuilder(bytes.length * 2);
    for (int i = 0; i < bytes.length; i++) {
      result.append(Integer.toHexString((bytes[i] & 0xf0) >> 4));
      result.append(Integer.toHexString((bytes[i] & 0x0f)));
    }
    return result.toString();
  }

  private String quoteString(String str) {
    // TODO: implement properly
    return "\"" + str + "\"";
  }

  private static String generateRandomNonce() {
    // TODO implement
    return "0a4f113b";
  }
}
