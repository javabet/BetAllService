package com.wisp.game.bet.api.beanConfig;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.wisp.game.bet.api.dao.log",sqlSessionTemplateRef ="logSqlSessionTemplate")
public class LogSourceConfig
{
    @Value("${spring.datasource.log.url}")
    private String url;
    @Value("${spring.datasource.log.username}")
    private String username;
    @Value("${spring.datasource.log.password}")
    private String password;
    @Value("${spring.datasource.log.driverClassName}")
    private String driverClassName;
    /**本数据源扫描的mapper路径*/
    static final String MAPPER_LOCATION = "classpath:mapper/log/*.xml";


    /**创建数据源*/
    @Bean(name = "logDS")
    @Primary
    public DataSource getLogtDataSource() {
        DataSource build =  DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
        return build;
    }


    /**创建SessionFactory*/
    @Bean(name = "logSqlSessionFactory")
    @Primary
    public SqlSessionFactory logSqlSessionFactory(@Qualifier("logDS") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //设置mapper配置文件
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        return bean.getObject();
    }

    /**创建事务管理器*/
    @Bean("logTransactionManger")
    @Primary
    public DataSourceTransactionManager logTransactionManger(@Qualifier("logDS") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    /**创建SqlSessionTemplate*/
    @Bean(name = "logSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate logSqlSessionTemplate(@Qualifier("logSqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
