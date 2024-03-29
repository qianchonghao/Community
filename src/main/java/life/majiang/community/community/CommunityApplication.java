package life.majiang.community.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//用容器去管理bean
@MapperScan(basePackages = "life.majiang.community.community.mapper")//促使mapper 介入spring上下文
public class CommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }
}



