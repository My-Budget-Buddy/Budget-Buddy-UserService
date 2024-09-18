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

  environment {
    MAJOR_VERSION = '0'
    MINOR_VERSION = '0'
    PATCH_VERSION = "${env.BUILD_NUMBER}"
  }

  stages {
    stage('Testing EKS') {
      when {
        branch 'testing-cohort'
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
                    kubectl apply -f postgres-secret.yaml
                    kubectl apply -f postgres-service.yaml
                    kubectl apply -f postgres-deployment.yaml
                    kubectl describe pods
                  '''
                }
            }
          }
        }
    }

    stage('Prepare Version') {
      steps {
        script {
          def newPatchVersion = PATCH_VERSION.toInteger() + 1
          env.VERSION = "${MAJOR_VERSION}.${MINOR_VERSION}.${newPatchVersion}"
          echo "Updated version to: ${env.VERSION}"
        }
      }
    }

    stage('Build for development') {
      when {
        branch 'testing-cohort'
      }

      steps {
        container('maven') {
          sh 'mvn clean install -DskipTests=true -Dspring.profiles.active=build'
        }
      }
    }

    stage('Test and Analyze for development') {
      when {
        branch 'testing-cohort'
      }

      steps {
        container('maven') {
          sh 'mvn clean verify -Pcoverage -Dspring.profiles.active=test'
          withSonarQubeEnv('SonarCloud') {
            sh '''
              mvn sonar:sonar \
                  -Dsonar.projectKey=My-Budget-Buddy_Budget-Buddy-UserService \
                  -Dsonar.projectName=Budget-Buddy-UserService \
                  -Dsonar.java.binaries=target/classes \
                  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
              '''
          }
        }
      }
    }

    stage('Deploy for production') {
      when {
        branch 'testing-main'
      }
      steps {
        echo 'Deploying...'
      }
    }
  }
}
