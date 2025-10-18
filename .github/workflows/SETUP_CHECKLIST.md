# CI/CD Pipeline Setup Checklist

Use this checklist to ensure your CI/CD pipeline is properly configured.

## ✅ Pre-requisites

- [ ] Repository is hosted on GitHub
- [ ] You have admin access to the repository
- [ ] Application builds successfully locally
- [ ] Tests run successfully locally (with database)

## ✅ GitHub Repository Configuration

### 1. Create Environments

Navigate to: **Settings** → **Environments** → **New environment**

- [ ] Create `dev` environment
- [ ] Create `staging` environment  
- [ ] Create `production` environment

### 2. Configure Production Environment Protection

For the `production` environment, configure:

- [ ] Enable "Required reviewers"
  - Add team members who must approve production deployments
  - Recommended: At least 2 reviewers
- [ ] Enable "Wait timer" (optional)
  - Set delay before deployment (e.g., 5 minutes)
- [ ] Enable "Deployment branches"
  - Restrict to `main` branch and release tags

### 3. Add Repository Secrets

Navigate to: **Settings** → **Secrets and Variables** → **Actions** → **New repository secret**

#### Required for All Environments:
- [ ] `DB_PASSWORD` - Database password

#### Choose Based on Your Infrastructure:

**For SSH-based deployment (Tomcat, custom servers):**
- [ ] `SERVER_SSH_KEY` - Private SSH key
- [ ] `SERVER_USER` - SSH username
- [ ] `DEV_SERVER_HOST` - Dev server hostname
- [ ] `STAGING_SERVER_HOST` - Staging server hostname
- [ ] `PROD_SERVER_HOST` - Production server hostname

**For AWS deployment:**
- [ ] `AWS_ACCESS_KEY_ID` - AWS access key
- [ ] `AWS_SECRET_ACCESS_KEY` - AWS secret key
- [ ] `AWS_REGION` - AWS region (e.g., us-east-1)
- [ ] `S3_BUCKET` - S3 bucket name
- [ ] `EB_APP_NAME` - Elastic Beanstalk application name
- [ ] `EB_ENV_NAME` - Elastic Beanstalk environment name

**For Azure deployment:**
- [ ] `AZURE_CREDENTIALS` - Service principal credentials (JSON)
- [ ] `AZURE_RESOURCE_GROUP` - Resource group name
- [ ] `AZURE_APP_NAME` - App Service name

**For Docker/Container deployment:**
- [ ] `DOCKER_USERNAME` - Docker Hub username
- [ ] `DOCKER_PASSWORD` - Docker Hub password
- [ ] `DOCKER_REGISTRY` - Docker registry URL (if not Docker Hub)

**For Heroku deployment:**
- [ ] `HEROKU_API_KEY` - Heroku API key
- [ ] `HEROKU_APP_NAME` - Heroku app name
- [ ] `HEROKU_EMAIL` - Heroku account email

### 4. Configure Environment Variables

Navigate to: **Settings** → **Secrets and Variables** → **Actions** → **Variables**

For each environment (dev, staging, production):

- [ ] `APP_URL` - Application URL (e.g., https://dev-app.example.com)
- [ ] `DB_HOST` - Database host
- [ ] `DB_NAME` - Database name
- [ ] `DB_USERNAME` - Database username (if different per environment)

## ✅ Customize Deployment Steps

### 1. Update Workflow Files

Based on your infrastructure, update the deployment steps in:

- [ ] `.github/workflows/deploy-dev.yml`
- [ ] `.github/workflows/deploy-staging.yml`
- [ ] `.github/workflows/deploy-production.yml`

See [DEPLOYMENT_EXAMPLES.md](DEPLOYMENT_EXAMPLES.md) for examples.

### 2. Configure Application URLs

Update the environment URLs in each workflow file:

```yaml
environment:
  name: dev
  url: https://your-dev-url.example.com  # Update this
```

### 3. Add Health Check Endpoints

Ensure your application has health check endpoints:

- [ ] Add Spring Boot Actuator dependency (if not present)
- [ ] Configure `/actuator/health` endpoint
- [ ] Update health check steps in workflows to use correct URL

## ✅ Database Configuration

### 1. Environment-Specific Configuration

Create or update application properties for each environment:

- [ ] `src/main/resources/application-dev.properties`
- [ ] `src/main/resources/application-staging.properties`
- [ ] `src/main/resources/application-prod.properties`

### 2. Database Migration (Optional)

If using database migrations:

- [ ] Add Flyway or Liquibase dependency
- [ ] Create migration scripts
- [ ] Add migration step to deployment workflows

## ✅ Testing the Pipeline

### 1. Test CI Workflow

- [ ] Create a test branch
- [ ] Make a small code change
- [ ] Create a pull request to `develop` or `main`
- [ ] Verify CI workflow runs successfully
- [ ] Check that tests pass
- [ ] Verify artifact is uploaded

### 2. Test Dev Deployment

- [ ] Merge a change to `develop` branch
- [ ] Verify deploy-dev workflow runs automatically
- [ ] Check deployment logs for errors
- [ ] Access dev environment URL
- [ ] Verify application is running correctly

### 3. Test Staging Deployment

- [ ] Create a release branch (e.g., `release/v1.0.0`)
- [ ] Push to the release branch
- [ ] Verify deploy-staging workflow runs
- [ ] Check deployment logs
- [ ] Access staging environment
- [ ] Run UAT tests

### 4. Test Production Deployment

- [ ] Create a GitHub release with a tag (e.g., `v1.0.0`)
- [ ] If reviewers are required, approve the deployment
- [ ] Verify deploy-production workflow runs
- [ ] Check deployment logs
- [ ] Access production environment
- [ ] Run smoke tests
- [ ] Verify backup was created

## ✅ Documentation

- [ ] Update README.md with:
  - [ ] Environment URLs
  - [ ] Deployment process
  - [ ] Rollback procedure
- [ ] Document secrets required for deployment
- [ ] Add workflow status badges to README
- [ ] Document branching strategy
- [ ] Create deployment runbook

## ✅ Monitoring and Alerts

### 1. Application Monitoring

- [ ] Set up application performance monitoring (APM)
- [ ] Configure error tracking (e.g., Sentry, Rollbar)
- [ ] Set up log aggregation (e.g., ELK, CloudWatch)
- [ ] Configure uptime monitoring

### 2. CI/CD Monitoring

- [ ] Enable email notifications for workflow failures
- [ ] Set up Slack notifications (optional)
- [ ] Configure GitHub notifications
- [ ] Review workflow runs regularly

## ✅ Security

- [ ] Review all secrets are stored securely
- [ ] Ensure no secrets are committed to code
- [ ] Configure branch protection rules
- [ ] Enable CodeQL security scanning (optional)
- [ ] Set up dependency security alerts
- [ ] Review OIDC authentication for cloud providers (recommended)

## ✅ Best Practices

- [ ] Document deployment schedule (e.g., prod deployments on Fridays)
- [ ] Create a deployment checklist
- [ ] Set up deployment windows
- [ ] Create rollback plan
- [ ] Document incident response process
- [ ] Regular pipeline maintenance and updates
- [ ] Review and optimize workflow execution time
- [ ] Keep dependencies up to date

## ✅ Training

- [ ] Train team on using the CI/CD pipeline
- [ ] Document deployment procedures
- [ ] Create video tutorials (optional)
- [ ] Schedule team walkthrough
- [ ] Document troubleshooting steps

## 🎉 Completion

Once all items are checked:

1. Run a full cycle test:
   - Feature branch → develop → dev deployment
   - Release branch → staging → staging deployment
   - GitHub release → production deployment

2. Celebrate! Your CI/CD pipeline is ready! 🎊

## Support

If you encounter issues during setup:

1. Check workflow logs in GitHub Actions
2. Verify all secrets are configured correctly
3. Test deployment steps manually first
4. Review [DEPLOYMENT_EXAMPLES.md](DEPLOYMENT_EXAMPLES.md)
5. Open an issue in the repository

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Spring Boot Deployment Guide](https://spring.io/guides/gs/spring-boot/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Maven Documentation](https://maven.apache.org/guides/)
