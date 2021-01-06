def call()  {
    stages = ['Compile', 'Test', 'Jar', 'Sonar', 'Run', 'Rest', 'Nexus']

    // Si stage es vacio se consideran todos los stages
    _stage = params.stage ? params.stage.split(';') : stages
    // Se valida stage ingresado
    _stage.each { el ->
        if (!stages.contains(el)) {
            throw new Exception("Stage: $el no es una opción válida.")
        }
    }

    if(_stage.contains('Compile')) {
        stage('Compile') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            sh "mvn clean compile -e"
        }
    }
    
         if(_stage.contains('Test')) {
        stage('Test') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            sh "mvn clean test -e"
        }
    }

    if(_stage.contains('Jar')) {
        stage('Jar') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            sh "mvn clean package -e"
        }
    }

    if(_stage.contains('Sonar')) {
        stage('Sonar') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            withSonarQubeEnv(installationName: 'Sonar') {
                sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
            }
        }
    }

    if(_stage.contains('Run')) {
        stage('Run') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            sh 'mvn spring-boot:run &'
            sleep 10
        }
      }

    if(_stage.contains('Rest')) {
         stage('Rest') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'
        }
      }
  

    if(_stage.contains('Nexus')) {
        stage('Nexus') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
        }
    }
}

return this;
