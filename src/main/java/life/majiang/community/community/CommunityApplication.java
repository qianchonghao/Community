package life.majiang.community.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
//用容器去管理bean

public class CommunityApplication {

    public static void main(String[] args){
        SpringApplication.run(CommunityApplication.class,args);
    }
}



