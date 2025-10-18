# Deployment Customization Examples

This file contains examples of how to customize the deployment steps in the workflows for different infrastructure scenarios.

## Table of Contents
1. [Tomcat Server Deployment](#tomcat-server-deployment)
2. [AWS Elastic Beanstalk](#aws-elastic-beanstalk)
3. [Azure App Service](#azure-app-service)
4. [Docker Container](#docker-container)
5. [Kubernetes](#kubernetes)
6. [Heroku](#heroku)

---

## Tomcat Server Deployment

Replace the deployment step in workflows with:

```yaml
- name: Deploy to Tomcat
  run: |
    # Install SSH key
    mkdir -p ~/.ssh
    echo "${{ secrets.SERVER_SSH_KEY }}" > ~/.ssh/id_rsa
    chmod 600 ~/.ssh/id_rsa
    ssh-keyscan -H ${{ secrets.SERVER_HOST }} >> ~/.ssh/known_hosts
    
    # Copy WAR file to server
    scp target/*.war ${{ secrets.SERVER_USER }}@${{ secrets.SERVER_HOST }}:/opt/tomcat/webapps/ROOT.war
    
    # Restart Tomcat
    ssh ${{ secrets.SERVER_USER }}@${{ secrets.SERVER_HOST }} 'sudo systemctl restart tomcat'
    
    # Wait for Tomcat to start
    sleep 10
```

**Required Secrets:**
- `SERVER_SSH_KEY`: Private SSH key for server access
- `SERVER_HOST`: Server hostname or IP
- `SERVER_USER`: SSH username

---

## AWS Elastic Beanstalk

Replace the deployment step in workflows with:

```yaml
- name: Deploy to AWS Elastic Beanstalk
  run: |
    # Set AWS credentials
    export AWS_ACCESS_KEY_ID=${{ secrets.AWS_ACCESS_KEY_ID }}
    export AWS_SECRET_ACCESS_KEY=${{ secrets.AWS_SECRET_ACCESS_KEY }}
    export AWS_DEFAULT_REGION=${{ secrets.AWS_REGION }}
    
    # Install AWS CLI (if not present)
    pip install awscli
    
    # Upload to S3
    VERSION_LABEL="v$(date +%Y%m%d%H%M%S)"
    aws s3 cp target/*.war s3://${{ secrets.S3_BUCKET }}/deployments/$VERSION_LABEL.war
    
    # Create application version
    aws elasticbeanstalk create-application-version \
      --application-name ${{ secrets.EB_APP_NAME }} \
      --version-label $VERSION_LABEL \
      --source-bundle S3Bucket="${{ secrets.S3_BUCKET }}",S3Key="deployments/$VERSION_LABEL.war"
    
    # Deploy to environment
    aws elasticbeanstalk update-environment \
      --environment-name ${{ secrets.EB_ENV_NAME }} \
      --version-label $VERSION_LABEL
    
    # Wait for deployment
    aws elasticbeanstalk wait environment-updated \
      --environment-name ${{ secrets.EB_ENV_NAME }}
```

**Required Secrets:**
- `AWS_ACCESS_KEY_ID`: AWS access key
- `AWS_SECRET_ACCESS_KEY`: AWS secret key
- `AWS_REGION`: AWS region (e.g., us-east-1)
- `S3_BUCKET`: S3 bucket name
- `EB_APP_NAME`: Elastic Beanstalk application name
- `EB_ENV_NAME`: Elastic Beanstalk environment name

---

## Azure App Service

Replace the deployment step in workflows with:

```yaml
- name: Deploy to Azure App Service
  uses: azure/webapps-deploy@v2
  with:
    app-name: ${{ secrets.AZURE_APP_NAME }}
    publish-profile: ${{ secrets.AZURE_PUBLISH_PROFILE }}
    package: target/*.war

# OR using Azure CLI:

- name: Login to Azure
  uses: azure/login@v1
  with:
    creds: ${{ secrets.AZURE_CREDENTIALS }}

- name: Deploy to Azure App Service
  run: |
    az webapp deployment source config-zip \
      --resource-group ${{ secrets.AZURE_RESOURCE_GROUP }} \
      --name ${{ secrets.AZURE_APP_NAME }} \
      --src target/*.war
    
    # Wait for deployment
    sleep 20
```

**Required Secrets:**
- `AZURE_PUBLISH_PROFILE`: Download from Azure Portal
  OR
- `AZURE_CREDENTIALS`: Service principal credentials (JSON format)
- `AZURE_RESOURCE_GROUP`: Resource group name
- `AZURE_APP_NAME`: App Service name

---

## Docker Container

Replace the deployment step in workflows with:

```yaml
- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v3

- name: Login to Docker Hub
  uses: docker/login-action@v3
  with:
    username: ${{ secrets.DOCKER_USERNAME }}
    password: ${{ secrets.DOCKER_PASSWORD }}

- name: Build and push Docker image
  run: |
    VERSION="${{ github.event.release.tag_name || 'latest' }}"
    
    # Build Docker image
    docker build -t ${{ secrets.DOCKER_USERNAME }}/predictive-health-app:$VERSION .
    docker build -t ${{ secrets.DOCKER_USERNAME }}/predictive-health-app:latest .
    
    # Push to Docker Hub
    docker push ${{ secrets.DOCKER_USERNAME }}/predictive-health-app:$VERSION
    docker push ${{ secrets.DOCKER_USERNAME }}/predictive-health-app:latest

- name: Deploy container to server
  run: |
    # Install SSH key
    mkdir -p ~/.ssh
    echo "${{ secrets.SERVER_SSH_KEY }}" > ~/.ssh/id_rsa
    chmod 600 ~/.ssh/id_rsa
    
    # SSH to server and update container
    ssh ${{ secrets.SERVER_USER }}@${{ secrets.SERVER_HOST }} << 'EOF'
      docker pull ${{ secrets.DOCKER_USERNAME }}/predictive-health-app:latest
      docker stop predictive-health-app || true
      docker rm predictive-health-app || true
      docker run -d \
        --name predictive-health-app \
        -p 8080:8080 \
        -e DB_USERNAME=${{ secrets.DB_USERNAME }} \
        -e DB_PASSWORD=${{ secrets.DB_PASSWORD }} \
        -e SPRING_DATASOURCE_URL=${{ secrets.DB_URL }} \
        ${{ secrets.DOCKER_USERNAME }}/predictive-health-app:latest
    EOF
```

**Required Secrets:**
- `DOCKER_USERNAME`: Docker Hub username
- `DOCKER_PASSWORD`: Docker Hub password
- `SERVER_SSH_KEY`: SSH key for deployment server
- `SERVER_USER`: SSH username
- `SERVER_HOST`: Server hostname
- `DB_USERNAME`, `DB_PASSWORD`, `DB_URL`: Database credentials

**Sample Dockerfile:**
```dockerfile
FROM tomcat:10-jdk21
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
```

---

## Kubernetes

Replace the deployment step in workflows with:

```yaml
- name: Set up kubectl
  uses: azure/setup-kubectl@v3

- name: Configure kubectl
  run: |
    mkdir -p ~/.kube
    echo "${{ secrets.KUBE_CONFIG }}" | base64 -d > ~/.kube/config

- name: Build and push Docker image
  run: |
    VERSION="${{ github.event.release.tag_name || 'latest' }}"
    docker build -t ${{ secrets.DOCKER_REGISTRY }}/predictive-health-app:$VERSION .
    docker push ${{ secrets.DOCKER_REGISTRY }}/predictive-health-app:$VERSION

- name: Deploy to Kubernetes
  run: |
    VERSION="${{ github.event.release.tag_name || 'latest' }}"
    
    # Update deployment image
    kubectl set image deployment/predictive-health-app \
      predictive-health-app=${{ secrets.DOCKER_REGISTRY }}/predictive-health-app:$VERSION \
      -n production
    
    # Wait for rollout
    kubectl rollout status deployment/predictive-health-app -n production
    
    # OR apply manifest
    # kubectl apply -f k8s/deployment.yaml
```

**Required Secrets:**
- `KUBE_CONFIG`: Base64-encoded Kubernetes config file
- `DOCKER_REGISTRY`: Docker registry URL
- `DOCKER_USERNAME`: Registry username
- `DOCKER_PASSWORD`: Registry password

**Sample Kubernetes Deployment:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: predictive-health-app
  namespace: production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: predictive-health-app
  template:
    metadata:
      labels:
        app: predictive-health-app
    spec:
      containers:
      - name: predictive-health-app
        image: registry/predictive-health-app:latest
        ports:
        - containerPort: 8080
        env:
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
```

---

## Heroku

Replace the deployment step in workflows with:

```yaml
- name: Deploy to Heroku
  uses: akhileshns/heroku-deploy@v3.12.14
  with:
    heroku_api_key: ${{ secrets.HEROKU_API_KEY }}
    heroku_app_name: ${{ secrets.HEROKU_APP_NAME }}
    heroku_email: ${{ secrets.HEROKU_EMAIL }}

# OR using Heroku CLI:

- name: Deploy to Heroku
  run: |
    # Install Heroku CLI
    curl https://cli-assets.heroku.com/install.sh | sh
    
    # Login to Heroku
    echo "${{ secrets.HEROKU_API_KEY }}" | heroku auth:token
    
    # Add Heroku remote
    heroku git:remote -a ${{ secrets.HEROKU_APP_NAME }}
    
    # Deploy
    git push heroku main
```

**Required Secrets:**
- `HEROKU_API_KEY`: Heroku API key (from Account Settings)
- `HEROKU_APP_NAME`: Heroku app name
- `HEROKU_EMAIL`: Heroku account email

---

## General Tips

### 1. Health Check After Deployment
Add a health check step after deployment:

```yaml
- name: Health Check
  run: |
    for i in {1..10}; do
      STATUS=$(curl -s -o /dev/null -w "%{http_code}" ${{ steps.deploy.outputs.app-url }}/actuator/health)
      if [ $STATUS -eq 200 ]; then
        echo "✅ Application is healthy"
        exit 0
      fi
      echo "Waiting for application to be ready... (attempt $i/10)"
      sleep 10
    done
    echo "❌ Application health check failed"
    exit 1
```

### 2. Database Migration
Run database migrations before deployment:

```yaml
- name: Run Database Migrations
  run: |
    ./mvnw flyway:migrate -Dflyway.url=${{ secrets.DB_URL }} \
      -Dflyway.user=${{ secrets.DB_USERNAME }} \
      -Dflyway.password=${{ secrets.DB_PASSWORD }}
```

### 3. Backup Before Deployment
Create backup before production deployment:

```yaml
- name: Backup Current Deployment
  run: |
    BACKUP_NAME="backup-$(date +%Y%m%d%H%M%S)"
    ssh ${{ secrets.SERVER_USER }}@${{ secrets.SERVER_HOST }} \
      "cp /opt/tomcat/webapps/ROOT.war /opt/backups/$BACKUP_NAME.war"
```

### 4. Rollback Mechanism
Add automatic rollback on failure:

```yaml
- name: Rollback on Failure
  if: failure()
  run: |
    echo "Deployment failed, rolling back..."
    ssh ${{ secrets.SERVER_USER }}@${{ secrets.SERVER_HOST }} << 'EOF'
      LATEST_BACKUP=$(ls -t /opt/backups/*.war | head -1)
      cp $LATEST_BACKUP /opt/tomcat/webapps/ROOT.war
      sudo systemctl restart tomcat
    EOF
```

## Testing Deployments

Before deploying to production, test your deployment steps:

1. Use `workflow_dispatch` trigger for manual testing
2. Test in dev environment first
3. Verify all secrets are configured correctly
4. Check application logs after deployment
5. Run smoke tests to verify functionality

## Support

For issues or questions about deployment customization, please open an issue in the repository.
