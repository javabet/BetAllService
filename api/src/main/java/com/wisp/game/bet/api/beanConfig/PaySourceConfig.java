package com.wisp.game.bet.api.beanConfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.wisp.core.persistence.BaseEntity;
import com.wisp.core.persistence.MyBatisDao;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
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
@MapperScan(basePackages = "com.wisp.game.bet.api.dao.pay",sqlSessionTemplateRef ="paySqlSessionTemplate",annotationClass = MyBatisDao.class)
//@MapperScan(basePackages = "${spring.datasource.pay.basePackage}",sqlSessionTemplateRef ="paySqlSessionTemplate",
//        annotationClass = MyBatisDao.class)
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
    @Value("${spring.datasource.pay.typeAliasesPackage}")
    private String typeAliasesPackage;
    /**本数据源扫描的mapper路径*/
    //static final String MAPPER_LOCATION = "classpath:mapper/pay/*.xml";
    @Value("${spring.datasource.pay.mapperLocation}")
    private String MAPPER_LOCATION;

    @Value("${spring.datasource.pay.basePackage}")
    private String basePackage;

    /**创建数据源*/
    @Bean(name = "payDS")
    @Primary
    public DataSource getPaytDataSource() {

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


    //创建SessionFactory
    @Bean(name = "paySqlSessionFactory")
    @Primary
    public SqlSessionFactory paySqlSessionFactory(@Qualifier("payDS") DataSource dataSource) throws Exception {
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
    @Bean("payTransactionManger")
    @Primary
    public DataSourceTransactionManager payTransactionManger(@Qualifier("payDS") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    //创建SqlSessionTemplate
    @Bean(name = "paySqlSessionTemplate")
    @Primary
    public SqlSessionTemplate paySqlSessionTemplate(@Qualifier("paySqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
    @Bean(name = "payMapperScannerConfiger")
    public MapperScannerConfigurer payMapperScannerConfiger(@Qualifier("paySqlSessionFactory") SqlSessionFactory sqlSessionFactory)
    {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("paySqlSessionFactory");
        mapperScannerConfigurer.setBasePackage(basePackage);//这个属性就是映射接口的包，这个包里面的所有的接口扫描到
        //自定义一个注解加大排除我们所需要的Mapper接口，（ps：首先我们扫描这个包可能有不需要映射的接口，那我们就可以通过注解（自定义的注解）的方式选出我们所需要的，没加注解的就不是这个类所需要的）
        mapperScannerConfigurer.setAnnotationClass(MyBatisDao.class);
        return null;
    }
    **/
}
