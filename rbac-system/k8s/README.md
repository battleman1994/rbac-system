# Kubernetes Deployment Guide

## Prerequisites

- Kubernetes cluster (v1.20+)
- kubectl configured
- Ingress controller (nginx-ingress recommended)

## Deployment Steps

### 1. Create namespace and deploy MySQL

```bash
kubectl apply -f k8s/01-mysql.yaml
```

Wait for MySQL to be ready:
```bash
kubectl wait --for=condition=ready pod -l app=mysql -n rbac-system --timeout=300s
```

### 2. Deploy Backend

Update the JWT_SECRET in `02-backend.yaml` before deploying:

```bash
kubectl apply -f k8s/02-backend.yaml
```

### 3. Deploy Frontend

```bash
kubectl apply -f k8s/03-frontend.yaml
```

### 4. Deploy Redis (Optional)

```bash
kubectl apply -f k8s/04-redis.yaml
```

### 5. Update Ingress Hosts

Edit the Ingress resources to use your actual domain names:

```bash
kubectl edit ingress backend-ingress -n rbac-system
kubectl edit ingress frontend-ingress -n rbac-system
```

## Useful Commands

```bash
# Check all resources
kubectl get all -n rbac-system

# Check logs
kubectl logs -f deployment/backend -n rbac-system
kubectl logs -f deployment/frontend -n rbac-system

# Scale deployments
kubectl scale deployment backend --replicas=3 -n rbac-system

# Port forward for local testing
kubectl port-forward svc/backend 8080:8080 -n rbac-system
kubectl port-forward svc/frontend 3000:80 -n rbac-system

# Delete everything
kubectl delete namespace rbac-system
```

## Production Notes

1. Change all default passwords in Secrets
2. Use proper TLS certificates for Ingress
3. Configure resource limits based on actual load
4. Set up monitoring with Prometheus/Grafana
5. Configure backup for MySQL persistent volume
