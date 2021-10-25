/* pipeline ???? ???? */
def DOCKER_IMAGE_NAME = "sktellecom/ms-test"   // ??????? Docker image ???
def DOCKER_IMAGE_TAGS = "board-api"  // ??????? Docker image ?±?
def NAMESPACE = "ms-test"
def VERSION = "${env.BUILD_NUMBER}"
def DATE = new Date();
  
podTemplate(label: 'builder',
            containers: [
                containerTemplate(name: 'gradle', image: 'gradle:7.1-jdk11', command: 'cat', ttyEnabled: true),
                containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
                containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.15.3', command: 'cat', ttyEnabled: true)
            ],
            volumes: [
                hostPathVolume(mountPath: '/home/gradle/.gradle', hostPath: '/home/admin/k8s/jenkins/.gradle'),
                hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
                //hostPathVolume(mountPath: '/usr/bin/docker', hostPath: '/usr/bin/docker')
            ]) {
    node('builder') {
        stage('Checkout') {
            checkout scm   // github?κ??? ??? ???
            sh "cd webflux/"
        }
        stage('Build') {
            container('gradle') {
            	/*
            		jvm memory ???? & gradle daemon ???? ????
            		gradle 3.0 ???? CI ??濡???? daemon?? ??????? ????? ??????? ????
            	*/
            	sh "echo -e '\norg.gradle.jvmargs=-Xmx1024m\norg.gradle.daemon=false' >> ~/.gradle/gradle.properties"
                /* 
                	??Ŀ ??????? ?????? gradle ???? ??????? ./build/libs?? jar???? ????
                	???? ???????? ????
                	build ???? ?Ŀ??? sonarqube?? ?????? ????????м??? ????
                */
                // sh "gradle -x test build sonarqube --no-daemon"
                sh "gradlew bootjar --no-daemon"
            }
        }
        stage('Docker build') {
            container('docker') {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub-id',
                    usernameVariable: 'USERNAME',
                    passwordVariable: 'PASSWORD')]) {
                        /* ./build/libs ?????? jar?????? ??Ŀ?????? ?????? ??Ŀ ???? ???? ?? docker hub?? ??????? ?????? */
                        sh "docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAGS} ."
                        sh "docker login -u ${USERNAME} -p ${PASSWORD}"
                        sh "docker push ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAGS}"
                }
            }
        }
        stage('Run kubectl') {
            container('kubectl') {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub-id',
                    usernameVariable: 'USERNAME',
                    passwordVariable: 'PASSWORD')]) {
                        /* namespace ???翩?? ???. ??????? namespace ???? */
                        sh "kubectl get ns ${NAMESPACE}|| kubectl create ns ${NAMESPACE}"

                        /* secret ???翩?? ???. ??????? secret ???? */
                        sh """
                            kubectl get secret ms-secret -n ${NAMESPACE} || \
                            kubectl create secret docker-registry ms-secret \
                            --docker-server=https://index.docker.io/v1/ \
                            --docker-username=${USERNAME} \
                            --docker-password=${PASSWORD} \
                            -n ${NAMESPACE}
                        """
                        /* k8s-deployment.yaml ?? env???? ?????????(DATE??). ?????? ?????? ?????? ?????? ????? ?????? ???? ???????? ??´?. */
                        /*sh "echo ${VERSION}"
                        sh "sed -i.bak 's#VERSION_STRING#${VERSION}#' ./k8s/k8s-deployment.yaml"*/
                        sh "echo ${DATE}"
                        sh "sed -i.bak 's#DATE_STRING#${DATE}#' ./k8s/k8s-deployment.yaml"

                        /* yaml????? ?????? ??????? */
                        sh "kubectl apply -f ./k8s/k8s-deployment.yaml -n ${NAMESPACE}"
                        sh "kubectl apply -f ./k8s/k8s-service.yaml -n ${NAMESPACE}"
                }
            }
        }
    }
}