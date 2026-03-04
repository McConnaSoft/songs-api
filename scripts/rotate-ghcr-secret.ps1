param(
  [string]$Namespace = "songs",
  [string]$SecretName = "ghcr-secret",
  [string]$Registry = "ghcr.io",
  [string]$UsernameEnv = "GHCR_USER",
  [string]$TokenEnv = "GHCR_PAT",
  [string]$Email = "anything@example.com",
  [string]$DeployName = "songs-api",
  [switch]$RestartDeploy
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Require-Cmd($cmd) {
  if (-not (Get-Command $cmd -ErrorAction SilentlyContinue)) {
    throw "Missing required command: $cmd"
  }
}

function Exec([string]$exe, [string[]]$argv) {
  Write-Host "==> $exe $($argv -join ' ')"
  & $exe @argv
  if ($LASTEXITCODE -ne 0) {
    throw "Command failed (exit $LASTEXITCODE): $exe $($argv -join ' ')"
  }
}

Require-Cmd kubectl

$User = [Environment]::GetEnvironmentVariable($UsernameEnv, "Process")
if ([string]::IsNullOrWhiteSpace($User)) {
  $User = [Environment]::GetEnvironmentVariable($UsernameEnv, "User")
}
$Pat = [Environment]::GetEnvironmentVariable($TokenEnv, "Process")
if ([string]::IsNullOrWhiteSpace($Pat)) {
  $Pat = [Environment]::GetEnvironmentVariable($TokenEnv, "User")
}

if ([string]::IsNullOrWhiteSpace($User)) { throw "Env var $UsernameEnv is not set (e.g. `$env:$UsernameEnv='mcconnasoft')" }
if ([string]::IsNullOrWhiteSpace($Pat))  { throw "Env var $TokenEnv is not set (e.g. `$env:$TokenEnv='ghp_...')" }

Write-Host ""
Write-Host "==> Rotating image pull secret"
Write-Host "    Namespace : $Namespace"
Write-Host "    Secret    : $SecretName"
Write-Host "    Registry  : $Registry"
Write-Host "    User      : $User"
Write-Host ""

# Create/Update secret (idempotent) using server-side apply via yaml pipe
# NOTE: This does not print the PAT anywhere.
$yaml = & kubectl -n $Namespace create secret docker-registry $SecretName `
  --docker-server=$Registry `
  --docker-username=$User `
  --docker-password=$Pat `
  --docker-email=$Email `
  --dry-run=client -o yaml

if ($LASTEXITCODE -ne 0) { throw "Failed generating secret yaml" }

# Apply it (create or update)
$yaml | kubectl apply -f -
if ($LASTEXITCODE -ne 0) { throw "Failed applying secret" }

Write-Host ""
Write-Host "==> Secret updated. Verifying type + size:"
Exec "kubectl" @("-n", $Namespace, "get", "secret", $SecretName, "-o", "jsonpath={.type}{'  '}{.data..dockerconfigjson}{'\n'}")

Write-Host ""
Write-Host "==> Reminder: your Deployment must reference this secret:"
Write-Host "    spec.template.spec.imagePullSecrets: - name: $SecretName"

if ($RestartDeploy) {
  Write-Host ""
  Write-Host "==> Restarting deployment to force re-pull on next schedule:"
  Exec "kubectl" @("-n", $Namespace, "rollout", "restart", "deployment/$DeployName")
  Exec "kubectl" @("-n", $Namespace, "rollout", "status", "deployment/$DeployName", "--timeout=180s")
}

Write-Host ""
Write-Host "✅ GHCR secret rotation complete."
Write-Host "Tip: run with -RestartDeploy if you want an immediate re-pull."
Write-Host ""