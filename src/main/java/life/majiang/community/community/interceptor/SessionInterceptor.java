package life.majiang.community.community.interceptor;

import life.majiang.community.community.Provider.GithubProvider;
import life.majiang.community.community.enums.NotificationStatusEnum;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import life.majiang.community.community.model.UserExample;
import life.majiang.community.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Service

public class SessionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationService notificationService;

    //让srping接管，将其并入Spring上下文
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //在頁面跳转的时候，如果浏览器的cookie中之前存储了token，并且该token对应database内user，则视为登录。

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {

                if (cookie.getName().equals("token")) {//cookies内存在 name为token的cookie，那么依据该cookie.value()寻找db内的user
                    String token = cookie.getValue();
                    UserExample example = new UserExample();
                    example.createCriteria().andTokenEqualTo(token);//example制定标准
                    List<User> user = userMapper.selectByExample(example);//依据example select
                    if (user != null && user.size() != 0) {
                        request.getSession().setAttribute("user", user.get(0));
                        Long unreadCount = notificationService.unreadCount(user.get(0).getId());
                        request.getSession().setAttribute("unreadCount", unreadCount);
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
