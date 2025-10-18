# CI/CD Pipeline Flow Diagram

## Workflow Triggers and Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                         Git Branches                             │
└─────────────────────────────────────────────────────────────────┘

    feature/*  ──────►  develop  ────────►  release/*  ───────►  main
        │                 │                      │                 │
        │                 │                      │                 │
        ▼                 ▼                      ▼                 ▼
   Pull Request      Push to Dev         Push to Release     GitHub Release
        │                 │                      │                 │
        │                 │                      │                 │
┌───────▼─────────────────▼──────────────────────▼─────────────────▼────┐
│                    GitHub Actions Workflows                            │
└────────────────────────────────────────────────────────────────────────┘
        │                 │                      │                 │
        │                 │                      │                 │
        ▼                 ▼                      ▼                 ▼
    ┌───────┐      ┌──────────┐         ┌──────────┐       ┌────────────┐
    │  CI   │      │ Deploy   │         │ Deploy   │       │  Deploy    │
    │ Build │      │   Dev    │         │ Staging  │       │ Production │
    │ & Test│      │          │         │          │       │            │
    └───────┘      └──────────┘         └──────────┘       └────────────┘
        │                 │                      │                 │
        │                 │                      │                 │
        ▼                 ▼                      ▼                 ▼
   ✅ Tests         Dev Server          Staging Server      Production
   Passed                                                        Server


┌─────────────────────────────────────────────────────────────────┐
│                      Environment Flow                            │
└─────────────────────────────────────────────────────────────────┘

1. Developer creates feature branch
   └─► Works on feature locally

2. Create Pull Request to develop
   └─► CI workflow runs (build + test)
   └─► Code review
   └─► Merge to develop

3. Merge to develop branch
   └─► Deploy to Dev workflow runs automatically
   └─► Tests in development environment

4. Create release branch (release/v1.0.0)
   └─► Deploy to Staging workflow runs automatically
   └─► Integration tests + smoke tests
   └─► UAT testing

5. Create GitHub Release
   └─► Deploy to Production workflow runs
   └─► Backup current version
   └─► Deploy new version
   └─► Smoke tests + health checks
   └─► Rollback on failure


┌─────────────────────────────────────────────────────────────────┐
│                      Workflow Details                            │
└─────────────────────────────────────────────────────────────────┘

CI Workflow (ci.yml)
├─► Trigger: PR to main/develop, Push to main/develop
├─► Java 21 + PostgreSQL
├─► Build → Test → Package
└─► Upload WAR artifact

Deploy Dev (deploy-dev.yml)
├─► Trigger: Push to develop, Manual dispatch
├─► Environment: dev
├─► Build → Test → Deploy
└─► Notification

Deploy Staging (deploy-staging.yml)
├─► Trigger: Push to release/*, Manual dispatch
├─► Environment: staging
├─► Build → Test → Integration Tests → Deploy → Smoke Tests
└─► Notification

Deploy Production (deploy-production.yml)
├─► Trigger: GitHub Release, Manual dispatch
├─► Environment: production
├─► Backup → Build → Test → Deploy → Smoke Tests
└─► Rollback on failure


┌─────────────────────────────────────────────────────────────────┐
│                    Key Features                                  │
└─────────────────────────────────────────────────────────────────┘

✓ Automated testing with PostgreSQL
✓ Artifact management
✓ Environment-specific deployments
✓ Manual approval gates (configurable)
✓ Backup and rollback mechanisms
✓ Smoke tests and health checks
✓ Reusable workflow components
✓ Comprehensive documentation
```

## Environment URLs (Configure in GitHub Settings)

- **Dev**: https://dev-predictive-health-risk-assessment.example.com
- **Staging**: https://staging-predictive-health-risk-assessment.example.com  
- **Production**: https://predictive-health-risk-assessment.example.com

## Required GitHub Secrets

Configure these in: Repository Settings → Secrets and Variables → Actions

### For All Environments:
- `DB_PASSWORD` - Database password

### For Deployment (choose based on your infrastructure):
- `SERVER_SSH_KEY` - SSH key for server access
- `AWS_ACCESS_KEY_ID` - AWS access key
- `AWS_SECRET_ACCESS_KEY` - AWS secret key
- `AZURE_CREDENTIALS` - Azure service principal credentials

## Getting Started

1. **Configure Environments**: Set up dev, staging, and production in GitHub Settings
2. **Add Secrets**: Configure required secrets for your deployment method
3. **Customize Deployment**: Update deployment steps in workflows with your infrastructure commands
4. **Test**: Create a PR to test the CI workflow
5. **Deploy**: Merge to develop to test dev deployment

For detailed instructions, see [README.md](README.md)
