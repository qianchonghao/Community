package life.majiang.community.community.community;

import life.majiang.community.community.dto.CommentDTO;
import life.majiang.community.community.mapper.CommentMapper;
import life.majiang.community.community.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CommentController {
    @Autowired
    private CommentMapper commentMapper;
    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST )
    //需要client端口传输时候带有 @RequestBody标识
    public Object post(@RequestBody CommentDTO commentDTO){
        Comment comment = new Comment();
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(commentDTO.getContent());
        comment.setType(commentDTO.getType());
        comment.setCommentator(1);
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());

        commentMapper.insert(comment);
        return null;
    }
}
