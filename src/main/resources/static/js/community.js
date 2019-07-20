/*
link 或者button才能跳转js？
提交回复
 */
function post() {
    var questionId = $("#questionId").val();
    var content = $("#comment_content").val();
    //input( th:id="questionId" th:value="${quesitonDTO.id}")的值 其中 调用$("questionId").val() 会映射html页面的quesitonDto.id

    insertTarget(questionId,1,content);
}
    /*
    展开二级评论
     */
    function collapseComments(e){
        var collapse = e.getAttribute("data-collapse");
        var id =e.getAttribute("data-id");//获得本条html语句上的attribute
        var subCommentContainer = $("#comment-"+id);

        if(!collapse){

            if(subCommentContainer.children().length!=1){//判斷是否加载过，加载过则直接展开评论
                subCommentContainer.addClass("in");//展开二级评论
                e.setAttribute("data-collapse","in");//标记二级评论展开状态
                e.classList.add("comment-active");
            }else{

                $.getJSON( "/comment/"+id,function( data ) {//getJSON是经由jquery分装的js，联通后端和js，他通过访问url，返回JSONString的data

                    $.each( data.data.reverse(), function( index,commentDTO ) {//foreach循环，前半部分是合集，后半部分是从合集提取的属性
                      //function()内部参数都是键值对，是由后端传入到js文件

                       var img =  $("<img/>",{
                           "class":"media-object img-rounded",
                           "src":commentDTO.user.avatarUrl
                       });

                       var userName = $("<h5/>",{
                           "class":"profile-name",
                            html:commentDTO.user.name,
                       });

                       var content =  $("<div/>",{
                           html:commentDTO.content
                       });
                       var date = $("<div/>",{
                           "class":"menu "
                       }).append($("<div/>",{
                           "class":"pull-right ",
                           html:moment(commentDTO.gmtCreate).format('YYYY MM D')
                       }));

                        var mediaLeftElement = $("<div/>",{
                            "class":"media-left "
                        });

                        var mediaBodyElement = $("<div/>",{
                            "class":"media-body "
                        });

                        var mediaElement = $("<div/>",{
                            "class":"media "
                        });

                        var commentElement = $("<div/>",{//建立一个html语句对象
                            "class":"col-lg-12 col-md-12 col-sm-12 col-xs-12 comment-section "

                        });
                        img.append(content);
                        mediaLeftElement.append(img);
                        mediaBodyElement.append(userName).append(content).append(date);
                        mediaElement.append(mediaLeftElement).append(mediaBodyElement);
                        commentElement.append(mediaElement);
                        // html:comment.content

                        subCommentContainer.prepend(commentElement);//在父级前面追加一个子元素
                        debugger
                    });



                    subCommentContainer.addClass("in");//展开二级评论
                    e.setAttribute("data-collapse","in");//标记二级评论展开状态
                    e.classList.add("comment-active");

                });
            }
        }else{

            //折叠二级评论

            subCommentContainer.removeClass("in");
            e.removeAttribute("data-collapse");
            e.classList.remove("comment-active");

        }

    }

    function insertSubComment(e){
        var commentId =e.getAttribute("data-id");
        var content =$("#input-"+commentId).val();

        insertTarget(commentId,2,content);

    }


    //目的是通过js function 接受HTML信息，调用 CommentController ，最终插入comment
    //抽出 subComment
    function insertTarget(targetId,type,content) {
        if (!content) {
            alert("不能回复空内容.js");//alert和confirm都会跳出确认框，但confirm确认后会返回值
            //confirm("不能回复空内容.js");
            return;
        }
        $.ajax({//$.ajax是jquery分装，实现post访问跳转后端。
            type: "POST",
            url: "/comment",//url 要开头加/ 来定位起始位置
            contentType: 'application/json',
            data: JSON.stringify({//stringify将data由js———>字符串
                "parentId": targetId,
                "content": content,
                "type": type
            }),
            success: function (response) {
                console.log(response);
                if (response.code == 200) {

                    window.location.reload();
                } else {
                    if (response.code == 2003) {
                        var isAccepted = confirm(response.message);//跳出确定框，点击确定后 isAccepted为1
                        if (isAccepted) {
                            window.open("https://github.com/login/oauth/authorize?client_id=6be2a6faa72ecf6008fb&redirect_uri=http://localhost:8090/callback&scope=user&state=1");
                            window.localStorage.setItem("closable", "true");
                        }
                    } else
                        alert(response.message);
                }
            },
            dataType: "json"
        });

    }

