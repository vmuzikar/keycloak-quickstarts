# BPMS Approvals for Keycloak
This is a helper Java EE app that handles BPMS user tasks for Keycloak Approvals System.

## Prerequisites
* Running Keycloak server at `localhost:8081`.
  * In case Keycloak is running somewhere else, edit `src/webapp/WEB-INF/keycloak.json`.
* Running BPMS with Intelligent Process Server and the following parameters:
  * Server address: `http://localhost:8080/kie-server/services/rest/server`
  * Username: `kieuser`
  * Password: `BPMpassword1;`
  * Container: `org.keycloak.quickstart:bpm:1.0`
  * Process ID: `bpm-quickstart.HandleApprovalRequest`
  * Those can be changed in `Controller.java`

## How to build and run
1. Download [WildFly server](http://wildfly.org/downloads/).
1. [Install Keycloak Adapter on WildFly](https://www.keycloak.org/docs/latest/securing_apps/index.html#jboss-eap-wildfly-adapter).
1. Run Wildfly with `$WILDFLY_ROOT/bin/standalone.[sh|bat]`.
1. Build and deploy this project with `mvn wildfly:deploy`.
1. Import `bpm-realm.json` with the neccesary configuration to Keycloak - [HOW TO](https://www.keycloak.org/docs/latest/server_admin/index.html#_create-realm)
