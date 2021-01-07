def call()  {
    figlet 'Maven'
    stages = ['Compile', 'Test', 'Jar', 'Sonar', 'Run', 'Rest', 'Nexus']

    Stage = params.stage ? params.stage.split(';') : stages
    Stage.each { opcion ->
        if (!stages.contains(opcion)) {
            throw new Exception("Stage: $opcion no es válido, vuelva a ingresar.")
        }
    }

    if(Stage.contains('Compile')) {
        figlet 'Compile'
        stage('Compile') {
            env.VARIABLE = env.STAGE_NAME
            sh "./mvnw clean compile -e"
        }
    }
    
         if(Stage.contains('Test')) {
            figlet 'Test'
            stage('Test') {
                env.VARIABLE = env.STAGE_NAME
                sh "./mvnw clean test -e"
        }
    }

    if(Stage.contains('Jar')) {
        figlet 'Jar'
        stage('Jar') {
            env.VARIABLE = env.STAGE_NAME
            sh "./mvnw clean package -e"
        }
    }

    if(Stage.contains('Sonar')) {
        figlet 'Sonar'
        stage('Sonar') {
            env.VARIABLE = env.STAGE_NAME
            withSonarQubeEnv(installationName: 'sonar-server') {
                sh './mvnw org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
            }
        }
    }

    if(Stage.contains('Run')) {
        figlet 'Run'
        stage('Run') {
            env.VARIABLE = env.STAGE_NAME
            sh './mvnw spring-boot:run &'
            sleep 10
        }
      }

    if(Stage.contains('Rest')) {
         figlet 'Rest'
         stage('Rest') {
            env.VARIABLE = env.STAGE_NAME
            sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'
        }
      }
  

    if(Stage.contains('Nexus')) {
        figlet 'Nexus'
        stage('Nexus') {
            env.VARIABLE = env.STAGE_NAME
            nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
        }
    }
}

return this;
