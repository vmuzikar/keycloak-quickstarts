<%--
  ~ Copyright 2018 Red Hat, Inc. and/or its affiliates
  ~ and other contributors as indicated by the @author tags.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="controller" class="org.keycloak.quickstart.approvals.bpms.Controller" scope="request"/>
<% controller.handleLogout(request,response); %>
<% controller.handleApproval(request,response); %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link rel="stylesheet" type="text/css" href="styles.css"/>

    <title>BPMS Approval Tasks</title>
</head>
<body>
<div class="wrapper">
    <a class="button" href="?logout">Logout</a>
    <div class="content">
        <c:set var = "tasks" value = "${controller.tasks}"/>
        <c:choose>
            <c:when test = "${tasks.size() > 0}">
                <table>
                    <tr>
                        <th>Requested action</th>
                        <th>Detailed description</th>
                    </tr>
                    <c:forEach items="${controller.tasks}" var="task">
                        <tr>
                            <td>Realm: <i>${task.inputData.get("realm")}</i> / ${task.inputData.get("name")}</td>
                            <td><pre>${task.inputData.get("desc")}</pre></td>
                            <td>
                                <a class="button approve" href="?approve=${task.id}">Approve</a>
                                <a class="button reject" href="?reject=${task.id}">Reject</a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:when>
            <c:otherwise>
                <strong>No available approval tasks found</strong>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
