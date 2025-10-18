# CI/CD Pipeline Documentation

This document describes the GitHub Actions CI/CD pipeline setup for the Predictive Health Risk Assessment application.

## Workflow Overview

The CI/CD pipeline consists of four main workflows:

### 1. CI - Build and Test (`ci.yml`)
**Triggers:**
- Pull requests to `main` or `develop` branches
- Pushes to `main` or `develop` branches

**Purpose:**
- Validates code changes through compilation and testing
- Ensures code quality before merging
- Creates build artifacts for deployment

**Steps:**
1. Checkout code
2. Set up Java 21
3. Build the application with Maven
4. Run tests with PostgreSQL database
5. Package the application as WAR file
6. Upload WAR artifact for deployment

### 2. Deploy to Dev (`deploy-dev.yml`)
**Triggers:**
- Pushes to `develop` branch
- Manual workflow dispatch

**Environment:** Development

**Purpose:**
- Automatically deploys code changes to the development environment
- Used for testing new features and bug fixes

**Steps:**
1. Checkout code
2. Set up Java 21
3. Build and test the application
4. Deploy WAR file to dev server
5. Notify deployment status

**Configuration:**
- Environment: `dev`
- URL: Configure in GitHub repository settings

### 3. Deploy to Staging (`deploy-staging.yml`)
**Triggers:**
- Pushes to `release/*` branches
- Manual workflow dispatch with version input

**Environment:** Staging

**Purpose:**
- Deploys release candidates to staging environment
- Performs integration tests before production
- Allows UAT (User Acceptance Testing)

**Steps:**
1. Checkout code
2. Set up Java 21
3. Build and test the application
4. Run integration tests
5. Deploy WAR file to staging server
6. Run smoke tests
7. Notify deployment status

**Configuration:**
- Environment: `staging`
- URL: Configure in GitHub repository settings

### 4. Deploy to Production (`deploy-production.yml`)
**Triggers:**
- Release published on GitHub
- Manual workflow dispatch with required version

**Environment:** Production

**Purpose:**
- Deploys stable releases to production environment
- Includes backup and rollback mechanisms
- Performs comprehensive smoke tests

**Steps:**
1. Checkout code at specific version/tag
2. Set up Java 21
3. Build and test the application
4. Run integration tests
5. Create backup of current production
6. Deploy WAR file to production server
7. Run smoke tests
8. Notify deployment status
9. Rollback on failure (if needed)

**Configuration:**
- Environment: `production`
- URL: Configure in GitHub repository settings
- Requires approval for deployment (configurable in GitHub)

### 5. Build and Test - Reusable (`build-and-test.yml`)
**Type:** Reusable workflow

**Purpose:**
- Provides common build and test logic
- Can be called by other workflows
- Reduces code duplication

**Inputs:**
- `java-version`: Java version (default: 21)
- `run-tests`: Whether to run tests (default: true)

**Outputs:**
- `artifact-name`: Name of the generated WAR file

## GitHub Environments

To use the deployment workflows, you need to configure the following environments in your GitHub repository:

### Setting Up Environments

1. Go to your repository on GitHub
2. Navigate to **Settings** → **Environments**
3. Create three environments:
   - `dev`
   - `staging`
   - `production`

### Environment Configuration

For each environment, configure:

#### Secrets (if needed):
- `DB_PASSWORD`: Database password
- `SERVER_SSH_KEY`: SSH key for deployment
- `AWS_ACCESS_KEY_ID`: AWS credentials (if using AWS)
- `AZURE_CREDENTIALS`: Azure credentials (if using Azure)

#### Variables:
- `APP_URL`: Application URL for the environment
- `DB_HOST`: Database host
- `SERVER_HOST`: Deployment server host

#### Protection Rules (recommended for production):
- Required reviewers: Add team members who must approve deployments
- Wait timer: Add delay before deployment starts
- Restrict deployment branches: Limit to `main` or release branches

## Branching Strategy

The pipeline follows a Git Flow branching strategy:

```
main (production)
  └── release/* (staging)
        └── develop (development)
              └── feature/* (feature branches)
```

### Workflow:
1. Developers create feature branches from `develop`
2. Feature branches are merged to `develop` via pull requests
3. CI workflow runs on pull requests to validate changes
4. Merging to `develop` triggers automatic deployment to dev
5. Release branches are created from `develop`
6. Pushing to `release/*` triggers deployment to staging
7. After testing in staging, create a GitHub release
8. Publishing a release triggers deployment to production

## Deployment Customization

The deployment steps in each workflow contain placeholder commands. You need to customize them based on your infrastructure:

### For Tomcat Deployment:
```bash
scp target/*.war user@server:/path/to/tomcat/webapps/
ssh user@server 'sudo systemctl restart tomcat'
```

### For AWS Elastic Beanstalk:
```bash
aws elasticbeanstalk create-application-version \
  --application-name my-app \
  --version-label $VERSION \
  --source-bundle S3Bucket=bucket,S3Key=path/to/app.war
aws elasticbeanstalk update-environment \
  --environment-name my-env \
  --version-label $VERSION
```

### For Azure App Service:
```bash
az webapp deployment source config-zip \
  --resource-group my-rg \
  --name my-app \
  --src target/*.war
```

### For Container Deployment:
```bash
docker build -t my-app:$VERSION .
docker push my-registry/my-app:$VERSION
kubectl set image deployment/my-app my-app=my-registry/my-app:$VERSION
```

## Database Configuration

The workflows use PostgreSQL service containers for testing. For deployment, ensure your application.properties is configured for each environment:

### Dev:
```properties
spring.datasource.url=jdbc:postgresql://dev-db-host:5432/predictive_health_db
```

### Staging:
```properties
spring.datasource.url=jdbc:postgresql://staging-db-host:5432/predictive_health_db
```

### Production:
```properties
spring.datasource.url=jdbc:postgresql://prod-db-host:5432/predictive_health_db
```

## Testing Strategy

### CI Workflow:
- Unit tests run on every pull request
- Integration tests with PostgreSQL
- Test results uploaded as artifacts

### Staging Workflow:
- All tests from CI
- Integration tests with verify goal
- Smoke tests after deployment

### Production Workflow:
- All tests from CI
- Integration tests
- Comprehensive smoke tests
- Health check validation

## Monitoring and Rollback

### Monitoring:
- Check deployment logs in GitHub Actions
- Monitor application logs on servers
- Set up application monitoring (APM, logs aggregation)

### Rollback:
The production workflow includes automatic rollback on failure. To manually rollback:

```bash
# Restore from backup
cp /backup/previous-version.war /path/to/tomcat/webapps/ROOT.war
sudo systemctl restart tomcat
```

Or use the workflow dispatch with a previous version number.

## Best Practices

1. **Never commit secrets**: Use GitHub Secrets for sensitive data
2. **Test before deploying**: Always test in dev and staging first
3. **Use environment protection**: Require approvals for production
4. **Tag releases**: Use semantic versioning (v1.0.0, v1.1.0, etc.)
5. **Monitor deployments**: Set up alerts for failed deployments
6. **Keep backups**: Maintain regular backups of production data
7. **Document changes**: Update changelog with each release
8. **Review logs**: Regularly check workflow logs for issues

## Troubleshooting

### Build Failures:
- Check Java version compatibility
- Verify Maven dependencies are accessible
- Review test logs for specific errors

### Deployment Failures:
- Verify server connectivity and credentials
- Check disk space on target servers
- Ensure database is accessible
- Review application logs

### Test Failures:
- Check PostgreSQL service container status
- Verify database connection settings
- Review individual test logs

## Manual Deployment

To manually trigger a deployment:

1. Go to **Actions** tab in GitHub
2. Select the deployment workflow (dev/staging/production)
3. Click **Run workflow**
4. Fill in required inputs (version for production)
5. Click **Run workflow** button

## Continuous Improvement

Consider adding:
- Code coverage reports
- Security scanning (CodeQL, Snyk)
- Performance testing
- Automated release notes generation
- Slack/email notifications
- Blue-green deployments
- Canary deployments
