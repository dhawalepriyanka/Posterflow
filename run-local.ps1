param([string]$DeviceId = 'emulator-5554')

$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
$sdkSetting = Get-Content (Join-Path $PSScriptRoot 'local.properties') |
    Where-Object { $_ -match '^sdk\.dir=' } | Select-Object -First 1
$sdkPath = ($sdkSetting -replace '^sdk\.dir=', '') -replace '\\:', ':' -replace '\\\\', '\'
if (-not (Test-Path "$sdkPath\platform-tools\adb.exe")) {
    throw 'Set sdk.dir in local.properties to your installed Android SDK.'
}
$javaCandidates = @($env:JAVA_HOME, 'C:\Program Files\Android\Android Studio1\jbr', 'C:\Program Files\Android\Android Studio\jbr')
$javaPath = $javaCandidates | Where-Object { $_ -and (Test-Path "$_\bin\java.exe") } | Select-Object -First 1
if (-not $javaPath) { throw 'Set JAVA_HOME to the jbr folder in Android Studio.' }
$env:JAVA_HOME = $javaPath
$env:ANDROID_HOME = $sdkPath

Write-Host 'Building PosterFlow...'
& "$PSScriptRoot\gradlew.bat" :app:assembleDebug --console=plain
if ($LASTEXITCODE -ne 0) { throw 'Build failed. See the error above.' }

$adb = Join-Path $sdkPath 'platform-tools\adb.exe'
& $adb -s $DeviceId get-state
if ($LASTEXITCODE -ne 0) { throw 'Start an emulator in Android Studio, or pass -DeviceId <serial> for a connected phone.' }
$apk = Join-Path $PSScriptRoot 'app\build\outputs\apk\debug\app-debug.apk'
& $adb -s $DeviceId install -r $apk
if ($LASTEXITCODE -ne 0) { throw 'APK installation failed.' }
& $adb -s $DeviceId shell am start -W -n 'com.aistudio.postermaker.shydv/com.example.MainActivity'
if ($LASTEXITCODE -ne 0) { throw 'App launch failed.' }
Write-Host "PosterFlow launched. APK: $apk"
