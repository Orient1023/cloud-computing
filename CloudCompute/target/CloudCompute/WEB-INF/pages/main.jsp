<%--
  Created by IntelliJ IDEA.
  User: qi
  Date: 2017/1/3
  Time: 10:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page isELIgnored="false" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <link href="../../static/css/bootstrap.min.css" rel="stylesheet">
    <link href="../../static/css/mdb.min.css" rel="stylesheet">
    <link href="../../static/css/md_font-awesome.min.css" rel="stylesheet">
    <title>分布式文件系统</title>
    <style type="text/css">
        #filePanel
        {
            margin-top: 20px;
        }
    </style>
</head>
<div class="modal fade" id="createDirModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <!--Content-->
        <div class="modal-content">
            <!--Header-->
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">新建文件夹</h4>
            </div>
            <form id="mkdir" action="/mkDir" method="post">
            <!--Body-->
            <div class="modal-body">
                <div class="md-form">
                    <i class="fa fa-folder prefix"></i>
                    <input type="hidden" name="currentPath" value="${currentPath}">
                    <input type="text" id="folderName" class="form-control" name="dirName">
                    <label for="folderName">输入文件夹名字</label>
                </div>
            </div>
            <!--Footer-->
            <div class="modal-footer">
                <button  class="btn btn-primary btn-large" value="">确定</button>
                <button  class="btn btn-secondary btn-large" data-dismiss="modal" value="">取消</button>
            </div>
            </form>
        </div>
        <!--/.Content-->
    </div>
</div>

<div class="modal fade" id="renameModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <!--Content-->
        <div class="modal-content">
            <!--Header-->
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">重命名</h4>
            </div>
            <form id="renameFile" action="/renameFile" method="post">
                <!--Body-->
                <div class="modal-body">
                    <div class="md-form">
                        <i class="fa fa-folder prefix"></i>
                        <input type="hidden" name="fromPath"/>
                        <input type="text" id="newFileName" class="form-control" name="aimName"/>
                        <label for="folderName">重命名为</label>
                    </div>
                </div>
                <!--Footer-->
                <div class="modal-footer">
                    <button  class="btn btn-primary btn-large" value="">确定</button>
                    <button  class="btn btn-secondary btn-large" data-dismiss="modal" value="">取消</button>
                </div>
            </form>
        </div>
        <!--/.Content-->
    </div>
</div>


<c:set value="${hadoopFileStatusList}" var="hadoopFileStatusList"></c:set>
<c:set value="${currentPath}" var="path"></c:set>
<body>
<div class="container">
    <div class="row">
        <div class="panel" id="filePanel">
            <div class="card-header danger-color-dark white-text">
               <h4> 欢迎访问分布式文件系统</h4>
                <c:set var="pathList" value="${fn:split(path, '/')}"></c:set>
                <c:set var="current" value=""></c:set>
                <h5>当前路径:
                    <c:forEach var="splitPath" items="${pathList}">
                    <c:set var="current" value="${current}/${splitPath}"></c:set>
                    <a href="/back?aimPath=${current}">/${splitPath}</a>
                    </c:forEach>
            </div>
            <div class="pull-right">
                <span><button data-toggle="modal" class="btn btn-primary" data-target="#createDirModal">+<i class="fa fa-folder" style="color: red"></i></button></span>
            </div>
            <div id="fileInfo" class="card-block">
                <table class="table table-striped table-hover">
                <thead>
                <tr>
                    <th>名称</th>
                    <th>大小</th>
                    <th>文件所有者</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${hadoopFileStatusList}" varStatus="status">
                    <tr>
                        <td>
                        <c:choose>
                            <c:when test="${item.dir}">
                                <a href="/goToHadoopDir?dir=${item.fileName}"><i class="fa fa-folder"></i>${item.fileName}</a>
                            </c:when>
                            <c:otherwise>
                               <a href="/fileDownLoad?filename=${item.fileName}&filePath=${item.filePath}"><i class="fa fa-file">${item.fileName}</i></a>
                            </c:otherwise>
                        </c:choose>
                        </td>
                        <td><c:if test="${item.dir==false}">${item.formatSizeInfo}</c:if></td>
                        <td>${item.owner}</td>
                        <td>
                            <a class="blue-text"><i class="fa fa-user"></i></a>
                            <input type="hidden" value="${item.filePath}">
                            <a  class="teal-text renameLink"><i class="fa fa-pencil"></i></a>
                            <a  href="/deleteFile?filePath=${item.filePath}" class="red-text" data-toggle="#renameModal"><i class="fa fa-times"></i></a>
                            <c:if test="${item.dir==false}">
                            <a class="black-text" data-toggle="collapse" href="#collapseExample${status.count}"><i class="fa fa-arrows-v"></i> 查看详情</a>
                            <div class="collapse" id="collapseExample${status.count}" style="padding-left: 15px">
                                <hr>
                                <h5>文件存放详情</h5>
                                <h6>分为${item.blkCount}块</h6>
                                <h6>备份数为${item.replication}</h6>
                                <hr>
                                <h6>存放位置如下：</h6>
                                <h6>
                                    <c:forEach var="blockLocation" items="${item.blockLocations}" varStatus="blockStatus">

                                        <c:forEach var="host" items="${blockLocation.hosts}">
                                            <c:out value="${host}    " default=""></c:out>
                                        </c:forEach>
                                    </c:forEach>
                                </h6>
                            </div>
                            </c:if>
                        </td>
                    </tr>

                </c:forEach>
                </tbody>
                </table>
            </div>

            <form about="#" id="file-upload" action="/uploadFile"  method="post" enctype="multipart/form-data">
                <div class="file-field ">
                    <div>
                        <i class="fa fa-file prefix"></i>
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
        </div>
    </div>
</div>

<script src="../../static/js/jquery-2.2.3.min.js"></script>
<script src="../../static/js/mdb.min.js"></script>
<script src="../../static/js/bootstrap.min.js"></script>
<script src="../../static/js/tether.min.js"></script>
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

        $("#mkdir").submit(function () {
           var folderName = $("#folderName").val();
            if(folderName=="") {
                toastr["error"]("请输入文件夹名字");
                return false;
            }
            return true;
        });

        $("#renameFile").submit(function () {
            var newFileName = $("#newFileName").val();
            if(newFileName=="")
            {
                toastr["error"]("不能为空");
                return false;
            }
            return true;
        })



        $("#fileReset").click(function () {
            $("form :input").val("");
        });
        $(".renameLink").click(function () {
            var fromPath =  $(this).prev().val();
            $("#renameModal input[name='fromPath']").val(fromPath);
            $("#renameModal").modal();
        })
    });
</script>
</body>
</html>
