@echo off
setlocal EnableDelayedExpansion

echo =========================================
echo Corriendo Proyecto Spring Boot (El Garaje)
echo =========================================
echo.

:: Verifica si JAVA_HOME esta configurado
if "%JAVA_HOME%"=="" (
    echo [ADVERTENCIA] JAVA_HOME no esta configurado en tus variables de entorno.
    echo Intentando continuar usando el comando 'java' del sistema...
)

:: Verifica si existe el Maven Wrapper en el proyecto
if not exist "mvnw.cmd" (
    echo [ERROR] No se encontro 'mvnw.cmd' en el directorio del proyecto.
    echo Asegurate de estar ejecutando este script desde la raiz del proyecto PDS-Grupo5.
    echo.
    pause
    exit /b 1
)

echo Iniciando aplicacion con Maven Wrapper...
echo.

:: Ejecuta Spring Boot usando el wrapper
call .\mvnw.cmd spring-boot:run

if !ERRORLEVEL! NEQ 0 (
    echo.
    echo [ERROR] La aplicacion se detuvo o fallo al iniciar. Revisa los logs arriba.
    echo Si ves un error de "Access denied", revisa la contrasena de MySQL en application.properties.
)

echo.
pause
