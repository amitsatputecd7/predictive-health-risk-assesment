# GitHub Actions Status Badges

Add these badges to your README.md to display the status of your workflows:

## CI Build Status
```markdown
[![CI - Build and Test](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/ci.yml/badge.svg)](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/ci.yml)
```

## Deployment Status

### Dev Environment
```markdown
[![Deploy to Dev](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/deploy-dev.yml/badge.svg)](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/deploy-dev.yml)
```

### Staging Environment
```markdown
[![Deploy to Staging](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/deploy-staging.yml/badge.svg)](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/deploy-staging.yml)
```

### Production Environment
```markdown
[![Deploy to Production](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/deploy-production.yml/badge.svg)](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/deploy-production.yml)
```

## All in One

Add all badges at once to the top of your README.md:

```markdown
# Predictive Health Risk Assessment API

[![CI - Build and Test](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/ci.yml/badge.svg)](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/ci.yml)
[![Deploy to Dev](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/deploy-dev.yml/badge.svg)](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/deploy-dev.yml)
[![Deploy to Staging](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/deploy-staging.yml/badge.svg)](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/deploy-staging.yml)
[![Deploy to Production](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/deploy-production.yml/badge.svg)](https://github.com/amitsatputecd7/predictive-health-risk-assesment/actions/workflows/deploy-production.yml)

A Spring Boot REST API application that provides health risk assessment services...
```

## Usage

Simply copy the markdown code above and paste it at the top of your README.md file, right after the title.

The badges will automatically update to show:
- ✅ Passing (green) when the workflow succeeds
- ❌ Failing (red) when the workflow fails
- 🟡 Pending (yellow) when the workflow is running
