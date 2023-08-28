pipeline {
    agent any

    stages {
        stage("Clone the project") {
            steps {
                // Clone the Git repository
                git branch: 'main', url: 'https://github.com/StelioPortugal/usermanagement.git'
            }
        }

        stage("Compilation") {
            steps {
                // Compile the project using Maven
                script {
                    sh '''
                    #!/bin/bash
                    ./mvnw clean install -DskipTests
                    '''
                }
            }
        }

        stage("Tests and Deployment") {
            stages {
                stage("Running unit tests") {
                    steps {
                        // Run unit tests using Maven
                        script {
                            sh '''
                            #!/bin/bash
                            ./mvnw test -Punit
                            '''
                        }
                    }
                }

                stage("Deployment") {
                    steps {
                        // Deploy the Spring Boot application
                        script {
                            sh '''
                            #!/bin/bash
                            nohup ./mvnw spring-boot:run -Dserver.port=8001 &
                            '''
                        }
                    }
                }
            }
        }
    }
}
