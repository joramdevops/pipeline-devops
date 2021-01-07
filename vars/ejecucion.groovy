def call(){
  
pipeline {
    agent any
  
    parameters { 
         choice(name: 'buildtool', choices: ['gradle','maven'], description: 'Seleccione la herramienta para la aplicación') 
      // string(name: 'stages', defaultValue: '', description: 'Escriba el stage que quiere ejecutar: "Build&Test", "Sonar", "RunJar", "Rest", "Nexus". Sino escribe se ejecutarán todos')
    }   
      
    stages {
         stage('Pipeline') {
               steps {
               script {
                      env.VARIABLE = 'asasas'       
                      println 'La herramienta de ejecucion es: ' + params.buildtool
                            
                      if(params.buildtool == 'gradle'){ 
                         gradle.call()
                         string(name: 'stages', defaultValue: '', description: 'Escriba el stage que quiere ejecutar: "Build&Test", "Sonar", "RunJar", "Rest", "Nexus". Sino escribe se ejecutarán todos')
                      }else{
                         maven.call()
                         string(name: 'stages', defaultValue: '', description: 'Escriba el stage que quiere ejecutar: "Compile", "Test", "Jar", "Sonar", "Run", "Rest", "Nexus". Sino escribe se ejecutarán todos')
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
