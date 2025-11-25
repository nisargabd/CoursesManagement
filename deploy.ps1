Write-Host "Starting CourseManagement Deployment to Minikube..." -ForegroundColor Green

# 1. Build Docker Images
Write-Host "Building Backend Image..." -ForegroundColor Cyan
docker build -t course-backend:latest ./course-backend -f ./course-backend/Dockerfile.backend
if ($LASTEXITCODE -ne 0) { Write-Error "Backend build failed"; exit 1 }

Write-Host "Building Frontend Image..." -ForegroundColor Cyan
docker build -t course-frontend:latest ./course-frontend -f ./course-frontend/Dockerfile.frontend
if ($LASTEXITCODE -ne 0) { Write-Error "Frontend build failed"; exit 1 }

Write-Host "Building Keycloak Image..." -ForegroundColor Cyan
docker build -t course-keycloak:latest . -f Dockerfile.keycloak
if ($LASTEXITCODE -ne 0) { Write-Error "Keycloak build failed"; exit 1 }

# 2. Load Images into Minikube
Write-Host "Loading images into Minikube (this may take a while)..." -ForegroundColor Cyan
minikube image load course-backend:latest
minikube image load course-frontend:latest
minikube image load course-keycloak:latest
# Pre-load standard images to save download time inside Minikube
minikube image load postgres:15-alpine
minikube image load redis:7-alpine

# 3. Apply Kubernetes Manifests
Write-Host "Applying Kubernetes Manifests..." -ForegroundColor Cyan
kubectl apply -f k8s/

Write-Host "Deployment applied successfully!" -ForegroundColor Green
Write-Host "Check status with: kubectl get pods -n course-management"
Write-Host "Frontend URL: http://localhost:30000"
Write-Host "Keycloak URL: http://localhost:30080"
