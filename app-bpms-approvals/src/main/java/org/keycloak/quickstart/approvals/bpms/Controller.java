/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.quickstart.approvals.bpms;

import org.keycloak.KeycloakSecurityContext;
import org.kie.api.task.model.Status;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.UserTaskServicesClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vaclav Muzikar <vmuzikar@redhat.com>
 */
public class Controller {
    private static final String KIE_URL = "http://localhost:8080/kie-server/services/rest/server";
    private static final String KIE_USER = "kieuser";
    private static final String KIE_PASSWORD = "BPMpassword1;";
    private static final String KIE_CONTAINER = "org.keycloak.quickstart:bpm:1.0";
    private static final String KIE_PROCCES_ID = "bpm-quickstart.HandleApprovalRequest";

    public void handleLogout(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {
        if (req.getParameter("logout") != null) {
            req.logout();
            res.sendRedirect(req.getContextPath());
        }
    }

    public List<TaskInstance> getTasks() {
        UserTaskServicesClient taskServicesClient = getUserTaskServices();
        List<TaskSummary> taskSummaries = taskServicesClient.findTasksAssignedAsPotentialOwner(KIE_USER, 0, 0);

        List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();
        for (TaskSummary task : taskSummaries) {
            if (!(task.getProcessId().equals(KIE_PROCCES_ID)
                    && (task.getStatus().equals(Status.Ready.toString()) || KIE_USER.equals(task.getActualOwner())))) {
                continue;
            }
            TaskInstance taskInstance = taskServicesClient.getTaskInstance(task.getContainerId(), task.getId(), true, true, true);
            taskInstances.add(taskInstance);
        }

        return taskInstances;
    }

    public void handleApproval(HttpServletRequest req, HttpServletResponse res) throws IOException {
        boolean approved = true;
        String taskId = req.getParameter("approve");

        if (taskId == null) {
            taskId = req.getParameter("reject");
            approved = false;
        }

        if (taskId == null) {
            return;
        }

        Long taskIdLong = Long.valueOf(taskId);
        String token = getSession(req).getTokenString();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approved", approved);
        params.put("token", token);

        UserTaskServicesClient taskServicesClient = getUserTaskServices();
        TaskInstance task = taskServicesClient.getTaskInstance(KIE_CONTAINER, taskIdLong);

        if (task.getStatus().equals(Status.Ready.toString())) {
            taskServicesClient.claimTask(KIE_CONTAINER, taskIdLong, KIE_USER);
            taskServicesClient.startTask(KIE_CONTAINER, taskIdLong, KIE_USER);
        }
        taskServicesClient.completeTask(KIE_CONTAINER, taskIdLong, KIE_USER, params);

        res.sendRedirect(req.getContextPath());
    }

    private KeycloakSecurityContext getSession(HttpServletRequest req) {
        return (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
    }

    private UserTaskServicesClient getUserTaskServices() {
        KieServicesConfiguration conf = KieServicesFactory.newRestConfiguration(KIE_URL, KIE_USER, KIE_PASSWORD);

        conf.setMarshallingFormat(MarshallingFormat.JSON);
        KieServicesClient kieServicesClient = KieServicesFactory.newKieServicesClient(conf);

        return kieServicesClient.getServicesClient(UserTaskServicesClient.class);
    }
}
