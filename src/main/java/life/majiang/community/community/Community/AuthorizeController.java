package life.majiang.community.community.Community;

import life.majiang.community.community.Provider.GithubProvider;
import life.majiang.community.community.dto.AccessToeknDTO;
import life.majiang.community.community.dto.GithubUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {

    @Autowired //自动把spring容器中由@Component实例化的实例加载到当前使用的上下文
    private GithubProvider githubProvider;
    @Value("${github.client.id}")
    private String client_id;
    //@Value("$KEY") ：将配置文件中Key对应的value赋值给变量
    @Value("${github.client.secret}")
    private String client_secret;
    @Value("${github.redirect.uri}")
    private String redirect_uri;

    @GetMapping("/callback")
    //callback 是Github Application中 预存的redirect_uri的请求参数
    public String Callback(@RequestParam(name="code")String code,
                           @RequestParam(name="state")String state){

        AccessToeknDTO accessTokenDTO = new AccessToeknDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(redirect_uri);
        accessTokenDTO.setClient_id(client_id);
        accessTokenDTO.setClient_secret(client_secret);

        String token = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser user = githubProvider.getUser(token);
        System.out.println(user.getLogin());
        return "index";
        //@controller使得 return的String内容会调用相对应的Templates文件
    }
}
