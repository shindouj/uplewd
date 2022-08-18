pipeline {
    agent {
        label 'java-11'
    }

    environment {
        DOCKER_REGISTRY_HOST = 'docker.jeikobu.net'
        DOCKER_REGISTRY_USER = credentials('jenkins-docker-registry-user')
        DOCKER_REGISTRY_PASS = credentials('jenkins-docker-registry-pass')
        SONAR_TOKEN = credentials('SONAR_TOKEN')
    }

    stages {
        stage('Build & Test') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Build & Deploy Docker Image') {
            when {
                allOf {
                    environment name: 'CHANGE_ID', value: ''
                }
            }
            steps {
                sh './gradlew bootBuildImage'
            }
        }
    }
}