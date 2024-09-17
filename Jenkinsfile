pipeline {
  agent {
    kubernetes {
      yaml '''
            apiVersion: v1
            kind: Pod
            spec:
              containers:
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
              - name: maven
                image: maven
              volumes:
              - name: kaniko-cache
                emptyDir: {}
        '''
    }
  }

    stages {
        stage('Deliver for development') {
            when {
                branch 'test-cohort'
            }
            steps {
                container('kaniko') {
                sh './jenkins/scripts/deliver-for-development.sh'
                input message: 'Finished using the web site? (Click "Proceed" to continue)'
                sh './jenkins/scripts/kill.sh'
                }
            }
        }
        stage('Deploy for production') {
            when {
                branch 'main'
            }
            steps {
                container('kaniko') {
                sh './jenkins/scripts/deploy-for-production.sh'
                input message: 'Finished using the web site? (Click "Proceed" to continue)'
                sh './jenkins/scripts/kill.sh'
                }
            }
        }
        stage('Test') {
            steps {
                echo 'Testing...'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying...'
            }
        }
    }
}
