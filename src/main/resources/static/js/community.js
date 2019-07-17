function post() {
    var questionId = $("#questionId").val();
    var content = $("#comment_content").val();
    //input( th:id="questionId" th:value="${quesitonDTO.id}")的值 其中 调用$("questionId").val() 会映射html页面的quesitonDto.id

    console.log(questionId);//等同system.out
    console.log(content);
    $.ajax({
        type: "POST",
        url: "/comment",//url 要开头加/ 来定位起始位置
        contentType: 'application/json',
        data: JSON.stringify({//stringify将data由js———>字符串
        "parentId":questionId,
        "content": content,
        "type":1
    }),
        success: function(response){
            console.log(response);
            if(response.code==200){
                $("#comment_section").hide();
            }else{
                if(response.code==2003){
                    var isAccepted=confirm(response.message);//跳出确定框，点击确定后 isAccepted为1
                    if(isAccepted){
                        window.open("https://github.com/login/oauth/authorize?client_id=6be2a6faa72ecf6008fb&redirect_uri=http://localhost:8090/callback&scope=user&state=1");
                        window.localStorage.setItem("closable","true");
                    }
                }
                else
                alert(response.message);
            }
        },
        dataType: "json"
    });
}