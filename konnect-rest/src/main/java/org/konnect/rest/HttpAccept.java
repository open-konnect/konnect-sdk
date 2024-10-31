package org.konnect.rest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum HttpAccept {
    JSON("application/json"),
    JSON_LD("application/ld+json"),
    XML("application/xml"),
    TEXT_XML("text/xml"),
    TEXT_HTML("text/html"),
    TEXT_PLAIN("text/plain"),
    TEXT_JS("text/javascript"),
    TEXT_CSS("text/css"),

    IMG_PNG("image/png"),
    IMG_JPG("image/jpeg"),
    IMG_GIF("image/gif"),
    IMG_WEBP("image/webp"),

    APP_JS("application/javascript"),

    MULTI_PART_FORM("multipart/form-data"),

    PDF("application/pdf"),
    BINARY("application/octet-stream"),

    ANY("*/*");

    @Getter private final String val;
}
