package com.wisp.game.bet.api.beanConfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.wisp.core.persistence.BaseEntity;
import com.wisp.core.persistence.MyBatisDao;
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
@MapperScan(basePackages = "com.wisp.game.bet.api.dao.log",sqlSessionTemplateRef ="logSqlSessionTemplate",annotationClass = MyBatisDao.class)
//@MapperScan(basePackages = "${spring.datasource.log.mapperLocation}",sqlSessionTemplateRef ="logSqlSessionTemplate",annotationClass = MyBatisDao.class)
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
    @Value("${spring.datasource.log.typeAliasesPackage}")
    private String typeAliasesPackage;
    /**本数据源扫描的mapper路径*/
    //static final String MAPPER_LOCATION = "classpath:mapper/log/*.xml";
    @Value("${spring.datasource.log.mapperLocation}")
    private String MAPPER_LOCATION;

    /**创建数据源*/
    @Bean(name = "logDS")
    @Primary
    public DataSource getLogtDataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setInitialSize(3);
        ds.setMinIdle(3);
        ds.setMaxActive(100);
        ds.setMaxWait(60000);
        ds.setTimeBetweenEvictionRunsMillis(60000);
        ds.setMinEvictableIdleTimeMillis(300000);
        ds.setValidationQuery("select 'x' from dual");
        ds.setTestWhileIdle(true);
        ds.setTestOnBorrow(true);
        ds.setTestOnReturn(true);
        //ds.setFilters("stat");

        return ds;

//        DataSource build =  DataSourceBuilder.create()
//                .driverClassName(driverClassName)
//                .url(url)
//                .username(username)
//                .password(password)
//                .build();
//        return build;
    }


    /**创建SessionFactory*/
    @Bean(name = "logSqlSessionFactory")
    @Primary
    public SqlSessionFactory logSqlSessionFactory(@Qualifier("logDS") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //typeAliasesSuperType该配置请和 typeAliasesPackage 一起使用，如果配置了该属性，则仅仅会扫描路径下以该类作为父类的域对象
        bean.setTypeAliasesSuperType(BaseEntity.class);
        bean.setTypeAliasesPackage(typeAliasesPackage);
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
