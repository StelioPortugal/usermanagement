node {
    stage("Clone the project") {
        git branch: 'main', url: 'https://github.com/StelioPortugal/usermanagement.git'
    }

    stage("Compilation") {
        steps {
            script {
                sh '''
                #!/bin/bash
                ./mvnw clean install -DskipTests
                '''
            }
        }
    }

    stage("Tests and Deployment") {
        stage("Running unit tests") {
            steps {
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
