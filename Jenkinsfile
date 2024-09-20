pipeline {
  agent {
    kubernetes {
      yaml '''
            apiVersion: v1
            kind: Pod
            spec:
              containers:
              - name: maven
                image: maven:latest
                command:
                - "sleep"
                args:
                - "9999999"
              - name: aws-kubectl
                image: heyvaldemar/aws-kubectl:latest
                env:
                - name: AWS_REGION
                  valueFrom:
                    secretKeyRef:
                      name: ecr-login
                      key: AWS_REGION
                - name: AWS_ACCESS_KEY_ID
                  valueFrom:
                    secretKeyRef:
                      name: ecr-login
                      key: AWS_ACCESS_KEY_ID
                - name: AWS_SECRET_ACCESS_KEY
                  valueFrom:
                    secretKeyRef:
                      name: ecr-login
                      key: AWS_SECRET_ACCESS_KEY
                command:
                - "sleep"
                args:
                - "9999999"
              - name: kaniko
                image: 924809052459.dkr.ecr.us-east-1.amazonaws.com/kaniko:latest
                imagePullPolicy: Always
                volumeMounts:
                - name: kaniko-cache
                  mountPath: /kaniko/.cache
                env:
                - name: AWS_REGION
                  valueFrom:
                    secretKeyRef:
                      name: ecr-login
                      key: AWS_REGION
                - name: AWS_ACCESS_KEY_ID
                  valueFrom:
                    secretKeyRef:
                      name: ecr-login
                      key: AWS_ACCESS_KEY_ID
                - name: AWS_SECRET_ACCESS_KEY
                  valueFrom:
                    secretKeyRef:
                      name: ecr-login
                      key: AWS_SECRET_ACCESS_KEY
                command:
                - sleep
                args:
                - '9999999'
                tty: true
              volumes:
              - name: kaniko-cache
                emptyDir: {}
        '''
    }
  }

  stages {
    
    stage('Build for Staging') {
        when {
            branch 'testing-cohort'
        }

        steps {
            container('maven') {
                sh 'mvn clean install -DskipTests=true -Dspring.profiles.active=build'
            }
        }
    }
    
    stage('Set Up EKS Test Database') {
        when {
            branch 'testing-cohort'
        }
        
        steps {
            sh 'git clone https://github.com/My-Budget-Buddy/Budget-Buddy-Kubernetes.git'
            container('aws-kubectl') {
                withCredentials([
                      string(credentialsId: 'STAGING_DATABASE_USER', variable: 'DATABASE_USERNAME'),
                      string(credentialsId: 'STAGING_DATABASE_PASSWORD', variable: 'DATABASE_PASSWORD')])
                {
                sh '''
                aws eks --region us-east-1 update-kubeconfig --name project3-eks
                
                # deploy test db

                cd Budget-Buddy-Kubernetes/Databases
                chmod +x ./deploy-database.sh
                ./deploy-database.sh user-test user $DATABASE_USERNAME $DATABASE_PASSWORD
                
                '''
                }
            }
        }
    }
    
    stage('Test and Analyze for Staging') {
        when {
            branch 'testing-cohort'
        }

        steps {
            container('maven') {
                withCredentials([
                  string(credentialsId: 'STAGING_DATABASE_USER', variable: 'DATABASE_USERNAME'),
                  string(credentialsId: 'STAGING_DATABASE_PASSWORD', variable: 'DATABASE_PASSWORD')])
                {
                    sh '''
                        export DATABASE_URL=jdbc:postgresql://user-postgres.user-test.svc.cluster.local:5432/my_budget_buddy
                        mvn clean verify -Pcoverage -Dspring.profiles.active=test \
                            -Dspring.datasource.url=$DATABASE_URL \
                            -Dspring.datasource.username=$DATABASE_USERNAME \
                            -Dspring.datasource.password=$DATABASE_PASSWORD
                    '''
                    withSonarQubeEnv('SonarCloud') {
                        sh '''
                            mvn sonar:sonar \
                                -Dsonar.projectKey=My-Budget-Buddy_Budget-Buddy-BudgetService \
                                -Dsonar.projectName=Budget-Buddy-BudgetService \
                                -Dsonar.java.binaries=target/classes \
                                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        '''
                    }
                }
            }
        }
    }
    
    stage('Build and Push Docker Image') {
      steps {
        container('kaniko') {
          script {
            def imageTag = 'latest'
            
            // Determine the image tag based on the branch
            // note that the var must nonetheless be exported in the operative shell command
            if (BRANCH_NAME == 'testing-cohort') {
              imageTag = 'test-latest'
            } else if (env.BRANCH_NAME == 'main') {
              imageTag = 'latest'
            }

            sh '''
              export IMAGE_TAG=''' + imageTag + '''
              echo "Deploying to namespace: $IMAGE_TAG"
              rm -rf /var/lock
              # Get the ECR login password
              export ECR_LOGIN=$(aws ecr get-login-password --region $AWS_REGION)
              if [ -z "$ECR_LOGIN" ]; then
                echo "Failed to get ECR login password"
                exit 1
              fi
              mkdir -p /kaniko/.docker
              echo "{\"auths\":{\"924809052459.dkr.ecr.us-east-1.amazonaws.com\":{\"auth\":\"$(echo -n AWS:$ECR_LOGIN | base64)\"}}}" > /kaniko/.docker/config.json
                echo ${imageTag}
              # Build and push the Docker image with the determined tag
              /kaniko/executor --dockerfile=Dockerfile.prod --context=dir://. --destination=924809052459.dkr.ecr.us-east-1.amazonaws.com/user-service:${IMAGE_TAG}
            '''
          }
        }
      }
    }
    
    // add functional, performance tests

  }
  
  post {
    always {
      cleanWs()
    }
  }
}

