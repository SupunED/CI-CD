# Operations Runbook – HealthPlus

## 1. App Monitoring
- Monitor CI builds in GitHub Actions
- Crash logs tracked via Firebase Crashlytics (planned)

## 2. CI/CD
- Trigger: Push to `main` or PRs
- Jobs:  `lint`, `unit-test`, deployment
- Secrets: API keys not exposed (in GitHub secrets/local.properties)

## 3. Recovery Plan
- Restore Firebase data using `.json` export
- Re-deploy app via GitHub Actions if CI fails

## 4. Incident Response
- GitHub issues tagged with `bug`
- Hotfix branch created for critical issues
- Triage response within 24 hours
