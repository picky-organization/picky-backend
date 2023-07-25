package network.picky.web.config;

import network.picky.web.auth.provider.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public ProviderManager providerManager(JwtAuthenticationProvider provider){
        return new ProviderManager(provider);
    }



}
