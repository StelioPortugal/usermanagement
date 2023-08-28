pipeline {
    agent any
    
    environment {
        // Set the JAVA_HOME environment variable to your Java installation path
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
    }
    
    stages {
        stage('Checkout') {
            steps {
                // Checkout the source code from your Git repository
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], userRemoteConfigs: [[url: 'https://github.com/StelioPortugal/usermanagement.git']]])
            }
        }
        
        stage('Build') {
            steps {
                script {
                    // Ensure that the Maven wrapper script is executable
                    sh 'chmod +x mvnw'
                    
                    // Build the project using Maven, skipping tests
                    sh './mvnw clean install -DskipTests'
                }
            }
        }
        
        stage('Deployment') {
            steps {
                // Add your deployment steps here
                // For example, you can deploy the application to a web server, container, or cloud platform
            }
        }
    }
    
    post {
        failure {
            // Actions to perform in case of pipeline failure
            echo 'Build or deployment failed!'
        }
    }
}
