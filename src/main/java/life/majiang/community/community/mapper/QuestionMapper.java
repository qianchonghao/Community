package life.majiang.community.community.mapper;

import life.majiang.community.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {


    @Insert("insert into question (title,description,gmt_create,gmt_modified,creator,tag) values (#{title},#{description},#{gmt_create},#{gmt_modified},#{creator},#{tag})" )
    void getQuestion(Question question);

    @Select("select * from question limit #{size} offset #{offset}")
    List<Question> getList(@Param("offset") Integer offset, @Param("size")Integer size);

    @Select("select count(1) from question")
    Integer count();

    @Select("select * from question where creator=#{id} limit #{size} offset #{offset}" )
    List<Question> getListById(@Param("id") Integer id,@Param("offset") Integer offset, @Param("size")Integer size);

    @Select("select count(1) from question where creator=#{id}")
    Integer MyCount(@Param("id") Integer id);
}
