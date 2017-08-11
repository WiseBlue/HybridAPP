<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2017/8/4
  Time: 下午5:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>HybridTestThree</title>
    <style type="text/css">
      body{
        background-color: #eff4f7;
        margin: 0px;
      }
      .menu{
        height:15%;
      }

      .menu ul{
        -webkit-padding-start:0px;
        margin: 0px;
      }
      .menu ul li{
        float: left;
        width: 20%;
        background-color: #FFFFFF;
        list-style: none;
      }
      .menu ul li a img{
        display: block;
        height: 75%;
        margin: 10px auto;
      }
      .menu ul li div{
        height: 25%;
        text-align: center;
      }
      .diliver{
        
      }
      .content{
        background-color: #ffffff;
        border-top:solid 1px;
        border-top-color: #d3dbd9;
        border-bottom: solid 1px;
        border-bottom-color:#d3dbd9 ;
      }
      .content_header{
        width: 100%;
        float: left;
        /*background-color: #942a25;*/
        height: 50px;
        line-height: 50px;
      }
      .content_header span{
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
        display: block;
        width: 70%;
        padding-left: 15px;
        vertical-align: middle;
        font-size: 20px;
        float: left;
      }
      .content_header div{
        padding-top: 10px;
        padding-right: 20px;
        width:20%;
        float: right;
        text-align:right;
        vertical-align: middle;
      }
      .content_content{
        padding-bottom: 10px;
        /*background-color: #1f6377;*/

      }
      .content_content p{
        padding-left:10px;
        font-size: 15px;
        overflow: hidden;
        text-overflow:ellipsis;
        display: -webkit-box;
        -webkit-line-clamp:5;
        -webkit-box-orient:vertical;
      }
      .footer{
        padding-top: 15px;
        height: 50px;
        text-align:center;
        font-size: 20px;
      }
    </style>
    <script type="text/javascript" src="resources/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="resources/js/jquery-1.9.1.js"></script>
    <script type="text/javascript">
      $(function() {
          $("div.content:first").clone().appendTo($("div.content_all"));
          $("div.content:first").clone().appendTo($("div.content_all"));

          /*scrollBottomTest=function () {
              $("div.content_all").scroll(function () {
                  var $this=$(this);
                  var v=$this[0];
                  var visibleHeight=$(this).height();//可见高度
                  var contentHeight=v.scrollHeight;//内容高度
                  var scrollTop=v.scrollTop;//滚动高度
                  if((contentHeight-visibleHeight-scrollTop)<=50){
                      alert("到底部啦");
                      $("div.footer").css("display","block");
                  }
              })

          }*/
          /*var txt=$("div.content_content p").html();
          var count=txt.split(/<br\s*\/?>/).length;
          alert(count);*/
      })
    </script>

  </head>
  <body>
  <div class="slider"><iframe src="beta11.html"></iframe></div>
  <div class="menu">
    <ul>
      <li><a href="#"><img src="resources/image/knowledge.png"></a><div>知识</div></li>
      <li><a href="#"><img src="resources/image/news.png"></a><div>资讯</div></li>
      <li><a href="#"><img src="resources/image/circle.png"></a><div>圈子</div></li>
      <li><a href="#"><img src="resources/image/homework.png"></a><div>作业</div></li>
      <li><a href="#"><img src="resources/image/friend.png"></a><div>好友</div></li>
    </ul>
  </div>
  <div class="diliver"></div>
  <div class="content_all">
    <div class="content">
      <div class="content_header">
        <span>足够看一整年的电影片单！！！都是经典！！！</span>
        <div><img src="resources/image/arrow-down.png"/> </div>
      </div>
      <div style="clear: both;"></div>
      <div class="content_content">
      <p>
        最费脑力的14部电影：《盗梦空间》、《记忆裂痕》、《生死停留》、《死亡幻觉》、《禁闭岛》、《穆赫兰道》、《蝴蝶效应》、
        《恐怖游轮》、《伤城》、《盗走达芬奇》、《88分钟》、《万能钥匙》、《决胜21点》、《沉默的羔羊》<br>
        感动无数人的电影：《恋空》、《婚纱》、《比悲伤更悲伤的故事》、《我脑中的橡皮擦》、
        《属于你的我的初恋》、《夏天协奏曲》、《天使之恋》、《分手信》、《近在咫尺的爱恋》<br>
        15部让你哭的昏天黑地的电影：《假如爱有天意》、《我脑海中的橡皮擦》、《情书》、《恋空》、《等待，只为与你相遇》、《我们的幸福时光》
        《请别相信她》、《触不到的恋人》、《菊花香》、《剪刀手爱德华》、《海上钢琴师》、《恋恋笔记本》、《泰坦尼克号》、《美丽心灵的永恒阳光》<br>
        十部适合一个人静静看的电影：《阿甘正传》、《肖申克的救赎》、《触不到的恋人》、《海上钢琴师》、《千与千寻》、《雏菊》、《花样年华》、
        《幸福来敲门》、《蓝莓之夜》、《放牛班的春天》<br>

      </p>
      </div>
    </div>
  </div>
  <div class="footer">加载更多内容...</div>
  </body>
</html>
