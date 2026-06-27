# Admin Service

Spring Boot REST API deployed on Kubernetes using Docker and Minikube.

This repository is part of a complete DevOps learning journey where the application will gradually evolve from a simple Kubernetes deployment to a production-ready deployment using Helm, CI/CD, monitoring, ingress, and AWS.

---

# Technology Stack

| Technology | Version |
|------------|----------|
| Java | 21 |
| Spring Boot | 4.x |
| PostgreSQL | 17 |
| Docker | Latest |
| Kubernetes | Minikube |
| Maven | Wrapper (mvnw) |

---

# Phase 1 Architecture

```text
Minikube Cluster
│
├── Namespace: database
│     ├── PostgreSQL Deployment
│     └── PostgreSQL Service (ClusterIP)
│
└── Namespace: dev
      ├── Admin Service Deployment
      └── Admin Service Service (NodePort)
```

---

# Application Flow

```text
Client
   │
   ▼
admin-service (NodePort)
   │
   ▼
Admin Service Pod
   │
   ▼
postgres-service.database.svc.cluster.local
   │
   ▼
PostgreSQL Pod
```

---

# Project Structure

```text
admin-service
│
├── src/
│
├── k8s/
│   │
│   ├── namespaces/
│   │      ├── database-namespace.yaml
│   │      └── dev-namespace.yaml
│   │
│   ├── database/
│   │      ├── postgres-deployment.yaml
│   │      └── postgres-service.yaml
│   │
│   └── admin-service/
│          ├── admin-deployment.yaml
│          └── admin-service.yaml
│
├── Dockerfile
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

# Kubernetes Namespace Strategy

This project uses two namespaces.

## database

The **database** namespace contains infrastructure-related components.

Current Resources

- PostgreSQL Deployment
- PostgreSQL Service

Future Resources

- Redis
- Kafka
- RabbitMQ
- MongoDB

---

## dev

The **dev** namespace contains application workloads.

Current Resources

- Admin Service Deployment
- Admin Service Service

Future Resources

- Employee Service
- User Service
- API Gateway
- Config Server
- Authentication Service

---

# Why Separate Namespaces?

Separating infrastructure from applications is considered a Kubernetes best practice.

Benefits

- Better isolation
- Easier security management
- Independent scaling
- Cleaner resource organization
- Easier migration to cloud

Later, PostgreSQL can be migrated to

- Amazon RDS
- Azure Database
- Google Cloud SQL
- Dedicated Kubernetes Cluster

without changing the application architecture.

---

# Prerequisites

Verify the required tools are installed.

### Docker

```powershell
docker --version
```

---

### Kubernetes CLI

```powershell
kubectl version --client
```

---

### Minikube

```powershell
minikube version
```

---

### Start Minikube

```powershell
minikube start
```

---

### Verify Cluster

```powershell
kubectl get nodes
```

Expected Output

```text
NAME       STATUS   ROLES
minikube   Ready    control-plane
```

---

# Create Kubernetes Namespaces

Apply the namespace manifests.

```powershell
kubectl apply -f k8s/namespaces/
```

Verify

```powershell
kubectl get namespaces
```

Expected Output

```text
NAME
database
dev
default
kube-system
```

---

# Deploy PostgreSQL

Deploy PostgreSQL resources.

```powershell
kubectl apply -f k8s/database/
```

Verify Deployment

```powershell
kubectl get deployments -n database
```

Verify Pod

```powershell
kubectl get pods -n database
```

Expected

```text
NAME                         READY   STATUS
postgres-xxxxxxxxxx          1/1     Running
```

---

Verify Service

```powershell
kubectl get svc -n database
```

Expected

```text
NAME                TYPE        CLUSTER-IP
postgres-service    ClusterIP   10.x.x.x
```

---

# Verify PostgreSQL Connectivity

Connect to PostgreSQL.

```powershell
kubectl exec -it -n database deployment/postgres -- psql -U postgres
```

List Schemas

```sql
\dn
```

Expected

```text
Employee_app
public
```

List Tables

```sql
\dt Employee_app.*
```

Exit PostgreSQL

```sql
\q
```

---

# Spring Boot Configuration

Update the datasource configuration.

```properties
spring.application.name=admin-service

server.port=9007

spring.datasource.url=jdbc:postgresql://postgres-service.database.svc.cluster.local:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=2001

spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update

spring.jpa.properties.hibernate.default_schema=Employee_app

spring.jpa.properties.hibernate.hbm2ddl.create_namespaces=true
```

---

# Build the Application

Using Maven Wrapper

```powershell
.\mvnw clean install -DskipTests
```

Expected

```text
BUILD SUCCESS
```

---

# Build Docker Image

Build the Docker image.

```powershell
docker build -t admin-service:v1 .
```

Verify the image.

```powershell
docker images
```

Expected Output

```text
REPOSITORY      TAG
admin-service   v1
```

---

# Load Docker Image into Minikube

Since Minikube has its own container runtime, load the image into the Minikube cluster.

```powershell
minikube image load admin-service:v1
```

Verify

```powershell
minikube image ls | findstr admin-service
```

Expected

```text
docker.io/library/admin-service:v1
```

---

# Deploy Admin Service

Deploy the application.

```powershell
kubectl apply -f k8s/admin-service/
```

Expected

```text
deployment.apps/admin-service created
service/admin-service created
```

---

# Verify Deployment

Check Deployments

```powershell
kubectl get deployments -n dev
```

Expected

```text
NAME            READY
admin-service   1/1
```

---

Check Pods

```powershell
kubectl get pods -n dev
```

Expected

```text
NAME                               READY   STATUS
admin-service-xxxxxxxxxx           1/1     Running
```

---

Check Services

```powershell
kubectl get svc -n dev
```

Expected

```text
NAME            TYPE
admin-service   NodePort
```

---

# View Application Logs

View deployment logs.

```powershell
kubectl logs -n dev deployment/admin-service
```

Expected

```text
Tomcat started on port 9007

Started AdminServiceApplication
```

You should also see Hibernate successfully connecting to PostgreSQL.

---

# Verify Database Objects

Connect to PostgreSQL.

```powershell
kubectl exec -it -n database deployment/postgres -- psql -U postgres
```

Show Schemas

```sql
\dn
```

Expected

```text
Employee_app
public
```

---

Show Tables

```sql
\dt Employee_app.*
```

Expected

```text
users
```

Exit PostgreSQL

```sql
\q
```

---

# Access the Application

Since Minikube is running using the Docker driver on Windows, expose the service using:

```powershell
minikube service admin-service -n dev --url
```

Example Output

```text
http://127.0.0.1:57098
```

> **Important:** Keep this terminal window open. It maintains the tunnel to the NodePort service.

---

# Test REST APIs

## Get All Users

```powershell
Invoke-RestMethod http://127.0.0.1:57098/api/v1/users
```

Expected

```json
[]
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

## Verify User Creation

```powershell
Invoke-RestMethod http://127.0.0.1:57098/api/v1/users
```

Expected

```json
[
  {
    "id":1,
    "username":"Arul",
    "role":"ADMIN"
  }
]
```

---

# Updating the Application

Whenever code changes are made, follow this sequence.

## Step 1 - Build the Project

```powershell
.\mvnw clean install -DskipTests
```

---

## Step 2 - Build Docker Image

```powershell
docker build -t admin-service:v1 .
```

---

## Step 3 - Load Image into Minikube

```powershell
minikube image load admin-service:v1
```

---

## Step 4 - Restart Deployment

```powershell
kubectl rollout restart deployment/admin-service -n dev
```

---

## Step 5 - Verify Rollout

```powershell
kubectl rollout status deployment/admin-service -n dev
```

Expected

```text
deployment "admin-service" successfully rolled out
```

---

# Common Verification Commands

Check all namespaces

```powershell
kubectl get namespaces
```

---

Check all deployments

```powershell
kubectl get deployments -A
```

---

Check all pods

```powershell
kubectl get pods -A
```

---

Check all services

```powershell
kubectl get svc -A
```

---

Describe Deployment

```powershell
kubectl describe deployment admin-service -n dev
```

---

Describe Pod

```powershell
kubectl describe pod <pod-name> -n dev
```

---

Watch Pods

```powershell
kubectl get pods -n dev -w
```

---

View Logs

```powershell
kubectl logs -f deployment/admin-service -n dev
```

---

Delete Deployment

```powershell
kubectl delete deployment admin-service -n dev
```

---

Delete Service

```powershell
kubectl delete service admin-service -n dev
```

---

Recreate Application

```powershell
kubectl apply -f k8s/admin-service/
```

---

# Troubleshooting

## Pod is in ImagePullBackOff

Verify the image exists inside Minikube.

```powershell
minikube image ls | findstr admin-service
```

If the image is missing, load it again.

```powershell
minikube image load admin-service:v1
```

Restart the deployment.

```powershell
kubectl rollout restart deployment/admin-service -n dev
```

---

## Pod is in CrashLoopBackOff

View the application logs.

```powershell
kubectl logs deployment/admin-service -n dev
```

Common reasons

- Incorrect datasource URL
- PostgreSQL not running
- Invalid application.properties
- Missing environment variables

---

## PostgreSQL Pod Not Starting

Check the pod.

```powershell
kubectl get pods -n database
```

View logs.

```powershell
kubectl logs deployment/postgres -n database
```

Describe the pod.

```powershell
kubectl describe pod <postgres-pod-name> -n database
```

---

## Cannot Access REST APIs

Verify the service.

```powershell
kubectl get svc -n dev
```

Expose the service.

```powershell
minikube service admin-service -n dev --url
```

Keep the terminal window open while accessing the application.

---

## Verify Kubernetes Resources

Deployments

```powershell
kubectl get deployments -A
```

Pods

```powershell
kubectl get pods -A
```

Services

```powershell
kubectl get svc -A
```

Namespaces

```powershell
kubectl get namespaces
```

---

# Clean Up Resources

Delete only the Admin Service.

```powershell
kubectl delete -f k8s/admin-service/
```

Delete PostgreSQL.

```powershell
kubectl delete -f k8s/database/
```

Delete Namespaces.

```powershell
kubectl delete -f k8s/namespaces/
```

Delete everything inside the cluster.

```powershell
kubectl delete all --all -A
```

Delete the entire Minikube cluster.

```powershell
minikube delete
```

Start a fresh cluster.

```powershell
minikube start
```

---

# DevOps Learning Roadmap

This repository will gradually evolve into a production-ready Kubernetes deployment.

## ✅ Phase 1 — Basic Kubernetes Deployment

Completed

- Spring Boot Application
- PostgreSQL
- Docker
- Kubernetes
- Namespaces
- Deployments
- Services
- NodePort
- ClusterIP

---

## 🔜 Phase 2 — Kubernetes Best Practices

Topics

- Separate YAML files
- Labels
- Selectors
- Resource Limits
- Requests
- Readiness Probe
- Liveness Probe
- Startup Probe

---

## 🔜 Phase 3 — Configuration Management

Topics

- ConfigMap
- Secret
- Environment Variables
- Volume Mounts

---

## 🔜 Phase 4 — Persistent Storage

Topics

- Persistent Volume (PV)
- Persistent Volume Claim (PVC)
- Storage Classes
- PostgreSQL Persistent Storage

---

## 🔜 Phase 5 — Helm

Topics

- Helm Installation
- Helm Charts
- Templates
- Values.yaml
- Helpers
- Release Management

---

## 🔜 Phase 6 — Multi-Environment Deployment

Namespaces

```text
database
dev
sit
uat
preprod
prod
```

Topics

- Environment-specific configuration
- Multiple Helm values files
- Promotion strategy

---

## 🔜 Phase 7 — Ingress

Topics

- NGINX Ingress Controller
- Host-based Routing
- Path-based Routing
- TLS/HTTPS

---

## 🔜 Phase 8 — CI/CD Pipeline

Tools

- GitHub Actions
- Jenkins
- Docker Hub / Container Registry

Pipeline Flow

```text
Developer

↓

Git Push

↓

Build

↓

Unit Tests

↓

Docker Image

↓

Container Registry

↓

Deploy to Kubernetes
```

---

## 🔜 Phase 9 — Monitoring

Topics

- Prometheus
- Grafana
- Metrics
- Dashboards

---

## 🔜 Phase 10 — Logging

Topics

- Elasticsearch
- Fluent Bit
- Kibana

---

## 🔜 Phase 11 — Autoscaling

Topics

- Horizontal Pod Autoscaler (HPA)
- Vertical Pod Autoscaler (VPA)

---

## 🔜 Phase 12 — Security

Topics

- RBAC
- Service Accounts
- Network Policies
- Image Scanning

---

## 🔜 Phase 13 — AWS Migration

Infrastructure

```text
Amazon EKS

↓

Amazon RDS PostgreSQL

↓

Amazon ECR

↓

Application Load Balancer

↓

CloudWatch

↓

Route53
```

---

# Final Architecture

Current Architecture

```text
                   Minikube Cluster
────────────────────────────────────────────────────

                Namespace : dev

          +---------------------------+
          |       admin-service       |
          |      Deployment           |
          +------------+--------------+
                       |
                       |
                +------+------+
                |  NodePort   |
                |   Service   |
                +------+------+
                       |
                       |
                       ▼

────────────────────────────────────────────────────

             Namespace : database

          +---------------------------+
          |    postgres-service       |
          |      ClusterIP            |
          +------------+--------------+
                       |
                       |
                +------+------+
                | PostgreSQL  |
                | Deployment  |
                +-------------+

────────────────────────────────────────────────────
```

---

# Learning Outcomes

After completing this project, you will have practical experience with:

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Docker
- Docker Images
- Docker Containers
- Kubernetes
- Pods
- Deployments
- Services
- Namespaces
- ClusterIP
- NodePort
- Service Discovery
- Minikube
- kubectl
- YAML
- Kubernetes Networking
- Kubernetes Troubleshooting

Future phases will introduce:

- ConfigMaps
- Secrets
- Persistent Volumes
- Helm
- Ingress
- CI/CD
- Monitoring
- Logging
- Autoscaling
- Security
- AWS EKS

---

# Author

**Arul Kumaran**

This repository is maintained as a hands-on DevOps learning project to understand how enterprise-grade Java applications are deployed using Docker, Kubernetes, and modern DevOps practices.

---