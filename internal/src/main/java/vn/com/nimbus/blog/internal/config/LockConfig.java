package vn.com.nimbus.blog.internal.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LockConfig {
    @Bean
    public LockProvider lockProvider(@Qualifier("master") DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }
}
