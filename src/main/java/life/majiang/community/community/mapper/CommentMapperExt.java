package life.majiang.community.community.mapper;

import life.majiang.community.community.model.Comment;
import life.majiang.community.community.model.CommentExample;
import life.majiang.community.community.model.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentMapperExt {

    int incCommentCount(@Param("record") Comment record);
}