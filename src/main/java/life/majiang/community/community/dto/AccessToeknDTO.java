package life.majiang.community.community.dto;
//dto内的bean实质是和github沟通
import lombok.Data;

/*
将 AccessToken信息分装到下面这个类中
 */
@Data
//@Data不奏效？

public class AccessToeknDTO {
        //编程习惯：把超过两个参数的信息体构建成一个对象
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;//不是必须认证的？
    private String state;


}
