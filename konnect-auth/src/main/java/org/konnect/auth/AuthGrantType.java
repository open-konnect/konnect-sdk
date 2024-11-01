package org.konnect.auth;

public enum AuthGrantType {

    AUTHORIZATION_CODE(3600),
    IMPLICIT, PASSWORD,
    CLIENT_CREDENTIALS(3600),
    DEVICE_CODE,
    REFRESH_TOKEN(24*3600), NONE;

    public int defaultTokenValidity;

    AuthGrantType(){
        this.defaultTokenValidity = 0;
    }

    AuthGrantType(int defaultTokenValidity) {
        this.defaultTokenValidity = defaultTokenValidity;
    }

}
