<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
        
<!DOCTYPE HTML>
<html lang="en-US">
<head>
  <title>Umeng服务登录</title>
  <link rel="stylesheet" type="text/css" href="static/base.css" />
  <link rel="stylesheet" type="text/css" href="static/cas.css" />
  <link rel="icon" type="image/png" href="static/favicon.png" />
</head>
<script type="text/javascript">var NREUMQ=NREUMQ||[];NREUMQ.push(["mark","firstbyte",new Date().getTime()]);</script><body onload="if (document.getElementById('userId')) document.getElementById('userId').focus()">
  <div class="panel">
  <div style="min-height:100%;">
    <div style="min-height:50px"></div>
    <div class="contentpanel">
      <div class="formpanel">
        

<form class="login" method="post" action="login" id="login-form">
  
  <p><span class="input-bg"><input type="text" name="username" id="userId" placeholder="用户名"/></span></p>
  <p><span class="input-bg"><input type="password" name="password" id="passWord" placeholder="密码"/></span></p>
  <p class="btnpanel">
    <input type="hidden" id="lt" name="lt" value="LT-1375444950rKj-Iwj4k5jGfZTXEi9" />
    <input type="hidden" id="service" name="service" value="http:&#x2F;&#x2F;www.umeng.com&#x2F;apps" />
    <input type="submit" value="登&nbsp;录" class="submit" id="login-submit" accesskey="l"/>
    <span>
      <a href="http://www.umeng.com/users/password/new" class="forget">忘记密码?</a>
      <a href="http://www.umeng.com/users/sign_up" class="reg">注册</a>
    </span>
  </p>
</form>
      </div>
    </div>
  </div>
  <div class="footer">
    <p>
      <span>UMENG Copyright©2013 All Rights Reserved</span>
      <a href="http://www.umeng.com/aboutus_about" target="_blank">关于我们</a>|<a href="http://weibo.com/umengcom" target="_blank">微博</a>|<a href="http://blog.umeng.com/" target="_blank">Blog</a>|<a href="http://www.umeng.com/termsofservice" target="_blank">服务条款</a>|<a href="http://www.umeng.com/privacypolicy" target="_blank">隐私政策</a>
      <span>京公网安备110000000013号京ICP证6255884号</span>
    </p>
  </div>
</div>
</body lang="">
<script type="text/javascript" src="/sso/themes/simple/javascript/jquery.min.js"></script>
<script type="text/javascript" src="/sso/themes/simple/javascript/jquery.validate.1.8.0.js"></script>
<script type="text/javascript">
$().ready(function(){
  $('.input-bg input').focus(function(){
    $(this).parent().addClass('input-bg-active');
  })
  $('.input-bg input').blur(function(){
    $(this).parent().removeClass('input-bg-active');
  })
  $('.input-bg input').keyup(function(){
    if($(this).val().length > 1){
      $('.messagebox').hide("fast");
    }
  })
  $('.submit').click(function(){
    $('.messagebox').hide();
  })
  $('.login').validate({
    rules:{
      "username":{
        required:true
      },
      "password":{
        required:true
      }
    },
    errorPlacement: function(error,element) {
      $(element).after(error);
      $(error).prepend('<b class="corner"></b>');
    },
    wrapper:'h4',
    errorContainer:'div.error',
    messages:{
      "username":{
        required:"邮箱不能为空!"
      },
      "password":{
        required:"密码不能为空!"
      }
    }
  });
})
</script>
<script type="text/javascript">if (!NREUMQ.f) { NREUMQ.f=function() {
NREUMQ.push(["load",new Date().getTime()]);
var e=document.createElement("script");
e.type="text/javascript";
e.src=(("http:"===document.location.protocol)?"http:":"https:") + "//" +
  "js-agent.newrelic.com/nr-100.js";
document.body.appendChild(e);
if(NREUMQ.a)NREUMQ.a();
};
NREUMQ.a=window.onload;window.onload=NREUMQ.f;
};
NREUMQ.push(["nrfj","beacon-3.newrelic.com","ecec91bd47","2249672","clZWTBYMW18BRk43UF9YTEoFTHRyN2cEFk9USwICNwZFRQFGTiN8ZRlLSwtMW1wDXQ8=",0,8,new Date().getTime(),"","","","",""]);</script></html>
