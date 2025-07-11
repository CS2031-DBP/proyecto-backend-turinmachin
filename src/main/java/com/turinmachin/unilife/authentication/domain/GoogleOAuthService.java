package com.turinmachin.unilife.authentication.domain;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.javanet.NetHttpTransport;

@Service
public class GoogleOAuthService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleOAuthService(@Value("${oauth.google.client-id}") final String clientId) {
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                Utils.getDefaultJsonFactory())
                .setAudience(List.of(clientId))
                .build();
    }

    public GoogleIdToken verifyIdToken(final String idToken) throws IOException, GeneralSecurityException {
        return verifier.verify(idToken);
    }

}
