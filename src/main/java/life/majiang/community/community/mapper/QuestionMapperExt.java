package life.majiang.community.community.mapper;

import life.majiang.community.community.dto.QuestionQueryDTO;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionMapperExt {
    int incView(@Param("record") Question record);
    int incComment(@Param("record") Question record);

    List<Question> selectRelated(@Param("record")Question question);

    Integer countBySearch(@Param("query") QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearchWithRowbounds( QuestionQueryDTO query);
}