def call(){
  
pipeline {
    agent any
  
    parameters { 
         choice(name: 'buildtool', choices: ['gradle','maven'], description: 'Seleccione la herramienta para la aplicación') 
         
         string(name: 'stages', defaultValue: '', description: 'Escriba el stage que quiere ejecutar. Sino escribe se ejecutarán todos')
    }   
      
    stages {
         stage('Pipeline') {
            steps {
               script {
                          
               println 'La herramienta seleccionada es: ' + params.buildtool
               println 'El stage seleccionado es: ' + params.stages
                            
                      if(params.buildtool == 'gradle'){ 
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
                        slackSend color: 'good', message: "[Joram Diaz][${env.JOB_NAME}][${params.buildtool}] Ejecución exitosa."
                        }
                failure {
                        slackSend color: 'danger', message: "[Joram Diaz][${env.JOB_NAME}][${params.buildtool}] Ejecución fallida en stage [${env.VARIABLE}]."
                        }
                }
        }
 }

return this;
