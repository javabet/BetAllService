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
@MapperScan(basePackages = "com.wisp.game.bet.api.dao.pay",sqlSessionTemplateRef ="paySqlSessionTemplate")
public class PaySourceConfig
{
    @Value("${spring.datasource.pay.url}")
    private String url;
    @Value("${spring.datasource.pay.username}")
    private String username;
    @Value("${spring.datasource.pay.password}")
    private String password;
    @Value("${spring.datasource.pay.driverClassName}")
    private String driverClassName;
    /**本数据源扫描的mapper路径*/
    static final String MAPPER_LOCATION = "classpath:mapper/pay/*.xml";


    /**创建数据源*/
    @Bean(name = "payDS")
    @Primary
    public DataSource getPaytDataSource() {
        DataSource build =  DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
        return build;
    }


    /**创建SessionFactory*/
    @Bean(name = "paySqlSessionFactory")
    @Primary
    public SqlSessionFactory paySqlSessionFactory(@Qualifier("payDS") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //设置mapper配置文件
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        return bean.getObject();
    }

    /**创建事务管理器*/
    @Bean("payTransactionManger")
    @Primary
    public DataSourceTransactionManager payTransactionManger(@Qualifier("payDS") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    /**创建SqlSessionTemplate*/
    @Bean(name = "paySqlSessionTemplate")
    @Primary
    public SqlSessionTemplate paySqlSessionTemplate(@Qualifier("paySqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
