pipeline {
    agent any

    environment {
        // Jenkins 里配置的 Docker Hub 凭证 ID
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub')
        // Docker Hub 仓库名（替换成你的）
        DOCKER_IMAGE = 'linwenqi/teedy-app'
        // 用 Jenkins 构建号作为 Docker Tag
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -Dmaven.test.failure.ignore=true'
            }
        }

        stage('PMD') {
            steps {
                sh 'mvn pmd:pmd'
            }
        }

        stage('JaCoCo') {
            steps {
                sh 'mvn jacoco:report'
            }
        }

        stage('Javadoc') {
            steps {
                sh 'mvn javadoc:javadoc -DfailOnError=false -DadditionalJOption=-Xdoclint:none'
            }
        }

        stage('Site') {
            steps {
                sh 'mvn site:site -DskipTests'
                sh 'mvn site:deploy'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        // ---------------- Docker 相关阶段 ----------------

        stage('Build Docker Image') {
            steps {
                script {
                    // 假设 Dockerfile 在项目根目录
                    docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    // 登录 Docker Hub 并推送镜像
                    docker.withRegistry('https://registry.hub.docker.com', 'DOCKER_HUB_CREDENTIALS') {
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push()
                        // 可选：打 latest 标签并推送
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push('latest')
                    }
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    // 停掉并删除已存在容器
                    sh 'docker stop teedy-container-8081 || true'
                    sh 'docker rm teedy-container-8081 || true'
                    // 启动新容器
                    sh "docker run --name teedy-container-8081 -d -p 8081:8080 ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
                    // 查看容器状态
                    sh 'docker ps --filter "name=teedy-container"'
                }
            }
        }
    }

    post {
        always {
            // 保存构建产物
            archiveArtifacts artifacts: '**/target/deploy-site/**/*.*', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.jar', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.war', fingerprint: true
            // 测试报告
            junit '**/target/surefire-reports/*.xml'
        }
    }
}