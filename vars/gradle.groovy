def call(){
    stages = ["Build&Test", "Sonar", "Run", "Rest", "Nexus"] as String[]
    
        // Si stage es vacio se consideran todos los stages
    Stage = params.stage ? params.stage.split(';') : stages
    // Se valida stage ingresado
    Stage.each { el ->
        if (!stages.contains(el)) {
            throw new Exception("Stage: $el no es una opción válida.")
        }
    }

       if(Stage.contains('Build&Test')) {
            stage('Build&Test') {
              env.VARIABLE = env.STAGE_NAME
                sh "./gradlew clean build"
            }
         }   

        if(Stage.contains("Sonar")){
            stage('Sonar'){
               env.VARIABLE = env.STAGE_NAME
               def scannerHome = tool 'sonar-scanner';
               withSonarQubeEnv('sonar-server'){ 
                   sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
                }  
            }
        }

        if(Stage.contains("Run")){
            stage('Run'){
              env.VARIABLE = env.STAGE_NAME
                sh 'nohup gradle bootRun &'
                sleep 10
            }
        }

        if(Stage.contains("Rest")){
            stage('Rest'){
                env.VARIABLE = env.STAGE_NAME
                sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'
            }
        }

        if(Stage.contains("Nexus")){
            stage('Nexus'){
                env.VARIABLE = env.STAGE_NAME
                nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]    
             }
        }

    }  

return this;
