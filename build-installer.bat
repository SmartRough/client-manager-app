@echo off
setlocal ENABLEDELAYEDEXPANSION

:: CONFIGURACIÓN
set FX_SDK=D:\javafx-sdk-21
set JAR_NAME=client-manager-app-1.0-SNAPSHOT-jar-with-dependencies.jar
set TMP_RUNTIME=tmp-runtime
set TMP_DIR=tmp-dist
set INSTALLER_DIR=installer
set CAXA_EXE=ClientManager.exe
set ICON_PATH=target\app-icon.ico

echo 🧹 Limpiando carpetas anteriores...
rmdir /s /q %TMP_RUNTIME% 2>nul
rmdir /s /q %TMP_DIR% 2>nul
rmdir /s /q %INSTALLER_DIR% 2>nul

echo 🔍 Creando runtime con jlink...
jlink ^
  --module-path "%FX_SDK%\lib;%JAVA_HOME%\jmods" ^
  --add-modules java.base,javafx.controls,javafx.fxml,javafx.graphics,java.sql,java.desktop,java.logging,java.naming,jdk.unsupported,jdk.xml.dom,jdk.charsets ^
  --output %TMP_RUNTIME% ^
  --strip-debug --compress=2 --no-header-files --no-man-pages ^
  --bind-services

if errorlevel 1 (
    echo ❌ jlink falló.
    pause
    exit /b
)

echo 📁 Copiando runtime, JAR y DLLs...
mkdir %TMP_DIR%
xcopy /s /e /i /y %TMP_RUNTIME% %TMP_DIR%\runtime >nul
copy target\%JAR_NAME% %TMP_DIR%\%JAR_NAME% >nul
if exist %ICON_PATH% copy %ICON_PATH% %TMP_DIR%\app-icon.ico >nul

:: 🔧 Agregar DLLs nativos de JavaFX
xcopy /y /i "%FX_SDK%\bin\*.dll" %TMP_DIR%\runtime\bin >nul

echo 🚀 Empaquetando con Caxa...
npx caxa ^
  --input %TMP_DIR% ^
  --output %INSTALLER_DIR%\%CAXA_EXE% ^
  --no-include-node ^
  -- "cmd" "/c" "set PRISM_ORDER=sw && {{caxa}}/runtime/bin/javaw -jar {{caxa}}/%JAR_NAME%"

if errorlevel 1 (
    echo ❌ Error con Caxa.
    pause
    exit /b
)

echo ✅ ¡EXE generado!: %INSTALLER_DIR%\%CAXA_EXE%
pause