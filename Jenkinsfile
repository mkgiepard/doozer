pipeline {
    agent any

    stages {
        stage('Build') { 
            steps {
                sh 'mvn -B -DskipTests clean package' 
            }
        }
        stage ('Test'){
            steps{
                sh '''
                mvn test -Dgroups=\\!DOOZER
                '''
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml' 
                }
            }
        }
        stage ('Doozer Test'){
            steps{
                sh '''
                mvn test -Dtest=MyDoozerTest -Ddoozer.directory=src/test/java/dev/softtest/doozer/scripts/ -Ddoozer.failOnPixelDiff=false
                '''
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml' 
                }
            }
        }
    }
}