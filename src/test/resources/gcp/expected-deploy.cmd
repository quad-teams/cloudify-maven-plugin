@ECHO off
@SETLOCAL
SET CREATE_PROJECT=false
SET PROJECT_ID=gcloud-mojo

WHERE /Q gcloud
IF %ERRORLEVEL% NEQ 0 (
    GOTO printInstructions
) else (
    GOTO deploy
)

:createProject
CALL gcloud projects create %PROJECT_ID%
CALL gcloud config set project %PROJECT_ID%
CALL gcloud config set run/region us-central1
CALL gcloud beta billing accounts list

@REM It reads the account ID
SET /P ACCOUNT_ID="Enter the billing account ID you want to use: "

CALL gcloud beta billing projects link %PROJECT_ID% --billing-account=%ACCOUNT_ID%
CALL gcloud services enable cloudbuild.googleapis.com
CALL gcloud services enable run.googleapis.com

FOR /F "tokens=*" %%a in ('gcloud beta projects describe %PROJECT_ID% --format="get(projectNumber)"') DO SET PROJECT_NUMBER=%%a
CALL gcloud projects add-iam-policy-binding %PROJECT_ID% --member serviceAccount:%PROJECT_NUMBER%@cloudbuild.gserviceaccount.com --role roles/run.admin
CALL gcloud projects add-iam-policy-binding %PROJECT_ID% --member serviceAccount:%PROJECT_NUMBER%@cloudbuild.gserviceaccount.com --role roles/iam.serviceAccountUser
CALL gcloud builds submit --config cloudbuild.yaml .
EXIT /B 0

:deployProject
CALL gcloud config set project %PROJECT_ID%
CALL gcloud builds submit --config cloudbuild.yaml .
EXIT /B 0

:deploy
IF "%CREATE_PROJECT%" == "true" (
    GOTO createProject
) ELSE (
    GOTO deployProject
)

:printInstructions
ECHO ========================================================================
ECHO In order to deploy this project, you need gcloud set in your PATH.
ECHO You can download it from https://cloud.google.com/sdk
ECHO ========================================================================
EXIT /B 0
