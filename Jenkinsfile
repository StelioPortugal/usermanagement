pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                // Checkout the code from your Git repository
                script {
                    def scmVars = checkout scm
                }
            }
        }
        
        stage('Build and Deploy') {
            steps {
                // Build and deploy the application
                script {
                    // Set execute permissions on the mvnw script
                    sh 'chmod +x mvnw'
                    
                    // Build the application and skip tests
                    sh './mvnw clean install -DskipTests'
                    
                    // Start the application (adjust as needed)
                    sh 'nohup ./mvnw spring-boot:run -Dserver.port=8001 &'
                }
            }
        }
    }
    
    post {
        success {
            // Add any post-build actions or notifications here
            echo 'Build and deployment successful!'
        }
        failure {
            // Add actions for when the build or deployment fails
            echo 'Build or deployment failed!'
        }
    }
}
