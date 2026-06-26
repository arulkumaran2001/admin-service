# admin-service
contains devops

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.5/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.5/maven-plugin/build-image.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.0.5/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [SpringDoc OpenAPI](https://springdoc.org/)
* [Spring Web](https://docs.spring.io/spring-boot/4.0.5/reference/web/servlet.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [SpringDoc OpenAPI](https://github.com/springdoc/springdoc-openapi-demos/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.





# Admin Service Application Deployment Guide

## Spring Boot + PostgreSQL + Docker + Kubernetes (Minikube)

---

# Architecture

```text
Minikube Cluster
│
├── Namespace: database
│     ├── PostgreSQL Deployment
│     └── PostgreSQL Service
│
└── Namespace: app1
      ├── Spring Boot Deployment
      └── Spring Boot Service (NodePort)
```

---

# Prerequisites

Verify installation:

```powershell
docker --version
```

```powershell
kubectl version --client
```

```powershell
minikube version
```

Check Minikube status:

```powershell
kubectl get nodes
```

Expected:

```text
NAME       STATUS   ROLES           AGE
minikube   Ready    control-plane
```

---

# Create Namespaces

```powershell
kubectl create namespace database
```

```powershell
kubectl create namespace app1
```

Verify:

```powershell
kubectl get namespaces
```

Expected:

```text
database
app1
default
kube-system
```

---

# PostgreSQL Deployment

## postgres-deployment.yaml

```yaml
apiVersion: apps/v1
kind: Deployment

metadata:
  name: postgres
  namespace: database

spec:
  replicas: 1

  selector:
    matchLabels:
      app: postgres

  template:
    metadata:
      labels:
        app: postgres

    spec:
      containers:
        - name: postgres
          image: postgres:17

          ports:
            - containerPort: 5432

          env:
            - name: POSTGRES_DB
              value: postgres

            - name: POSTGRES_USER
              value: postgres

            - name: POSTGRES_PASSWORD
              value: "2001"
```

Apply:

```powershell
kubectl apply -f postgres-deployment.yaml
```

---

# PostgreSQL Service

## postgres-service.yaml

```yaml
apiVersion: v1
kind: Service

metadata:
  name: postgres-service
  namespace: database

spec:
  selector:
    app: postgres

  ports:
    - port: 5432
      targetPort: 5432

  type: ClusterIP
```

Apply:

```powershell
kubectl apply -f postgres-service.yaml
```

Verify:

```powershell
kubectl get pods -n database
```

```powershell
kubectl get svc -n database
```

Expected:

```text
postgres-service
```

---

# Spring Boot Configuration

## application.properties

```properties
spring.application.name=admin-service

server.port=9007

spring.datasource.url=jdbc:postgresql://postgres-service.database.svc.cluster.local:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=2001
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.properties.hibernate.default_schema=Employee_app
spring.jpa.properties.hibernate.hbm2ddl.create_namespaces=true
spring.jpa.hibernate.ddl-auto=update
```

---

# Build Application

Skip tests:

```powershell
mvn clean install -DskipTests
```

If Maven is not installed globally:

```powershell
.\mvnw clean install -DskipTests
```

Expected:

```text
BUILD SUCCESS
```

---

# Build Docker Image

```powershell
docker build -t admin-service:v1 .
```

Verify:

```powershell
docker images
```

Expected:

```text
admin-service    v1
```

---

# Load Image into Minikube

```powershell
minikube image load admin-service:v1
```

Verify:

```powershell
minikube image ls | findstr admin-service
```

Expected:

```text
admin-service:v1
```

---

# Spring Boot Deployment

## employee-app.yaml

```yaml
apiVersion: apps/v1
kind: Deployment

metadata:
  name: employee-app
  namespace: app1

spec:
  replicas: 1

  selector:
    matchLabels:
      app: employee-app

  template:
    metadata:
      labels:
        app: employee-app

    spec:
      containers:
        - name: employee-app
          image: admin-service:v1
          imagePullPolicy: Never

          ports:
            - containerPort: 9007

---
apiVersion: v1
kind: Service

metadata:
  name: employee-app-service
  namespace: app1

spec:
  selector:
    app: employee-app

  ports:
    - port: 9007
      targetPort: 9007

  type: NodePort
```

Deploy:

```powershell
kubectl apply -f employee-app.yaml
```

---

# Verify Deployment

Pods:

```powershell
kubectl get pods -n app1
```

Deployments:

```powershell
kubectl get deployments -n app1
```

Services:

```powershell
kubectl get svc -n app1
```

Expected:

```text
employee-app
employee-app-service
```

---

# Check Application Logs

```powershell
kubectl logs -n app1 deployment/employee-app
```

Successful startup:

```text
Tomcat started on port 9007
Started EmployeeManagementApplication
```

---

# Verify Schema Creation

Connect to PostgreSQL:

```powershell
kubectl exec -it -n database deployment/postgres -- psql -U postgres
```

Check schemas:

```sql
\dn
```

Expected:

```text
Employee_app
public
```

Check tables:

```sql
\dt Employee_app.*
```

Expected:

```text
users
```

Exit PostgreSQL:

```sql
\q
```

---

# Access Application

Get URL:

```powershell
minikube service employee-app-service -n app1 --url
```

Example:

```text
http://127.0.0.1:57098
```

> Keep this terminal open. Minikube creates a tunnel for NodePort access when using the Docker driver on Windows.

---

# Test APIs

## Get Users

```powershell
Invoke-RestMethod http://127.0.0.1:57098/api/v1/users
```

---

## Create User

```powershell
$body = @{
    username = "Arul"
    role = "ADMIN"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri http://127.0.0.1:57098/api/v1/user `
  -Method POST `
  -Body $body `
  -ContentType "application/json"
```

---

## Verify User

```powershell
Invoke-RestMethod http://127.0.0.1:57098/api/v1/users
```

---

# Useful Kubernetes Commands

## View All Pods

```powershell
kubectl get pods -A
```

## View Pods in App Namespace

```powershell
kubectl get pods -n app1
```

## View Pods in Database Namespace

```powershell
kubectl get pods -n database
```

## View Services

```powershell
kubectl get svc -A
```

## View Deployments

```powershell
kubectl get deployments -A
```

## View Logs

```powershell
kubectl logs -n app1 deployment/employee-app
```

## Delete Deployment

```powershell
kubectl delete deployment employee-app -n app1
```

## Delete Service

```powershell
kubectl delete svc employee-app-service -n app1
```

---

# Application Upgrade Flow

Whenever application code changes:

```powershell
mvn clean install -DskipTests
```

```powershell
docker build -t admin-service:v1 .
```

```powershell
minikube image load admin-service:v1
```

```powershell
kubectl rollout restart deployment employee-app -n app1
```

Check status:

```powershell
kubectl rollout status deployment employee-app -n app1
```

---

# Learning Summary

### Docker

- Dockerfile
- Image
- Container
- Docker Build

### Kubernetes

- Namespace
- Pod
- Deployment
- Service
- ClusterIP
- NodePort
- Logs
- Rollout Restart

### Spring Boot

- REST API
- JPA
- Hibernate
- PostgreSQL Integration
- Automatic Schema Creation

### Final Architecture

```text
employee-app (Namespace: app1)
        |
        |
        v
postgres-service (Namespace: database)
        |
        |
        v
postgres Pod
```

The application never talks directly to the PostgreSQL Pod. It communicates through the Kubernetes Service (`postgres-service`), which provides stable service discovery and load balancing.