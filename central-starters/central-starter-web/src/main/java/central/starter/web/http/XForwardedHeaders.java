package central.starter.web.http;

/**
 * X-Forwarded-*
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public interface XForwardedHeaders {
    String HOST = "X-Forwarded-Host";
    String PORT = "X-Forwarded-Port";
    String SCHEMA = "X-Forwarded-Schema";
    String PATH = "X-Forwarded-Path";
    String TENANT = "X-Forwarded-Tenant";
}
