package life.majiang.community.community.advice;

import life.majiang.community.community.Exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice//throw 出来的异常跳转到此
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)//表明handle的Exception.class
    ModelAndView handle(HttpServletRequest request, Throwable e, Model model) {
        HttpStatus status = getStatus(request);

        if(e instanceof CustomizeException){
            model.addAttribute("message",e.getMessage());
        }else{
            model.addAttribute("message","服务器boom！！！");
        }
        return new ModelAndView("error");//return modelView 跳转相应的error.html
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
