def call(){
  
pipeline {
        agent any
        parameters { 
                parameters { choice(name: 'herramienta', choices: ['gradle','maven'], description: 'Seleccione la herramienta para la aplicación') }
                string(name: 'stage', defaultValue: '', description: '')
        }
        stages {
                stage('Pipelines') {
                        environment {
                            LAST_STAGE_NAME = ''
                        }
                        steps {
                                script {

                                    println 'herramienta: ' + params.herramienta
                                    println 'stage: ' + params.stage
                                        
                                        if(params.herramienta == 'gradle'){ 
                                        gradle.call()
                                         }else{
                                         maven.call()
                                         }
                                }
                        }
                }
        }
         post {
                success {
                        slackSend color: 'good', message: "[Joram Diaz][${env.JOB_NAME}][${params.herramienta}] Ejecución exitosa."
                }
                failure {
                        slackSend color: 'danger', message: "[Joram Diaz][${env.JOB_NAME}][${params.herramienta}] Ejecución fallida en stage [${env.LAST_STAGE_NAME}]."
                                }
                }
        }
 }

return this;
