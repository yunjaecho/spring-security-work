package com.yunjae.springsecuritywork;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@SpringBootApplication
public class SpringSecurityWorkApplication {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityWorkApplication.class, args);
    }
}


@RestController
class BookByIsbn {
    private RestTemplate restTemplate;

    public BookByIsbn(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //  /boos/9791160500073
    @GetMapping("/boos/{isbn}")
    public String lookupBookByIsbn(@PathVariable("isbn") String isbn) {
        /*ResponseEntity<String> exchange = restTemplate
                .exchange("http://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn,
                        HttpMethod.GET, null, String.class);

        return exchange.getBody();*/
        return isbn;
    }
}

@Component("uuid")
class UuidService {
    public String buildUuid() {
        return UUID.randomUUID().toString();
    }
}


@Component
@Aspect
class LoggingAspect {
    private final Log log = LogFactory.getLog(getClass());

    @Around("execution( * com.yunjae.springsecuritywork..*.*(..) )")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        this.log.info("before " + joinPoint.toString());
        Object object = joinPoint.proceed();
        this.log.info("after " + joinPoint.toString());
        return object;
    }
}

@Component
class LoggingFilter implements Filter {

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Assert.isTrue( servletRequest instanceof HttpServletRequest, "this assumes you have an HTTP request");
//        HttpServletRequest httpServletRequest = HttpServletRequest.class.cast(servletRequest);
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String uri = httpServletRequest.getRequestURI();
        log.info("new request for " + uri + ".");

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}