package group.springsecurityproject.config;

import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    //뷰 리졸버가 디폹트로 어떤 뷰 파일을 탐색하는지 설정
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        //내가 새롭게 설정할  mustache 뷰 리졸버
        MustacheViewResolver resolver = new MustacheViewResolver();
        
        resolver.setCharset("UTF-8");
        resolver.setContentType("text/html; charset=UTF-8"); //던지는 파일이 html파일임
        resolver.setPrefix("classpath:/templates/"); // 그 html 파일의 경로
        resolver.setSuffix(".html"); //html 파일의 형식
        //이렇게 해주면 mustache 뷰 리졸버가 html을 mustache처럼 인식하게 됨
        
        //내가 만든 mustache 뷰 리졸버 등록
        registry.viewResolver(resolver);
    }
}

