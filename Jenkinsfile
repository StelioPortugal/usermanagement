pipeline {
    agent any
    
    stages {
        stage("Checkout SCM") {
            steps {
                checkout scm
            }
        }
        
        stage("Compilation") {
            steps {
                script {
                    // Change to the project directory
                    dir('/var/lib/jenkins/workspace/usermanagement') {
                        // Compile the project using Maven
                        sh '''
                        #!/bin/bash
                        ./mvnw clean install -DskipTests
                        '''
                    }
                }
            }
        }
        
        stage("Tests and Deployment") {
            steps {
                stage("Running unit tests") {
                    steps {
                        script {
                            // Change to the project directory
                            dir('/var/lib/jenkins/workspace/usermanagement') {
                                // Run unit tests using Maven
                                sh './mvnw test -Punit'
                            }
                        }
                    }
                }
                
                stage("Deployment") {
                    steps {
                        script {
                            // Change to the project directory
                            dir('/var/lib/jenkins/workspace/usermanagement') {
                                // Start the Spring Boot application
                                sh 'nohup ./mvnw spring-boot:run -Dserver.port=8001 &'
                            }
                        }
                    }
                }
            }
        }
    }
}
