pipeline {
    agent any

    tools {
        maven 'devmaven' 
    }
    
    environment {
        ECR_REPO = '285065163560.dkr.ecr.us-east-1.amazonaws.com/novabank/account-service'
    }

    stages {
        stage ('cleanWS') {
            steps {
                cleanWs()
            }
        }
        
        stage ('checkout') {
            steps {
                git branch: 'account-service', url: 'https://github.com/CharanPolamarasetti/nova-bank-microservices.git'
            }
        }
        
        stage ('build') {
            steps {
                dir('account-service/') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage ('CQA') {
            steps {
                dir('account-service/') {
                    withSonarQubeEnv ('sonar') {
                        sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=novabank-account-service'
                    }
                }
            }
        }
        
        stage ('Qualitygates') {
            steps {
                waitForQualityGate abortPipeline: true, credentialsId: 'sonar'
            }
        }
        
        stage ('Artifacts & Docker build') {
            steps {
                parallel {
                    stage ('Nexus Artifacts') {
                        steps {
                            dir('account-service/') {
                                nexusArtifactUploader artifacts: [
                                    [artifactId: 'account-service', classifier: '', file: 'target/account-service.jar', type: 'jar'] // Adjust the .jar filename if necessary
                                ], 
                                credentialsId: '<nexus-credentials>', 
                                groupId: 'com.novabank', 
                                nexusUrl: '<nexus-url>', 
                                nexusVersion: 'nexus3', 
                                protocol: 'http', 
                                repository: '<nexus-repo>', 
                                version: "${BUILD_NUMBER}"
                            }
                        }
                    }
                    stage ('Docker') {
                        steps {
                            dir('account-service/') {
                                sh 'docker build --tag $ECR_REPO:$BUILD_NUMBER .'
                            }
                        }
                    }
                }
            }
        }
        
        stage ('ImageScan') {
            steps {
                sh 'trivy image --severity HIGH,CRITICAL --exit-code 1 -f table -o account-service-trivy.txt $ECR_REPO:$BUILD_NUMBER'
            }
            post {
                always {
                    archiveArtifacts artifacts: '*.txt', allowEmptyArchive: true
                }
            }
        }
        
        stage ('registry-push') {
            steps {
                sh '<jenkins-agent-IAM-evaluation>'
                sh 'docker push $ECR_REPO:$BUILD_NUMBER'
            }
        }
    }
}
