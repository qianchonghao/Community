package life.majiang.community.community.community;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

@Controller()
//ErrorController 时，相当于异常抛出时访问/error 页面，这是系统内部已经自定义的BasicErrorController。
//我们写一个实现类，来覆盖BasicErrorController的作用，
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomizeErrorController implements ErrorController {
    @Override
    public String getErrorPath() {
        return "error";
    }

    @RequestMapping(
            produces = {"text/html"}
    )
    public ModelAndView errorHtml(HttpServletRequest request, Model model) {
        HttpStatus status = this.getStatus(request);//获得handler无法处理的异常code
        if (status.is4xxClientError()) {
            //4xx客户端引起
            model.addAttribute("message", "客户端请求出错");
        }
        if (status.is5xxServerError()) {
            model.addAttribute("message", "服务器出错");
        }
        return new ModelAndView("error");
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        //getStatus 是为了返回出错code ，常用在ErrorController中getStatus代码源自BasicErrorController
        //HttpStatus status = this.getStatus(request)
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception var4) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }
}
//异常整个跳转顺序 throw---->CustomizeExceptionHandler--->CustomizeExceptionController--->Error.html