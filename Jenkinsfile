pipeline {
    agent any
    
    environment {
        ECR_REPO = '285065163560.dkr.ecr.us-east-1.amazonaws.com/novabank/frontend'
    }

    stages {
        stage ('cleanWS') {
            steps {
                cleanWs()
            }
        }
        
        stage ('checkout') {
            steps {
                git branch: 'frontend', url: 'https://github.com/CharanPolamarasetti/nova-bank-microservices.git'
            }
        }
        
        stage ('build') {
            steps {
                sh 'tar -czvf frontend-app.tgz src/' 
            }
        }
        
        stage ('CQA') {
            steps {
                script {
                    def scannerHome = tool 'novabankfrontendsonar'
                    withSonarQubeEnv ('novabanksonarqube') {
                        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=novabank-frontend -Dsonar.sources=src/"
                    }
                }
            }
        }
        
        stage ('QualityGates') {
            steps {
                waitForQualityGate abortPipeline: true, credentialsId: 'novabankfrontendsonartoken'
            }
        }
        
        stage ('Docker build') {
            steps {
                sh 'docker build --tag $ECR_REPO:$BUILD_NUMBER .'
            }
        }
        
        stage ('ImageScan') {
            steps {
                sh 'trivy image --severity HIGH,CRITICAL --exit-code 0 -f table -o service-1.txt $ECR_REPO:$BUILD_NUMBER'
            }
            post {
                always {
                    archiveArtifacts artifacts: '*.txt', allowEmptyArchive: true
                }
            }
        }
        
        stage ('Registry push') {
            steps {
                sh 'aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 285065163560.dkr.ecr.us-east-1.amazonaws.com'
                sh 'docker push $ECR_REPO:$BUILD_NUMBER'
            }
        }
    }
}
