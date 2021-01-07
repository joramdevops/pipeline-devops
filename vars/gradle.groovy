def call(){
    figlet 'gradle'
    stages = ["Build&Test", "Sonar", "RunJar", "Rest", "Nexus"] as String[]
    
    Stage = params.stage ? params.stage.split(';') : stages
    Stage.each { el ->
        if (!stages.contains(el)) {
            throw new Exception("Stage: $el no es una opción válida.")
        }
    }
     

       if(Stage.contains('Build&Test')) {
           figlet 'Build&Test' 
           stage('Build&Test') {
              env.VARIABLE = env.STAGE_NAME
                sh "./gradlew clean build"
            }
         }   

        if(Stage.contains("Sonar")){
            figlet 'Sonar'
            stage('Sonar'){
               env.VARIABLE = env.STAGE_NAME
               def scannerHome = tool 'sonar-scanner';
               withSonarQubeEnv('sonar-server'){ 
                   sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
                }  
            }
        }

        if(Stage.contains("Run")){
            figlet 'Run'
            stage('Run'){
              env.VARIABLE = env.STAGE_NAME
                sh 'nohup gradle bootRun &'
                sleep 10
            }
        }

        if(Stage.contains("Rest")){
            figlet 'Rest'
            stage('Rest'){
                env.VARIABLE = env.STAGE_NAME
                sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'
            }
        }

        if(Stage.contains("Nexus")){
            figlet 'Nexus'
            stage('Nexus'){
                env.VARIABLE = env.STAGE_NAME
                nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]    
             }
        }

    }  

return this;
