package life.majiang.community.community.service;

import life.majiang.community.community.Exception.CustomizeErrorCode;
import life.majiang.community.community.Exception.CustomizeException;
import life.majiang.community.community.dto.CommentCreateDTO;
import life.majiang.community.community.dto.CommentDTO;
import life.majiang.community.community.enums.CommentTypeEnum;
import life.majiang.community.community.mapper.CommentMapper;
import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.mapper.QuestionMapperExt;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionMapperExt questionMapperExt;
    @Autowired
    private UserMapper userMapper;

/*
commentService 对应commentDTO三个field进行检查，有问题则抛出异常
 */
@Transactional//保持下方函数 原子性
public void insert(Comment comment) {
    if (comment.getParentId() == null || comment.getParentId() == 0) {
        throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
    }
    if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
        throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
    }

    if (comment.getType() == CommentTypeEnum.QUESTION.getType()) {
        //回复问题
        Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
        if (question == null) throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        Question question1 = new Question();
        question1.setId(comment.getParentId());
        question1.setCommentCount(1);
        questionMapperExt.incComment(question1);
    } else {
        //回复评论
        Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
        if (dbComment == null) throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
    }
    commentMapper.insert(comment);

}

    public List<CommentDTO> getCommentListByQuesetionId(Long questionId) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().
                andParentIdEqualTo(questionId).
                andTypeEqualTo(CommentTypeEnum.QUESTION.getType());
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        //获取去重的评论人
        Set<Long> commentators =
                comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());

        List<Long>userId = new ArrayList<Long>();

        userId.addAll(commentators);

        //获取评论人 并转化为MAP
        UserExample example = new UserExample();
        example.createCriteria().andIdIn(userId);//根据容器内value来挑选数据库内数据

        List<User>users =userMapper.selectByExample(example);
        Map<Long,User> userMap = users.stream().collect(Collectors.toMap(user->user.getId(),user -> user));
        /* list等集合类型 都可以使用 stream()功能
        stream()不会修改数据源，将操作后的数据存储到另一个对象中
         Stream<String> s1 = userList.stream().map(user->user.getId());//map返回一个数据类型为Long内容为ID的stream
        Set<Long> commentator = userList.stream().map(user->user.getId()).collect(Collectors.toSet())
        末尾添加的collect 根据map返回的Stream<Long> 返回一个对应的Set
        Map<Long,User> userMap = userList.stream.collect(Collectors.toMap(user->userId,user->user))
        */
   /*     List<CommentDTO>commentDTOS = new ArrayList<>();
        for( Comment comment:comments){
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            commentDTOS.add(commentDTO);
        }*/
    //lambda转换 注意转换式内部最后return
        List<CommentDTO>commentDTOS = comments.stream().map(
                comment -> {
                    CommentDTO commentDTO = new CommentDTO();
                    BeanUtils.copyProperties(comment,commentDTO);
                    commentDTO.setUser(userMap.get(comment.getCommentator()));
                    return commentDTO;
                }
        ).collect(Collectors.toList());
        return commentDTOS;
    }
}
