param(
  [Parameter(Mandatory=$false)]
  [string]$Owner = "mcconnasoft",

  [Parameter(Mandatory=$false)]
  [string]$ImageName = "songs-api",

  [Parameter(Mandatory=$true)]
  [string]$Tag,

  [Parameter(Mandatory=$false)]
  [string]$Namespace = "songs",

  [Parameter(Mandatory=$false)]
  [string]$DeployName = "songs-api"
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

# ---- Guardrails ----
if ($Tag -notmatch '^\d+\.\d+\.\d+$') {
  throw "Tag must look like SemVer (e.g., 0.1.3). You gave: '$Tag'"
}

Require-Cmd docker
Require-Cmd kubectl

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$RepoRoot = Resolve-Path (Join-Path $ScriptDir "..")

$DockerContext  = Join-Path $RepoRoot "ms"
$DockerfilePath = Join-Path $DockerContext "Dockerfile"
$K8sInfraDir    = Join-Path $RepoRoot "k8s\infra"
$K8sAppDir      = Join-Path $RepoRoot "k8s\app"

if (-not (Test-Path $DockerfilePath)) { throw "Dockerfile not found: $DockerfilePath" }
if (-not (Test-Path $K8sInfraDir))    { throw "Missing: $K8sInfraDir" }
if (-not (Test-Path $K8sAppDir))      { throw "Missing: $K8sAppDir" }

$ImageRef = "ghcr.io/$Owner/$ImageName`:$Tag"

Write-Host ""
Write-Host "==> Effective params: Owner='$Owner' ImageName='$ImageName' Tag='$Tag' Namespace='$Namespace' DeployName='$DeployName'"
Write-Host "==> Repo root: $RepoRoot"
Write-Host "==> Docker context: $DockerContext"
Write-Host "==> Dockerfile: $DockerfilePath"
Write-Host "==> Image: $ImageRef"
Write-Host ""

# ---- Ensure buildx builder exists ----
$BuilderName = "songsbuilder"
$builderExists = $false
try {
  docker buildx inspect $BuilderName *> $null
  $builderExists = $true
} catch { $builderExists = $false }

if (-not $builderExists) {
  Exec "docker" @("buildx", "create", "--name", $BuilderName, "--use")
} else {
  Exec "docker" @("buildx", "use", $BuilderName)
}

Exec "docker" @("buildx", "inspect", "--bootstrap")

# ---- Build + Push (ARM64 only; matches nodeSelector arm64) ----
# If/when your whole cluster becomes arm64, you're golden.
# If you later want arm/v7 too, we’ll switch to Java 17 + multi-arch.
Exec "docker" @(
  "buildx","build",
  "--platform","linux/arm64",
  "-f", $DockerfilePath,
  "-t", $ImageRef,
  "--push",
  $DockerContext
)

# ---- Apply manifests ----
Exec "kubectl" @("apply", "-f", $K8sInfraDir)
Exec "kubectl" @("apply", "-f", $K8sAppDir)

# ---- Force deployment to the new image (Option A core idea) ----
Exec "kubectl" @("-n", $Namespace, "set", "image", "deployment/$DeployName", "$DeployName=$ImageRef")

# ---- Rollout ----
Exec "kubectl" @("-n", $Namespace, "rollout", "status", "deployment/$DeployName", "--timeout=180s")

Write-Host ""
Write-Host "==> Pods:"
Exec "kubectl" @("-n", $Namespace, "get", "pods", "-o", "wide")

Write-Host ""
Write-Host "✅ Done. Test locally with:"
Write-Host "  kubectl -n $Namespace port-forward svc/$DeployName 8080:8080"
Write-Host "  Then hit: http://localhost:8080/actuator/health (if enabled) or your API endpoints"
Write-Host ""