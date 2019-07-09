package life.majiang.community.community.mapper;

import life.majiang.community.community.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    // UserMapper 作用 是通过@Mapper实现映射 ，把User信息传入参数。目的:实现和数据库互动？
    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modified) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified})")
    void insert(User user);//当形参是一个bean的时候，会自动映射。当形参是个基本数据时，需要添加@Param(value = "token")才能映射


    @Select("select * from user where token=#{token}")
    User findByToekn(@Param("token") String token);
}


