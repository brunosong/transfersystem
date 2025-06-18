pipeline {
    agent {
        kubernetes {
            yaml '''
            apiVersion: v1
            kind: Pod
            metadata:
              name: maven-temp-pod
              namespace: jenkins
            spec:
              containers:
              - name: maven
                image: maven:3.9.9-eclipse-temurin-21
                command:
                - cat
                tty: true
                volumeMounts:
                - name: maven-cache
                  mountPath: /root/.m2/repository
              - name: jnlp
                image: jenkins/inbound-agent:latest
                args: ["\$(JENKINS_SECRET)", "\$(JENKINS_NAME)"]
              volumes:
              - name: maven-cache
                persistentVolumeClaim:
                  claimName: maven-cache-pvc
            '''
        }
    }
    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',url : 'https://BrunoSong@bitbucket.org/songbrunosong/transfersystem.git'
            }
        }
        stage('Build') {
            steps {
                container('maven') {
                    sh '''
                        set -e
                        echo "Starting build process..."
                        cat > /root/.m2/settings.xml <<EOF
                        <settings>
                          <servers>
                            <server>
                              <id>registry-1.docker.io</id>
                              <username>\${DOCKERHUB_CREDENTIALS_USR}</username>
                              <password>\${DOCKERHUB_CREDENTIALS_PSW}</password>
                            </server>
                          </servers>
                        </settings>
                        EOF
                        echo "Created settings.xml"
                        cat /root/.m2/settings.xml
                        if [ ! -d "config-service" ]; then
                            echo "Error: config-service directory not found"
                            ls -l
                            exit 1
                        fi
                        cd config-service
                        mvn compile jib:build -X
                    '''
                }
            }
        }
    }
}