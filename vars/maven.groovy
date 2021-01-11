def call()  {
    figlet 'Maven'
    
    stages = ['compile', 'test', 'jar', 'run', 'rest', 'sonar','nexus']
    
    Stage = params.stage ? params.stage.split(';') : stages
        Stage.each { opcion ->

        if (!stages.contains(opcion)) {
            throw new Exception("Stage: $opcion vuelva a ingresar un stage valido.")
        }
    }

    if(Stage.contains('compile')) {
        figlet 'Compile'
        stage('compile') {
            env.VARIABLE = env.STAGEIN
            sh "./mvnw clean compile -e"
        }
    }
    
    if(Stage.contains('test')) {
        figlet 'Test'
        stage('test') {
            env.VARIABLE = env.STAGEIN
            sh "./mvnw clean test -e"
        }
    }

    if(Stage.contains('jar')) {
        figlet 'Jar'
        stage('jar') {
            env.VARIABLE = env.STAGEIN
            sh "./mvnw clean package -e"
        }
    }

    if(Stage.contains('run')) {
        figlet 'Run'
        stage('run') {
            env.VARIABLE = env.STAGEIN
            sh './mvnw spring-boot:run &'
        }
    }
    
   if(Stage.contains('rest')) {
        figlet 'Rest'
        stage('rest') {
            env.VARIABLE = env.STAGEIN
            sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'   
        }
    }

    if(Stage.contains('sonar')) {
        figlet 'Sonar'
        stage('sonar') {
            env.VARIABLE = env.STAGEIN
            withSonarQubeEnv(installationName: 'sonar-server') {
            sh './mvnw org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'   
        }
    } 
            
    if(Stage.contains('nexus')) {
        figlet 'Nexus'
        stage('nexus') {
            env.VARIABLE = env.STAGEIN
            nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
        }
    } 

}

return this;
