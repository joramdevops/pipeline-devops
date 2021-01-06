def call(){
    stages = ["Build&Test", "Sonar", "Run", "Rest", "Nexus"] as String[]
    
        // Si stage es vacio se consideran todos los stages
    _stage = params.stage ? params.stage.split(';') : stages
    // Se valida stage ingresado
    _stage.each { el ->
        if (!stages.contains(el)) {
            throw new Exception("Stage: $el no es una opción válida.")
        }
    }

       if(_stage.contains('Build&Test')) {
            stage('Build&Test') {
              env.LAST_STAGE_NAME = env.STAGE_NAME
                sh "gradle clean build"
            }
         }   

        if(_stage.contains("Sonar")){
            stage('Sonar'){
               env.LAST_STAGE_NAME = env.STAGE_NAME
               def scannerHome = tool 'sonar-scanner';
               withSonarQubeEnv('Sonar'){ 
                   sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
                }  
            }
        }

        if(_stage.contains("Run")){
            stage('Run'){
              env.LAST_STAGE_NAME = env.STAGE_NAME
                sh 'nohup gradle bootRun &'
                sleep 10
            }
        }

        if(_stage.contains("Rest")){
            stage('Rest'){
                env.LAST_STAGE_NAME = env.STAGE_NAME
                sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'
            }
        }

        if(_stage.contains("Nexus")){
            stage('Nexus'){
                env.LAST_STAGE_NAME = env.STAGE_NAME
                nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]    
             }
        }

    }  

return this;
