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
            branch 'daniel413x/pipeline'
        }

        steps {
            container('maven') {
                sh 'mvn clean install -DskipTests=true -Dspring.profiles.active=build'
            }
        }
    }

    stage('Deploy Postgres') {
        when {
            branch 'daniel413x/pipeline'
        }
        steps {
            container('kaniko') {
                script {
                    sh 'aws eks --region us-east-1 update-kubeconfig --name project3-eks'
                    sh 'kubectl config current-context'
                    withCredentials([
                      string(credentialsId: 'STAGING_DATABASE_USER', variable: 'postgres-user'),
                      string(credentialsId: 'STAGING_DATABASE_PASSWORD', variable: 'postgres-password')])
                    {
                    sh '''
                        cd kubernetes
                        sed -i 's/<postgres-user>/$postgres_user/' postgres-secret.yaml
                        sed -i 's/<postgres-password>/$postgres_password/' postgres-secret.yaml
                        kubectl apply -f initdb-configmap.yaml
                        kubectl apply -f postgres-secret.yaml
                        kubectl apply -f postgres-service.yaml
                        kubectl apply -f postgres-deployment.yaml
                        sleep 5
                        kubectl describe pods
                    '''
                    }
                }
            }
        }
    }
    
    stage('Test and Analyze for Staging') {
        when {
            branch 'daniel413x/pipeline'
        }

        steps {
            container('maven') {
                withCredentials([
                  string(credentialsId: 'STAGING_DATABASE_USER', variable: 'DATABASE_USERNAME'),
                  string(credentialsId: 'STAGING_DATABASE_PASSWORD', variable: 'DATABASE_PASSWORD')])
                {
                    sh '''
                        export DATABASE_URL=jdbc:postgresql://postgres.devops-tools.svc.cluster.local:5432/my_budget_buddy
                        mvn clean verify -Pcoverage -Dspring.profiles.active=test \
                            -Dspring.datasource.url=$DATABASE_URL \
                            -Dspring.datasource.username=<postgres-user> \
                            -Dspring.datasource.password=<postgres-password>
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
              sh '''
                rm -rf /var/lock
                # Get the ECR login password
                export ECR_LOGIN=$(aws ecr get-login-password --region $AWS_REGION)
                if [ -z "$ECR_LOGIN" ]; then
                  echo "Failed to get ECR login password"
                  exit 1
                fi
                mkdir -p /kaniko/.docker
                echo "{\"auths\":{\"924809052459.dkr.ecr.us-east-1.amazonaws.com\":{\"auth\":\"$(echo -n AWS:$ECR_LOGIN | base64)\"}}}" > /kaniko/.docker/config.json
                /kaniko/executor --dockerfile=Dockerfile.prod --context=dir://. --destination=924809052459.dkr.ecr.us-east-1.amazonaws.com/user-service:latest
              '''
          }
        }
      }
    }
  }
  
  post {
    always {
      cleanWs()
    }
  }
}

