import pipeline.*
def call(String chosenStages)  {
    figlet 'Maven'
    
    def pipelineStages = ['compile', 'test', 'jar', 'run', 'rest', 'sonar','nexus']
    
    def utils  = new test.UtilMethods()
    def stages = util.getValidatedStages(chosenStages, pipelineStages)

 
    stages.each { 
        stage(it){
        try {
            "${it}"()
        }
        catch(Exception e){
            error "Stage ${it} tiene problemas: ${e}"        
        }
        }
    }
}

    def compile () {
        figlet 'Compile'
        sh "./mvnw clean compile -e"
    }
    
    
    def test() {
        figlet 'Test'
        sh "./mvnw clean test -e"
    }

    def jar () {
        figlet 'Jar'
        sh "./mvnw clean package -e"
    }

    def run() {
        figlet 'Run'
        sh './mvnw spring-boot:run &'
    }

    def rest() {
        figlet 'Rest'
        sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'    
    }
            
    def sonar () {
        figlet 'Sonar'
        withSonarQubeEnv(installationName: 'sonar-server') {
        sh './mvnw org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
        }
    }

    def nexus() {
        figlet 'Nexus'
        nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
        
    }
}

return this;
