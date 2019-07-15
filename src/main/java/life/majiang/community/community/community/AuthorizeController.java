package life.majiang.community.community.community;

import life.majiang.community.community.Provider.GithubProvider;
import life.majiang.community.community.dto.AccessToeknDTO;
import life.majiang.community.community.dto.GithubUser;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired //自动把spring容器中由@Component实例化的实例加载到当前使用的上下文
    private GithubProvider githubProvider;

    @Autowired
    private UserService userService;

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
                           @RequestParam(name="state")String state,
                            HttpServletRequest request,
                            HttpServletResponse response){
        //Sping 自动把上下文中HttpServletRequest  传入参数request 供我们使用
        AccessToeknDTO accessTokenDTO = new AccessToeknDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(redirect_uri);

        accessTokenDTO.setClient_id(client_id);
        accessTokenDTO.setClient_secret(client_secret);

        String token = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(token);

        if(githubUser!=null){
            //登录成功 写cookie和session
            User user = new User();
            user.setAccountId(String.valueOf(githubUser.getId()));//String.valueOf(String data)将int转化为 String
            //accountID原本是github账户的id，setAccountId用于识别user是否存在数据库
            //gmtCreate ，在user是否存在的两种情况下有区别。应在userService中分开讨论
            //其余属性都是可能变化，所以先set。
            String newToken = UUID.randomUUID().toString();
            user.setToken(newToken);  //手动token
            user.setName(githubUser.getName());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            user.setGmtModified(System.currentTimeMillis());


            userService.createOrUpdate(user);
            request.getSession().setAttribute("user",githubUser);
            //设置cookie和session 同时要思考怎么在HTML文件中传递Session
            response.addCookie(new Cookie("token",newToken));
            return "redirect:/";
            //return redirect 会重新跳转index 不包含请求参数
        }else{
            //登录失败 重新登录
            return "redirect:/";
        }

        //@controller使得 return的String内容会调用相对应的Templates文件
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie=new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);//注意addCookie( Cookie) 参数为cookie形


        return "redirect:/";
    }
}
