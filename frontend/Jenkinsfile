pipeline {
    agent any

    tools {
        // Use SonarScanner CLI for non-Java projects (HTML/JS/CSS)
        // Ensure 'sonar-scanner' is configured in Jenkins Global Tool Configuration
        sonarScanner 'sonar-scanner' 
    }
    
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
                // For static HTML/JS/CSS, we simply package the source folder.
                // If using a Node framework, replace with: sh 'npm install && npm run build'
                echo 'Packaging frontend assets...'
                sh 'tar -czvf frontend-app.tgz frontend/src/' 
            }
        }
        
        stage ('CQA') {
            steps {
                withSonarQubeEnv ('sonar') {
                    // Run SonarScanner for frontend files instead of Maven
                    sh 'sonar-scanner -Dsonar.projectKey=devproject-frontend -Dsonar.sources=frontend/src'
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
                            // Upload the packaged tarball to Nexus
                            nexusArtifactUploader artifacts: [
                                [artifactId: 'novabank-frontend', classifier: '', file: 'frontend-app.tgz', type: 'tgz']
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
                    stage ('Docker') {
                        steps {
                            // Assumes a Dockerfile using a web server like NGINX (e.g., FROM nginx:alpine COPY nova-bank/frontend/src /usr/share/nginx/html)
                            sh 'docker build --tag $ECR_REPO:$BUILD_NUMBER .'
                        }
                    }
                }
            }
        }
        
        stage ('ImageScan') {
            steps {
                // Pipeline fails here if vulnerabilities are found
                sh 'trivy image --severity HIGH,CRITICAL --exit-code 1 -f table -o service-1.txt $ECR_REPO:$BUILD_NUMBER'
            }
            post {
                always {
                    // Archiving runs no matter what the sh step returns
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
