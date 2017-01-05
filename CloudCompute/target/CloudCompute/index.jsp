<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ page language="java" contentType="text/html; charset=UTF-8"
             pageEncoding="UTF-8"%>
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta http-equiv="x-ua-compatible" content="ie=edge">
<link href="static/css/bootstrap.min.css" rel="stylesheet">
<link href="static/css/mdb.min.css" rel="stylesheet">
<link href="static/css/font-awesome.min.css" rel="stylesheet">
    <style>
        #file-upload
        {
            margin-top: 20px;
        }
    </style>
</head>
<body>
<c:redirect url="/goToHadoopDir"></c:redirect>
<div class="container">
    <form about="#" id="file-upload" action="/uploadFile"  method="post" enctype="multipart/form-data">
        <div class="file-field ">
            <div>
                <i class="icon-file prefix"></i>
                <input id="file_upload" type="file" name="myfile">
            </div>
            <div class="file-path-wrapper">
                <input class="file-path validate" type="text" placeholder="上传你的文件">
            </div>
        </div>
        <div>
            <button id="fileSubmit" type="button" class="btn btn-primary waves-light waves-effect btn-rounded">上传</button>
            <button id="fileReset" type="button" class="btn btn-primary waves-light waves-effect btn-rounded">清空</button>
        </div>
    </form>

    <a href="/fileDownLoad?filename=h.js.下载">下载文件</a>
</div>



<script src="static/js/jquery-2.2.3.min.js"></script>
<script src="static/js/mdb.min.js"></script>
<script src="static/js/bootstrap.min.js"></script>
<script src="static/js/tether.min.js"></script>
<script>
    $(function () {
        $("#fileSubmit").click(function () {
            if($("form :input[type=file]").val()=="")
            {
                toastr["error"]("请选择文件");
                return;
            }
            $("form").submit();
        });
        $("#fileReset").click(function () {
            $("form :input").val("");
        })
    });
</script>
</body>
</html>
