package alpine.auth;

import alpine.Config;
import alpine.cache.CacheManager;
import alpine.logging.Logger;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @since 1.8.0
 */
public class OidcConfigurationResolver {

    private static final OidcConfigurationResolver INSTANCE = new OidcConfigurationResolver(
            Config.getInstance().getPropertyAsBoolean(Config.AlpineKey.OIDC_ENABLED),
            Config.getInstance().getProperty(Config.AlpineKey.OIDC_ISSUER)
    );
    private static final Logger LOGGER = Logger.getLogger(OidcConfigurationResolver.class);
    static final String CONFIGURATION_CACHE_KEY = "OIDC_CONFIGURATION";

    private final boolean oidcEnabled;
    private final String issuer;

    OidcConfigurationResolver(final boolean oidcEnabled, final String issuer) {
        this.oidcEnabled = oidcEnabled;
        this.issuer = issuer;
    }

    public static OidcConfigurationResolver getInstance() {
        return INSTANCE;
    }

    /**
     * Resolve the {@link OidcConfiguration} either from a remote authorization server or from cache.
     *
     * @return The resolved {@link OidcConfiguration} or {@code null}, when resolving was not possible
     */
    @Nullable
    public OidcConfiguration resolve() {
        if (!oidcEnabled) {
            LOGGER.debug("Will not resolve OIDC configuration: OIDC is disabled");
            return null;
        }

        if (issuer == null) {
            LOGGER.error("Cannot resolve OIDC configuration: No issuer provided");
            return null;
        }

        OidcConfiguration configuration = CacheManager.getInstance().get(OidcConfiguration.class, CONFIGURATION_CACHE_KEY);
        if (configuration != null) {
            LOGGER.debug("OIDC configuration loaded from cache");
            return configuration;
        }

        LOGGER.debug("Fetching OIDC configuration from issuer " + issuer);
        final OIDCProviderMetadata providerMetadata;
        try {
            providerMetadata = OIDCProviderMetadata.resolve(new Issuer(issuer));
        } catch (IOException | GeneralException e) {
            LOGGER.error("Failed to fetch OIDC configuration from issuer " + issuer, e);
            return null;
        }

        configuration = new OidcConfiguration();
        configuration.setIssuer(providerMetadata.getIssuer().getValue());
        configuration.setJwksUri(providerMetadata.getJWKSetURI());
        configuration.setUserInfoEndpointUri(providerMetadata.getUserInfoEndpointURI());

        LOGGER.debug("Storing OIDC configuration in cache: " + configuration);
        CacheManager.getInstance().put(CONFIGURATION_CACHE_KEY, configuration);

        return configuration;
    }

}
