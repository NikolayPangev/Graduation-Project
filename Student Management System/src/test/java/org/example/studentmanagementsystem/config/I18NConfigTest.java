package org.example.studentmanagementsystem.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class I18NConfigTest {

    private final ApplicationContext applicationContext;

    public I18NConfigTest(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Test
    void testLocaleResolverBean() {
        LocaleResolver localeResolver = applicationContext.getBean(LocaleResolver.class);
        assertNotNull(localeResolver, "LocaleResolver bean should not be null");
        assertTrue(localeResolver instanceof org.springframework.web.servlet.i18n.CookieLocaleResolver, "LocaleResolver should be an instance of CookieLocaleResolver");
    }

    @Test
    void testLocaleChangeInterceptorBean() {
        LocaleChangeInterceptor localeChangeInterceptor = applicationContext.getBean(LocaleChangeInterceptor.class);
        assertNotNull(localeChangeInterceptor, "LocaleChangeInterceptor bean should not be null");
        assertTrue(localeChangeInterceptor.getParamName().equals("lang"), "LocaleChangeInterceptor paramName should be 'lang'");
    }

    @Test
    void testMessageSourceBean() {
        MessageSource messageSource = applicationContext.getBean(MessageSource.class);
        assertNotNull(messageSource, "MessageSource bean should not be null");
        assertTrue(messageSource instanceof org.springframework.context.support.ReloadableResourceBundleMessageSource, "MessageSource should be an instance of ReloadableResourceBundleMessageSource");
    }
}
