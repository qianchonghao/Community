package life.majiang.community.community.interceptor;

import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Service

public class SessionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserMapper userMapper;

    //让srping接管，将其并入Spring上下文
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       //interceptor 对所有的程序做拦截。然后检查Cookie是否匹配，如若匹配，则将user添加到session中
        Cookie[] cookies = request.getCookies();
        if(cookies!=null&&cookies.length>0){
            for(Cookie cookie:cookies){

                if(cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    User user = userMapper.findByToekn(token);
                    if(user != null){
                        request.getSession().setAttribute("user",user);
                    }

                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
