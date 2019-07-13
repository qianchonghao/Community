package life.majiang.community.community.mapper;

import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
//牢记questionMapper 返回的只是question 类型数据，其中questionDTO 由questionService返回！！！！
@Mapper
public interface QuestionMapper {


    @Insert("insert into question (title,description,gmt_create,gmt_modified,creator,tag) values (#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})" )
    void getQuestion(Question question);//输入question信息--->数据库

    @Select("select * from question limit #{size} offset #{offset}")
    List<Question> getList(@Param("offset") Integer offset, @Param("size")Integer size);
    //根据offset和size从数据库调取数据返回List<question>

    @Select("select count(1) from question")
    Integer count();
    //返回数据库数据总数


    @Select("select * from question where creator=#{id} limit #{size} offset #{offset}" )
    List<Question> getListById(@Param("id") Integer id,@Param("offset") Integer offset, @Param("size")Integer size);
    //根据userID offset size 为筛选条件，返回数据库中List<question>

    @Select("select count(1) from question where creator=#{id}")
    Integer MyCount(@Param("id") Integer id);

    //根据UserId 返回数据库中该用户的问题总数
    @Select("select * from question where id=#{id} ")
    Question getById(@Param("id") Integer id);

}
