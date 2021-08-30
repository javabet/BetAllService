package com.wisp.game.bet.recharge.beanConfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.wisp.core.persistence.BaseEntity;
import com.wisp.core.persistence.MyBatisDao;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.wisp.game.bet.recharge.dao",sqlSessionTemplateRef ="rechargeSqlSessionTemplate",annotationClass = MyBatisDao.class)
public class RechargeMySqlConfig
{
    @Value("${spring.datasource.recharge.url}")
    private String url;
    @Value("${spring.datasource.recharge.username}")
    private String username;
    @Value("${spring.datasource.recharge.password}")
    private String password;
    @Value("${spring.datasource.recharge.driverClassName}")
    private String driverClassName;
    @Value("${spring.datasource.recharge.typeAliasesPackage}")
    private String typeAliasesPackage;
    /**本数据源扫描的mapper路径*/
    @Value("${spring.datasource.recharge.mapperLocation}")
    private String MAPPER_LOCATION;

    @Value("${spring.datasource.recharge.basePackage}")
    private String basePackage;

    /**创建数据源*/
    @Bean(name = "rechargeDS")
    @Primary
    public DataSource getRechargetDataSource() {

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
    }


    //创建SessionFactory
    @Bean(name = "rechargeSqlSessionFactory")
    @Primary
    public SqlSessionFactory rechargeSqlSessionFactory(@Qualifier("rechargeDS") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //typeAliasesSuperType该配置请和 typeAliasesPackage 一起使用，如果配置了该属性，则仅仅会扫描路径下以该类作为父类的域对象
        bean.setTypeAliasesSuperType(BaseEntity.class);
        bean.setTypeAliasesPackage(typeAliasesPackage);
        //设置mapper配置文件
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        return bean.getObject();
    }

    //创建事务管理器
    @Bean("rechargeTransactionManger")
    @Primary
    public DataSourceTransactionManager rechargeTransactionManger(@Qualifier("rechargeDS") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    //创建SqlSessionTemplate
    @Bean(name = "rechargeSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate rechargeSqlSessionTemplate(@Qualifier("rechargeSqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
