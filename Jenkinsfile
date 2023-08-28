pipeline {
    agent any
    
    stages {
        stage('Checkout SCM') {
            steps {
                // Checkout code from Git
                checkout scm
            }
        }
        
        stage('Build and Deploy') {
            steps {
                // Compile and package the Spring Boot application
                sh './mvnw clean install -DskipTests'
                
                // Deploy your Spring Boot application
                sh 'nohup ./mvnw spring-boot:run -Dserver.port=8001 &'
            }
        }
    }
}
