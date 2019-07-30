package life.majiang.community.community.community;

import com.alibaba.fastjson.JSON;
import life.majiang.community.community.Exception.CustomizeErrorCode;
import life.majiang.community.community.Exception.CustomizeException;
import life.majiang.community.community.dto.CommentCreateDTO;
import life.majiang.community.community.dto.CommentDTO;
import life.majiang.community.community.dto.ResultDTO;
import life.majiang.community.community.enums.CommentTypeEnum;
import life.majiang.community.community.model.Comment;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

//    @ResponseBody//返回值是application/json形式
//    //@ResponseBody作用：将函数返回的Object--->序列化成JSON格式String
//    @PostMapping(value = "/comment")//post是如何输入的？
//
//
//    //需要client端口传输时候带有 @RequestBody标识
//    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
//                       HttpServletRequest request) {
//        // @RequestBody作用：将commentDTO由JSON格式的string --->反序列化生成的bean
//
//        /*注意传递@RequestBody时候，reset插件中必须设置head 的ContentType = application/JSON
//        这样才能和该函数接口对应*/
//        /* JSON的书写格式
//            {
//            "parentId":293,
//            "content":"这是一个内容",
//            "type":1
//            }
//         */
//        User user = (User) request.getSession().getAttribute("user");
//        if (user == null) {
//            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
//        }
//
//        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
//            //commons.lang 提供的StringUtils 可以简化代码
//            throw new CustomizeException(CustomizeErrorCode.CONTENT_IS_EMPTY);
//        }
//
//        Comment comment = new Comment();
//        comment.setParentId(commentCreateDTO.getParentId());
//        comment.setContent(commentCreateDTO.getContent());
//        comment.setType(commentCreateDTO.getType());
//        comment.setCommentator(user.getId());
//        comment.setGmtCreate(System.currentTimeMillis());
//        comment.setGmtModified(comment.getGmtCreate());
//        comment.setLikeCount((long) 0);
//        comment.setCommentCount(0);
//        commentService.insert(comment, user);
//
//        return ResultDTO.okOf(comment);//@response 将一个bean————>jsonStr 传递给response，所以community.js里面的success输出response的东西
//    }

@ResponseBody
@PostMapping("/comment")
public ResultDTO<Comment> insertComment(@RequestBody CommentCreateDTO commentCreateDTO ,
                                        HttpServletRequest request ){
    User user = (User) request.getSession().getAttribute("user");
    if(user==null) throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);

    Comment comment = new Comment();
    comment.setCommentCount(0);
    comment.setLikeCount((long) 0);
    comment.setGmtCreate(System.currentTimeMillis());
    comment.setGmtModified(comment.getGmtCreate());
    comment.setType(commentCreateDTO.getType());
    comment.setContent(commentCreateDTO.getContent());
    comment.setParentId(commentCreateDTO.getParentId());
    comment.setCommentator(user.getId());
    commentService.insert(comment,user);
    //insertComment 需要分类讨论,原因：不同类别的插入，会提高相应parent的commentCount，notification
    return ResultDTO.okOf(comment);
}
    //    comments目的：被访问时，request传入parentId返回包含相应commentsList的resultDTO
    @ResponseBody
    @GetMapping("/comment/{parentId}")
    public ResultDTO<List<CommentDTO>> comment(
            @PathVariable("parentId")Long parentId
    ){
        //二级评论展开是有jquery和后端commentController 互动实现的，
        // 通过访问url"/comment/{id}"实现调用CommentController返回JSONString ，而后JQuery利用返回的JSONString搭建html语句并且加载到前端。

        List<CommentDTO> commentDTOS = commentService.getCommentListById(parentId,CommentTypeEnum.COMMMENT.getType());

        return ResultDTO.okOf(commentDTOS);
        //comments 这个函数 是要根据Comment.parentID 返回List<CommentDTO> 同时又要返回获取成功的信息。所以在resultDTO中添加泛型
        //comments目的：返回属于某评论的二级评论List
    }



}
