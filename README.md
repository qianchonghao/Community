

##资料
[Spring ](https://spring.io/guides )
[Spring ](https://github.com/qianchonghao/Community)
[Spring Web](https://spring.io/guides/gs/serving-web-content/)
[Github OAuth  ](https://developer.github.com/apps/building-oauth-apps/creating-an-oauth-app/)
[Es ](https://elasticsearch.cn/)
[BootStrap ](https://v3.bootcss.com)
[OKHttp](https://square.github.io/okhttp/)
[Spring](https://docs.spring.io/spring-boot/docs/2.0.0.RC1/reference/htmlsingle/#boot-documentation)
[Mybaitis]()

##工具
[Visual Paradigm](https://www.visual-paradigm.com/cn/)

##脚本
*** sql
CREATE CACHED TABLE PUBLIC.USER(
    ID INT DEFAULT () 
    ACCOUNT_ID VARCHAR(100),
    NAME VARCHAR(50),
    TOKEN CHAR(36),
    GMT_CREATE BIGINT,
    GMT_MODIFIED BIGINT
)

*** 
##注意点
1.HTML文件名 和controller文件名要对应
2.HTML文件 <head>中存储内容=css+theme+js
3.拖拽文件到<head>注意去除 地址前缀../static
4.通过github中api 有guides指导！！
5. okhttp中maven的注入
       <dependency>
              <groupId>com.squareup.okhttp3</groupId>
              <artifactId>okhttp</artifactId>
              <version>3.14.1</version>
          </dependency>
 test 