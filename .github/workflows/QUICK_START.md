# Quick Start Guide for CI/CD Pipeline

This guide will help you get the CI/CD pipeline up and running in 15 minutes.

## Prerequisites

- [ ] Admin access to the GitHub repository
- [ ] Access to deployment infrastructure (servers, cloud accounts, etc.)

## 5-Minute Setup (Minimal)

This gets you started with the CI workflow only (build and test on pull requests).

### Step 1: No Configuration Needed!

The CI workflow (`.github/workflows/ci.yml`) will automatically run when:
- You create a pull request to `main` or `develop`
- You push to `main` or `develop`

### Step 2: Test It

```bash
# Create a test branch
git checkout -b test-ci-pipeline

# Make a small change
echo "# Test CI" >> TEST.md

# Commit and push
git add TEST.md
git commit -m "Test CI pipeline"
git push origin test-ci-pipeline

# Create a pull request on GitHub
# The CI workflow will run automatically!
```

## 15-Minute Setup (Full Pipeline)

This sets up dev, staging, and production deployments.

### Step 1: Create Environments (3 minutes)

1. Go to your repository on GitHub
2. Click **Settings** → **Environments**
3. Create three environments:
   - Click **New environment**, name it `dev`, click **Configure environment**
   - Click **New environment**, name it `staging`, click **Configure environment**
   - Click **New environment**, name it `production`, click **Configure environment**

### Step 2: Add Secrets (5 minutes)

1. Go to **Settings** → **Secrets and Variables** → **Actions**
2. Click **New repository secret**
3. Add at minimum:
   - Name: `DB_PASSWORD`, Value: `your-database-password`
   - Name: `SERVER_SSH_KEY`, Value: `your-ssh-private-key` (if using SSH deployment)

### Step 3: Customize Deployment (5 minutes)

1. Open `.github/workflows/deploy-dev.yml`
2. Find the "Deploy to Dev" step (around line 50)
3. Replace the placeholder commands with your actual deployment commands

Example for Tomcat:
```yaml
- name: Deploy to Dev
  id: deploy
  run: |
    echo "Deploying to development environment..."
    
    # Copy WAR to server
    scp target/*.war user@dev-server:/path/to/tomcat/webapps/
    
    # Restart Tomcat
    ssh user@dev-server 'sudo systemctl restart tomcat'
    
    echo "app-url=https://dev.example.com" >> $GITHUB_OUTPUT
```

4. Repeat for `deploy-staging.yml` and `deploy-production.yml`

### Step 4: Test Dev Deployment (2 minutes)

```bash
# Push to develop branch
git checkout develop
git merge your-feature-branch
git push origin develop

# The deploy-dev workflow will run automatically!
# Check: Actions tab → Deploy to Dev workflow
```

## What's Included?

Your CI/CD pipeline now includes:

✅ **CI Workflow** (`ci.yml`)
- Runs on pull requests and pushes
- Builds the application with Maven
- Runs tests with PostgreSQL
- Creates WAR artifact

✅ **Dev Deployment** (`deploy-dev.yml`)
- Automatically deploys from `develop` branch
- Runs tests before deployment
- Can be manually triggered

✅ **Staging Deployment** (`deploy-staging.yml`)
- Deploys from `release/*` branches
- Includes integration tests
- Runs smoke tests after deployment

✅ **Production Deployment** (`deploy-production.yml`)
- Deploys on GitHub releases
- Creates backups before deployment
- Includes rollback mechanism
- Can require manual approval

## Branching Strategy

```
feature/new-feature → develop → dev environment
                         ↓
                   release/v1.0.0 → staging environment
                         ↓
                    GitHub Release → production environment
```

### Daily Workflow:

1. **Feature Development**:
   ```bash
   git checkout -b feature/my-feature
   # Make changes
   git push origin feature/my-feature
   # Create PR to develop
   ```

2. **Deploy to Dev**:
   ```bash
   # After PR approval and merge
   # Automatic deployment to dev!
   ```

3. **Create Release**:
   ```bash
   git checkout -b release/v1.0.0
   git push origin release/v1.0.0
   # Automatic deployment to staging!
   ```

4. **Deploy to Production**:
   - On GitHub, create a new release with tag `v1.0.0`
   - Automatic deployment to production!

## Viewing Workflow Status

1. Go to your repository on GitHub
2. Click the **Actions** tab
3. See all workflow runs and their status
4. Click on any run to see detailed logs

## Status Badges

Add these to your README to show pipeline status:

```markdown
[![CI](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/ci.yml/badge.svg)](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/ci.yml)
```

## Common Issues

### Issue: "Workflow not running"
**Solution**: Check that the branch name matches the workflow trigger (e.g., `develop`, `main`)

### Issue: "Deployment step fails"
**Solution**: Verify all required secrets are configured in repository settings

### Issue: "Tests fail with database connection error"
**Solution**: The workflow includes a PostgreSQL service container - this is expected in CI. Ensure your application.properties uses environment variables for database connection.

### Issue: "SSH connection refused"
**Solution**: Verify `SERVER_SSH_KEY` secret is correctly formatted and server accepts SSH connections

## Next Steps

- [ ] Review [SETUP_CHECKLIST.md](SETUP_CHECKLIST.md) for complete setup
- [ ] Check [DEPLOYMENT_EXAMPLES.md](DEPLOYMENT_EXAMPLES.md) for your infrastructure
- [ ] Configure production environment protection rules
- [ ] Set up monitoring and alerts
- [ ] Document your deployment process

## Getting Help

1. Check workflow logs in GitHub Actions
2. Review [README.md](README.md) for detailed documentation
3. See [DEPLOYMENT_EXAMPLES.md](DEPLOYMENT_EXAMPLES.md) for examples
4. Open an issue if you need help

## Summary

You now have:
- ✅ Automated testing on every PR
- ✅ Automatic deployment to dev
- ✅ Staging deployments from release branches
- ✅ Production deployments from releases
- ✅ Status badges
- ✅ Comprehensive documentation

**Time to first deployment: 15 minutes!** 🚀
