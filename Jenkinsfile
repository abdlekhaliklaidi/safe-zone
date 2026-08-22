pipeline {

    agent any

    environment {
        COMPOSE_FILE = "docker-compose.yml"
        ENV_FILE = ".env"
    }

    stages {

        // =========================================================
        // CHECKOUT
        // =========================================================

        stage('Checkout') {
            steps {
                checkout scm
            }
        }


        // =========================================================
        // BACKEND TESTS
        // =========================================================

        stage('Backend Tests') {

            parallel {

                // -------------------------------------------------
                // USER SERVICE
                // -------------------------------------------------

                stage('User Service') {

                    environment {
                        JWT_SECRET = 'test-secret-key-test-secret-key-test-secret-key-123456'

                        // Docker network
                        SPRING_DATA_MONGODB_URI = 'mongodb://mongodb:27017/test'
                        SPRING_KAFKA_BOOTSTRAP_SERVERS = 'kafka:9092'
                        SPRING_DATA_REDIS_HOST = 'redis'
                    }

                    steps {
                        sh '''
                            cd user_service

                            chmod +x mvnw

                            echo "🧪 Testing User Service..."

                            ./mvnw test
                        '''
                    }
                }


                // -------------------------------------------------
                // PRODUCT SERVICE
                // -------------------------------------------------

                stage('Product Service') {

                    environment {
                        MEDIA_SERVICE_URL = 'http://media-service:8083'

                        // Docker network
                        SPRING_DATA_MONGODB_URI = 'mongodb://mongodb:27017/test'
                        SPRING_KAFKA_BOOTSTRAP_SERVERS = 'kafka:9092'
                    }

                    steps {
                        sh '''
                            cd product_service

                            chmod +x mvnw

                            echo "🧪 Testing Product Service..."

                            ./mvnw test
                        '''
                    }
                }


                // -------------------------------------------------
                // GATEWAY SERVICE
                // -------------------------------------------------

                stage('Gateway Service') {

                    environment {

                        JWT_SECRET = 'test-secret-key-test-secret-key-test-secret-key-123456'
                        SSL_KEYSTORE_PASSWORD = 'changeit'

                        // Docker network
                        USER_SERVICE_URL = 'http://user-service:8081'
                        PRODUCT_SERVICE_URL = 'http://product-service:8082'
                        MEDIA_SERVICE_URL = 'http://media-service:8083'
                    }

                    steps {
                        sh '''
                            cd gateway_service

                            chmod +x mvnw

                            echo "🧪 Testing Gateway Service..."

                            ./mvnw test
                        '''
                    }
                }


                // -------------------------------------------------
                // MEDIA SERVICE
                // -------------------------------------------------

                stage('Media Service') {

                    environment {

                        // Docker network
                        SPRING_DATA_MONGODB_URI = 'mongodb://mongodb:27017/test'
                        SPRING_KAFKA_BOOTSTRAP_SERVERS = 'kafka:9092'
                    }

                    steps {
                        sh '''
                            cd media_service

                            chmod +x mvnw

                            echo "🧪 Testing Media Service..."

                            ./mvnw test
                        '''
                    }
                }
            }
        }


        // =========================================================
        // FRONTEND TEST
        // =========================================================

        stage('Frontend Test') {

            agent {
                docker {
                    image 'node:20-bookworm'
                    reuseNode true
                }
            }

            steps {

                sh '''
                    set -e

                    echo "🧪 Installing Chromium..."

                    apt-get update
                    apt-get install -y chromium

                    export CHROME_BIN=/usr/bin/chromium

                    cd client

                    echo "📦 Installing npm dependencies..."

                    npm ci

                    echo "🧪 Running Angular tests..."

                    CI=true npx ng test \
                        --watch=false \
                        --code-coverage \
                        --browsers=ChromeHeadlessCI
                '''
            }
        }


        // =========================================================
        // SONARQUBE ANALYSIS
        // =========================================================

        stage('SonarQube Analysis') {

            steps {

                withSonarQubeEnv('SonarQube') {

                    withCredentials([
                        string(
                            credentialsId: 'sonar-token',
                            variable: 'SONAR_TOKEN'
                        )
                    ]) {

                        sh '''
                            set -e

                            echo "🔍 Starting SonarQube analysis..."


                            # -----------------------------------------
                            # USER SERVICE
                            # -----------------------------------------

                            cd user_service

                            ./mvnw sonar:sonar \
                                -Dsonar.projectKey=safe-zone-user \
                                -Dsonar.projectName="safe-zone-user" \
                                -Dsonar.host.url="$SONAR_HOST_URL" \
                                -Dsonar.token="$SONAR_TOKEN"


                            # -----------------------------------------
                            # PRODUCT SERVICE
                            # -----------------------------------------

                            cd ../product_service

                            ./mvnw sonar:sonar \
                                -Dsonar.projectKey=safe-zone-product \
                                -Dsonar.projectName="safe-zone-product" \
                                -Dsonar.host.url="$SONAR_HOST_URL" \
                                -Dsonar.token="$SONAR_TOKEN"


                            # -----------------------------------------
                            # GATEWAY SERVICE
                            # -----------------------------------------

                            cd ../gateway_service

                            ./mvnw sonar:sonar \
                                -Dsonar.projectKey=safe-zone-gateway \
                                -Dsonar.projectName="safe-zone-gateway" \
                                -Dsonar.host.url="$SONAR_HOST_URL" \
                                -Dsonar.token="$SONAR_TOKEN"


                            # -----------------------------------------
                            # MEDIA SERVICE
                            # -----------------------------------------

                            cd ../media_service

                            ./mvnw sonar:sonar \
                                -Dsonar.projectKey=safe-zone-media \
                                -Dsonar.projectName="safe-zone-media" \
                                -Dsonar.host.url="$SONAR_HOST_URL" \
                                -Dsonar.token="$SONAR_TOKEN"


                            echo "✅ SonarQube analysis completed."
                        '''
                    }
                }
            }
        }


        // =========================================================
        // QUALITY GATE
        // =========================================================

        stage('Quality Gate') {

            steps {

                timeout(time: 10, unit: 'MINUTES') {

                    waitForQualityGate abortPipeline: true
                }
            }
        }


        // =========================================================
        // DOCKER BUILD
        // =========================================================

        stage('Docker Build') {

            environment {

                JWT_SECRET = credentials('jwt-secret')

                SSL_KEYSTORE_PASSWORD =
                    credentials('ssl-keystore-password')

                KAFKA_CLUSTER_ID =
                    credentials('kafka-cluster-id')

                USER_DB_URI =
                    credentials('user-db-uri')

                PRODUCT_DB_URI =
                    credentials('product-db-uri')

                REDIS_HOST =
                    credentials('redis-host')

                KAFKA_HOST =
                    credentials('kafka-host')

                MEDIA_SERVICE_URL =
                    credentials('media-service-url')

                USER_SERVICE_URL =
                    credentials('user-service-url')

                PRODUCT_SERVICE_URL =
                    credentials('product-service-url')
            }

            steps {

                sh '''
                    set -e

                    echo "🐳 Preparing Docker environment..."

                    cat > .env <<EOF
JWT_SECRET=${JWT_SECRET}
SSL_KEYSTORE_PASSWORD=${SSL_KEYSTORE_PASSWORD}
KAFKA_CLUSTER_ID=${KAFKA_CLUSTER_ID}

USER_DB_URI=${USER_DB_URI}
PRODUCT_DB_URI=${PRODUCT_DB_URI}

REDIS_HOST=${REDIS_HOST}
KAFKA_HOST=${KAFKA_HOST}

MEDIA_SERVICE_URL=${MEDIA_SERVICE_URL}
USER_SERVICE_URL=${USER_SERVICE_URL}
PRODUCT_SERVICE_URL=${PRODUCT_SERVICE_URL}
EOF

                    echo "🐳 Building Docker images..."

                    docker compose --env-file .env build

                    echo "✅ Docker build completed."
                '''
            }
        }


        // =========================================================
        // BACKUP
        // =========================================================

        stage('Backup Before Deploy') {

            steps {

                sh '''
                    set -e

                    echo "📦 Creating deployment backup..."

                    TIMESTAMP=$(date +%Y%m%d_%H%M%S)

                    mkdir -p backups/$TIMESTAMP

                    echo "$TIMESTAMP" > backups/latest


                    # -----------------------------------------
                    # Backup .env
                    # -----------------------------------------

                    cp .env backups/$TIMESTAMP/


                    # -----------------------------------------
                    # Backup Docker images
                    # -----------------------------------------

                    docker compose ps -q | while read container
                    do

                        IMAGE=$(docker inspect \
                            --format='{{.Config.Image}}' \
                            "$container")

                        if [ -n "$IMAGE" ]; then

                            docker save "$IMAGE" \
                                -o "backups/$TIMESTAMP/$(echo "$IMAGE" | tr "/:" "_").tar"

                        fi

                    done


                    # -----------------------------------------
                    # MongoDB backup
                    # -----------------------------------------

                    echo "📦 Backing up MongoDB..."

                    docker exec mongodb mongodump \
                        --archive=/tmp/mongo_backup.archive \
                        --gzip


                    docker cp \
                        mongodb:/tmp/mongo_backup.archive \
                        backups/$TIMESTAMP/


                    echo "✅ Backup stored in backups/$TIMESTAMP"
                '''
            }
        }


        // =========================================================
        // DEPLOY
        // =========================================================

        stage('Deploy') {

            steps {

                script {

                    try {

                        sh '''
                            set -e

                            echo "🚀 Deploying new version..."

                            docker compose \
                                --env-file .env \
                                down

                            docker compose \
                                --env-file .env \
                                up -d

                            echo "⏳ Waiting for services..."

                            sleep 30

                            docker compose ps

                            echo "✅ Deployment completed."
                        '''

                    }

                    catch (err) {

                        echo "❌ Deployment failed. Starting rollback..."


                        sh '''
                            set -e

                            TIMESTAMP=$(cat backups/latest)

                            echo "🔄 Restoring backup: $TIMESTAMP"


                            # -----------------------------------------
                            # Stop current deployment
                            # -----------------------------------------

                            docker compose down


                            # -----------------------------------------
                            # Restore Docker images
                            # -----------------------------------------

                            for IMAGE in backups/$TIMESTAMP/*.tar
                            do

                                if [ -f "$IMAGE" ]; then

                                    docker load -i "$IMAGE"

                                fi

                            done


                            # -----------------------------------------
                            # Start MongoDB
                            # -----------------------------------------

                            docker compose up -d mongodb


                            echo "⏳ Waiting for MongoDB..."


                            until docker exec mongodb \
                                mongosh \
                                --eval "db.adminCommand('ping')" \
                                >/dev/null 2>&1

                            do

                                echo "Waiting for MongoDB..."

                                sleep 5

                            done


                            # -----------------------------------------
                            # Restore MongoDB
                            # -----------------------------------------

                            docker cp \
                                backups/$TIMESTAMP/mongo_backup.archive \
                                mongodb:/tmp/mongo_backup.archive


                            docker exec mongodb \
                                mongorestore \
                                --archive=/tmp/mongo_backup.archive \
                                --gzip \
                                --drop


                            # -----------------------------------------
                            # Restore .env
                            # -----------------------------------------

                            cp \
                                backups/$TIMESTAMP/.env \
                                .env


                            # -----------------------------------------
                            # Start previous version
                            # -----------------------------------------

                            docker compose \
                                --env-file .env \
                                up -d


                            echo "✅ Rollback completed."
                        '''


                        throw err
                    }
                }
            }
        }
    }


    // =============================================================
    // POST ACTIONS
    // =============================================================

    post {

        success {

            echo "🚀 CI/CD SUCCESS"

            script {

                mail(
                    to: 'abdlekhaliklaidi@gmail.com',
                    subject: "✅ SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """Build passed successfully.

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Logs: ${env.BUILD_URL}
"""
                )
            }
        }


        failure {

            echo "❌ CI/CD FAILED"

            script {

                mail(
                    to: 'abdlekhaliklaidi@gmail.com',
                    subject: "❌ FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """Build failed.

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Logs: ${env.BUILD_URL}
"""
                )
            }
        }
    }
}