pipeline {

    agent any

    environment {
        COMPOSE_FILE = "docker-compose.yml"
        ENV_FILE = ".env"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }



        stage('Backend Tests') {

            parallel {

                stage('User Service') {
                    environment {
                        JWT_SECRET = 'test-secret-key-test-secret-key-test-secret-key-123456'
                        SPRING_DATA_MONGODB_URI = 'mongodb://localhost/test'
                        SPRING_KAFKA_BOOTSTRAP_SERVERS = 'localhost:9092'
                        SPRING_DATA_REDIS_HOST = 'localhost'
                    }
                    steps {
                        sh '''
                        cd user_service
                        chmod +x mvnw
                        ./mvnw test
                        '''
                    }
                }


                stage('Product Service') {
                    environment {
                        MEDIA_SERVICE_URL = 'http://media-service:8083'
                        SPRING_DATA_MONGODB_URI = 'mongodb://localhost/test'
                        SPRING_KAFKA_BOOTSTRAP_SERVERS = 'localhost:9092'
                    }
                    steps {
                        sh '''
                        cd product_service
                        chmod +x mvnw
                        ./mvnw test
                        '''
                    }
                }


                stage('Gateway Service') {
                    environment {
                        JWT_SECRET = 'test-secret-key-test-secret-key-test-secret-key-123456'
                        SSL_KEYSTORE_PASSWORD = 'changeit'

                        USER_SERVICE_URL = 'http://localhost:8081'
                        PRODUCT_SERVICE_URL = 'http://localhost:8082'
                        MEDIA_SERVICE_URL = 'http://localhost:8083'
                    }
                    steps {
                        sh '''
                        cd gateway_service
                        chmod +x mvnw
                        ./mvnw test
                        '''
                    }
                }


                stage('Media Service') {
                    steps {
                        sh '''
                        cd media_service
                        ./mvnw test
                        '''
                    }
                }

            }
        }


        stage('Frontend Test') {
    agent {
        docker {
            image 'node:20-bookworm'
            reuseNode true
        }
    }

    steps {
        sh '''
        apt-get update
        apt-get install -y chromium

        export CHROME_BIN=/usr/bin/chromium

        cd client

        npm ci

        CI=true npx ng test \
          --watch=false \
          --browsers=ChromeHeadlessCI
        '''
    }
}

        
        stage('Docker Build') {
    steps {
        sh '''
        cat > .env <<EOF
        
    JWT_SECRET = credentials('jwt-secret')
    SSL_KEYSTORE_PASSWORD = credentials('ssl-keystore-password')
    KAFKA_CLUSTER_ID = credentials('kafka-cluster-id')

    USER_DB_URI = credentials('user-db-uri')
    PRODUCT_DB_URI = credentials('product-db-uri')

    REDIS_HOST = credentials('redis-host')
    KAFKA_HOST = credentials('kafka-host')

    MEDIA_SERVICE_URL = credentials('media-service-url')
    USER_SERVICE_URL = credentials('user-service-url')
    PRODUCT_SERVICE_URL = credentials('product-service-url')

EOF

        docker compose --env-file .env build
        '''
    }
}
        
        stage('Backup Before Deploy') {
    steps {
        sh '''
        echo "📦 Creating deployment backup..."

        TIMESTAMP=$(date +%Y%m%d_%H%M%S)

        mkdir -p backups/$TIMESTAMP

        echo $TIMESTAMP > backups/latest

        # Backup env
        cp .env backups/$TIMESTAMP/

        # Backup running docker images
        docker compose ps -q | while read container
        do
            IMAGE=$(docker inspect --format='{{.Config.Image}}' $container)
            docker save $IMAGE -o backups/$TIMESTAMP/$(echo $IMAGE | tr "/:" "_").tar
        done


        # MongoDB backup
        docker exec mongodb mongodump \
            --archive=/tmp/mongo_backup.archive \
            --gzip

        docker cp mongodb:/tmp/mongo_backup.archive \
            backups/$TIMESTAMP/


        echo "✅ Backup stored in backups/$TIMESTAMP"
        '''
    }
}

        stage('Deploy') {
    steps {
        script {

            try {

                sh '''
                echo "🚀 Deploying new version..."

                docker compose --env-file .env down

                docker compose --env-file .env up -d

                sleep 30

                docker compose ps
                '''

            } catch (err) {

                echo "❌ Deployment failed. Starting rollback..."

                sh '''
                TIMESTAMP=$(cat backups/latest)

                echo "Restoring backup $TIMESTAMP"

                docker compose down

                # Restore previous images
                for IMAGE in backups/$TIMESTAMP/*.tar
                do
                    docker load -i "$IMAGE"
                done

                # Start MongoDB so it can accept restore
                docker compose up -d mongodb

                until docker exec mongodb mongosh --eval "db.adminCommand('ping')" >/dev/null 2>&1
                do
                    echo "Waiting for MongoDB..."
                    sleep 5
                done

                # Restore database
                docker cp backups/$TIMESTAMP/mongo_backup.archive \
                    mongodb:/tmp/mongo_backup.archive

                docker exec mongodb mongorestore \
                    --archive=/tmp/mongo_backup.archive \
                    --gzip \
                    --drop

                # Restore environment
                cp backups/$TIMESTAMP/.env .env

                # Start previous version
                docker compose --env-file .env up -d

                echo "✅ Rollback completed"
                '''

                throw err
            }
        }
    }
}

    }


    post {

        success {
            echo "🚀 CI/CD SUCCESS"
             script {
                mail(
                    to: 'abdlekhaliklaidi@gmail.com',
                    subject: "✅ SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: "Build passed.\nLogs: ${env.BUILD_URL}"
                )
            }
        }


        failure {
            echo "❌ CI/CD FAILED"
             script {
                mail(
                    to: 'abdlekhaliklaidi@gmail.com',
                    subject: "❌ FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: "Build failed.\nLogs: ${env.BUILD_URL}"
                )
            }
        }

    }

}