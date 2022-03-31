package com.example.demo.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource("classpath:/application.properties") //설정파일위치 지정 yml파일을 써서 설정을 하기도 한다
public class DatabaseConfig implements WebMvcConfigurer{

	@Value("${fileUploadPath}")
	String fileUploadPath;
	 //root context파일과 동일
	 @Bean
	 public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		//System.out.println("****************************************"); 
	   final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
	   sessionFactory.setDataSource(dataSource);
	   PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	   Resource configLocation = (Resource)new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml");
	
	   sessionFactory.setConfigLocation(configLocation);
	   //sessionFactory.setMapperLocations(resolver.getResources("classpath:mappers/*.xml"));
	   return sessionFactory.getObject();
    }
 
	@Bean
	public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}	
	
	//파일업로드와 관계된 설정파일들
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory(fileUploadPath, registry);
    }
     
    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();
         
        if (dirName.startsWith("../")) 
        	dirName = dirName.replace("../", "");
        System.out.println(dirName);
        System.out.println(uploadPath);
        
        registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/"+ uploadPath + "/");
    }
	    
}




