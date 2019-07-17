package life.majiang.community.community.community;

import life.majiang.community.community.Exception.CustomizeErrorCode;
import life.majiang.community.community.dto.CommentCreateDTO;
import life.majiang.community.community.dto.ResultDTO;
import life.majiang.community.community.model.Comment;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;
    @ResponseBody//返回值是application/json形式
    //@ResponseBody作用：将函数返回的Object--->序列化成JSON格式String
    @PostMapping (value = "/comment")//post是如何输入的？

    //需要client端口传输时候带有 @RequestBody标识
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){
        // @RequestBody作用：将commentDTO由JSON格式的string --->反序列化生成的bean

        /*注意传递@RequestBody时候，reset插件中必须设置head 的ContentType = application/JSON
        这样才能和该函数接口对应*/
        /* JSON的书写格式
            {
            "parentId":293,\
            "content":"这是一个内容",
            "type":1
            }
         */
        User user= (User) request.getSession().getAttribute("user");
        if(user==null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setCommentator(user.getId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setLikeCount((long)0);
        commentService.insert(comment);

        return ResultDTO.okOf();//@response 将一个bean————>jsonStr 传递给response，所以community.js里面的success输出response的东西
    }
}
