pipeline {
    agent any

    stages {
        stage ('test'){
            steps{
                sh '''
                mvn test -Ddoozer.directory=src/test/java/dev/softtest/doozer/scripts/ -Ddoozer.failOnPixelDiff=false
                '''
            }
        }
    }
}