// Copyright (c) 2016 Petter Wintzell

package com.albroco.barebonesdigest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.albroco.barebonesdigest.DigestAuthentication.DEFAULT_CHALLENGE_COMPARATOR;
import static junit.framework.Assert.assertEquals;

public class DigestAuthenticationDefaultChallengeComparatorTest {
  private static final DigestChallenge SHA256_AUTH_CHALLENGE = createChallenge("SHA-256", "auth");
  private static final DigestChallenge SHA256_AUTH_INT_CHALLENGE =
      createChallenge("SHA-256", "auth-int");
  private static final DigestChallenge SHA256_AUTH_AUTH_INT_CHALLENGE =
      createChallenge("SHA-256", "auth,auth-int");
  private static final DigestChallenge SHA256_LEGACY_CHALLENGE = createChallenge("SHA-256", null);
  private static final DigestChallenge SHA256_UNSUPPORTED_QOP_CHALLENGE =
      createChallenge("SHA-256", "custom");
  private static final DigestChallenge MD5_AUTH_CHALLENGE = createChallenge("MD5", "auth");
  private static final DigestChallenge MD5_AUTH_INT_CHALLENGE = createChallenge("MD5", "auth-int");
  private static final DigestChallenge MD5_AUTH_AUTH_INT_CHALLENGE =
      createChallenge("MD5", "auth,auth-int");
  private static final DigestChallenge MD5_LEGACY_CHALLENGE = createChallenge("MD5", null);
  private static final DigestChallenge MD5_UNSUPPORTED_QOP_CHALLENGE =
      createChallenge("MD5", "custom");
  private static final DigestChallenge MD5_UNSUPPORTED_ALGORITHM_CHALLENGE =
      createChallenge("UNSUPPORTED", "auth,auth-int");

  @Test
  public void testSorting1() {
    List<DigestChallenge> sortedList = Arrays.asList(SHA256_AUTH_AUTH_INT_CHALLENGE,
        SHA256_AUTH_CHALLENGE,
        SHA256_LEGACY_CHALLENGE,
        SHA256_AUTH_INT_CHALLENGE,
        MD5_AUTH_AUTH_INT_CHALLENGE,
        MD5_AUTH_CHALLENGE,
        MD5_LEGACY_CHALLENGE,
        MD5_AUTH_INT_CHALLENGE,
        SHA256_UNSUPPORTED_QOP_CHALLENGE,
        MD5_UNSUPPORTED_QOP_CHALLENGE);

    List<DigestChallenge> list = new ArrayList<>(sortedList);
    Collections.reverse(list);
    Collections.sort(list, DEFAULT_CHALLENGE_COMPARATOR);

    assertEquals(sortedList, list);
  }

  @Test
  public void testSorting2() {
    List<DigestChallenge> sortedList = Arrays.asList(SHA256_AUTH_AUTH_INT_CHALLENGE,
        SHA256_AUTH_CHALLENGE,
        SHA256_LEGACY_CHALLENGE,
        SHA256_AUTH_INT_CHALLENGE,
        MD5_AUTH_AUTH_INT_CHALLENGE,
        MD5_AUTH_CHALLENGE,
        MD5_LEGACY_CHALLENGE,
        MD5_AUTH_INT_CHALLENGE,
        SHA256_UNSUPPORTED_QOP_CHALLENGE,
        MD5_UNSUPPORTED_ALGORITHM_CHALLENGE);

    List<DigestChallenge> list = new ArrayList<>(sortedList);
    Collections.reverse(list);
    Collections.sort(list, DEFAULT_CHALLENGE_COMPARATOR);

    assertEquals(sortedList, list);
  }

  private static DigestChallenge createChallenge(String algorithm, String qop) {
    String str = "Digest realm=\"\",algorithm=" + algorithm + ",nonce=\"whatever\"";
    if (qop != null) {
      str += ",qop=\"" + qop + "\"";
    }
    try {
      return DigestChallenge.parse(str);
    } catch (ChallengeParseException e) {
      e.printStackTrace();
      return null;
    }
  }
}
