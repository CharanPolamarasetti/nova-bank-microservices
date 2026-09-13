pipeline {
    agent any

    tools {
        maven 'novabankmaven' 
    }
    
    environment {
        ECR_REPO = '285065163560.dkr.ecr.us-east-1.amazonaws.com/novabank/account-service'
    }

    stages {
        stage('cleanWS') {
            steps {
                cleanWs()
            }
        }
        
        stage('checkout') {
            steps {
                git branch: 'account-service', url: 'https://github.com/CharanPolamarasetti/nova-bank-microservices.git'
            }
        }
        
        stage('build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('CQA') {
            steps {
                withSonarQubeEnv('novabanksonarqube') {
                    sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=novabank-account-service'
                }
            }
        }
        
        stage('Qualitygates') {
            steps {
                waitForQualityGate abortPipeline: true, credentialsId: 'novabankaccountservicesonartoken'
            }
        }
        
        stage('Artifacts & Docker build') {
            parallel {
                stage('Nexus Artifacts') {
                    steps {
                        nexusArtifactUploader artifacts: [[artifactId: 'account-service', classifier: '', file: 'target/account-service-1.0.0.jar', type: 'jar']], credentialsId: 'novabankaccountserviceNexustoken', groupId: 'com.novabank', nexusUrl: '44.201.193.7:8081/', nexusVersion: 'nexus3', protocol: 'http', repository: 'novabank-accountservice-repo', version: '1.0.0'
                    }
                }
                stage('Docker') {
                    steps {
                        sh 'docker build --tag $ECR_REPO:$BUILD_NUMBER .'
                    }
                }
            }
        }
        
        stage('ImageScan') {
            steps {
                sh 'trivy image --severity HIGH,CRITICAL --exit-code 0 -f table -o account-service-trivy.txt $ECR_REPO:$BUILD_NUMBER'
            }
            post {
                always {
                    archiveArtifacts artifacts: '*.txt', allowEmptyArchive: true
                }
            }
        }
        
        stage('registry-push') {
            steps {
                sh 'aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 285065163560.dkr.ecr.us-east-1.amazonaws.com'
                sh 'docker push $ECR_REPO:$BUILD_NUMBER'
            }
        }
    }
}
