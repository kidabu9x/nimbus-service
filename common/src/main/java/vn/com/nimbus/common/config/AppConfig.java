package vn.com.nimbus.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(basePackages = {"vn.com.nimbus.common.data.repository"},
        entityManagerFactoryRef = "appEntityManagerFactory",
        transactionManagerRef = "appTransactionManager"
)
@Slf4j
public class AppConfig {

    protected Properties hibProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        return properties;
    }

    @Bean(name = "master")
    public DataSource masterDataSource() throws IllegalStateException {
        HikariConfig hikariConfig = new HikariConfig();
        ConfigData.DBConnection dbConnection = ConfigLoader.getInstance().configData.getDbConnection();
        ConfigData.DBConnection.DBInfo dbInfo = dbConnection.getMaster();
        configHikariDatasource(hikariConfig, dbInfo);

        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "slave")
    public DataSource slaveDataSource() throws IllegalStateException {
        HikariConfig hikariConfig = new HikariConfig();
        ConfigData.DBConnection dbConnection = ConfigLoader.getInstance().configData.getDbConnection();
        ConfigData.DBConnection.DBInfo dbInfo = dbConnection.getSlave();
        configHikariDatasource(hikariConfig, dbInfo);

        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "dynamicDataSource")
    public DataSource dynamicDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource());
        targetDataSources.put("slave", slaveDataSource());

        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "slave" : "master";
            }
        };

        routingDataSource.setDefaultTargetDataSource(targetDataSources.get("master"));
        routingDataSource.setTargetDataSources(targetDataSources);
        return routingDataSource;
    }

    @Bean(name = "haDatasource")
    public DataSource haDatasource() {
        return new LazyConnectionDataSourceProxy(dynamicDataSource());
    }

    private void configHikariDatasource(HikariConfig hikariConfig, ConfigData.DBConnection.DBInfo dbInfo) {
        hikariConfig.setJdbcUrl(dbInfo.getUrl());
        hikariConfig.setUsername(dbInfo.getUsername());
        hikariConfig.setPassword(dbInfo.getPassword());
        hikariConfig.setDriverClassName(dbInfo.getDriverClassName());
        hikariConfig.setMaximumPoolSize(dbInfo.getMaximumPoolSize());
        hikariConfig.setMinimumIdle(dbInfo.getMinimumIdle());

        // add specific default config
        hikariConfig.setPoolName("nimbus-db-pool");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean appEntityManagerFactory() throws IllegalStateException, IOException {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        Boolean useHA = ConfigLoader.getInstance().configData.getDbConnection().getIsUseHa();
        if (useHA)
            entityManagerFactoryBean.setDataSource(haDatasource());
        else entityManagerFactoryBean.setDataSource(masterDataSource());
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setPackagesToScan("vn.com.nimbus.common.data.domain");
        entityManagerFactoryBean.setJpaProperties(hibProperties());
        entityManagerFactoryBean.setJpaVendorAdapter(getHibernateJpaVendorAdapter());

        return entityManagerFactoryBean;
    }

    @Bean
    public HibernateJpaDialect getHibernateJpaDialect() {
        return new HibernateJpaDialect();
    }

    @Bean("appTransactionManager")
    @Primary
    public JpaTransactionManager appTransactionManager() throws IllegalStateException, IOException {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(appEntityManagerFactory().getObject());
        transactionManager.setJpaDialect(getHibernateJpaDialect());
        return transactionManager;
    }

    @Bean
    public HibernateJpaVendorAdapter getHibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter hjva = new HibernateJpaVendorAdapter();
        hjva.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
//        hjva.setShowSql(true);
        return hjva;
    }
}
