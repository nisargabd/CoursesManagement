Write-Host "Starting Port Forwarding for CourseManagement..." -ForegroundColor Green

# Function to start a background job
function Start-PortForward {
    param (
        [string]$Name,
        [string]$Service,
        [string]$Ports
    )
    Write-Host "Forwarding $Name ($Ports)..." -ForegroundColor Cyan
    Start-Job -Name "PF-$Name" -ScriptBlock {
        param($svc, $pts)
        kubectl port-forward svc/$svc $pts -n course-management
    } -ArgumentList $Service, $Ports | Out-Null
}

# Stop existing jobs if any
Get-Job | Where-Object Name -like "PF-*" | Stop-Job | Remove-Job

# Start forwarding
Start-PortForward -Name "Frontend" -Service "frontend" -Ports "4200:80"
Start-PortForward -Name "Keycloak" -Service "keycloak" -Ports "8080:8080"
Start-PortForward -Name "Backend" -Service "backend" -Ports "9099:9099"

Write-Host "Port forwarding started in background jobs." -ForegroundColor Green
Write-Host "Access the application at:"
Write-Host "  Frontend: http://localhost:4200"
Write-Host "  Keycloak: http://localhost:8080"
Write-Host "  Backend:  http://localhost:9099"
Write-Host ""
Write-Host "To stop forwarding, run: Get-Job | Stop-Job"
