# CI/CD Pipeline Setup

This document explains how to configure the CI/CD pipeline for the customer-service microservice.

## Overview

The CI/CD pipeline is configured to:
- **On Pull Request to main/master**: Validate the application build and run SonarQube code quality analysis with minimum 70% coverage
- **On Merge to main/master**: Build and execute all microservices (customer-service, order-service, payment-service, production-service)

## GitHub Secrets Configuration

You need to configure the following secrets in your GitHub repository:

### 1. SONAR_TOKEN
- Your SonarCloud authentication token
- Get it from: SonarCloud → My Account → Security → Generate Token
- Add to: GitHub Repository → Settings → Secrets and variables → Actions → New repository secret

### 2. SONAR_ORGANIZATION
- Your SonarCloud organization key
- Find it at: SonarCloud → My Organizations → [Your Organization] → Key
- Add to: GitHub Repository → Settings → Secrets and variables → Actions → New repository secret

**Note:** The workflow is pre-configured to use SonarCloud at `https://sonarcloud.io`

## How to Add Secrets to GitHub

1. Go to your GitHub repository
2. Click on **Settings**
3. In the left sidebar, click on **Secrets and variables** → **Actions**
4. Click **New repository secret**
5. Add the first secret:
   - Name: `SONAR_TOKEN`
   - Value: Your SonarCloud token
   - Click **Add secret**
6. Add the second secret:
   - Name: `SONAR_ORGANIZATION`
   - Value: Your SonarCloud organization key
   - Click **Add secret**

## SonarCloud Project Configuration

1. Go to https://sonarcloud.io and log in
2. Create a new project or select your existing project
3. **Important**: Set the project key as: `customer-service`
4. Note your organization key (you'll need this for the `SONAR_ORGANIZATION` secret)
5. Go to **Quality Gates**
6. Create or modify a quality gate to enforce minimum 70% code coverage:
   - Go to Quality Gates → Create or select a gate
   - Add condition: Coverage on New Code ≥ 70%
   - Assign this quality gate to your `customer-service` project
7. Ensure your project is set to analyze the main/master branch

## Coverage Configuration

The project is configured with:
- **JaCoCo** for code coverage measurement
- **Minimum coverage**: 70% (configured in pom.xml)
- **Excluded from coverage**:
  - Configuration classes (**/config/**)
  - DTOs (**/dto/**)
  - Entities (**/entities/**)
  - Exceptions (**/exceptions/**)
  - Request/Response models (**/requests/**, **/responses/**)

## Workflow Triggers

### Pull Request Workflow
Triggers when:
- A pull request is opened to `main` or `master` branch
- A pull request is updated

Steps:
1. Checkout code
2. Set up Java 17
3. Build with Maven
4. Run tests
5. Run SonarQube analysis with quality gate validation
6. Upload coverage reports as artifacts

### Deployment Workflow
Triggers when:
- Code is merged to `main` or `master` branch

Steps:
1. Checkout code
2. Set up Java 17
3. Build all microservices:
   - customer-service
   - order-service
   - payment-service
   - production-service
4. Build Docker images (if Dockerfiles exist)
5. Upload JAR artifacts

## Testing the Pipeline

### Test PR Workflow
1. Create a new branch: `git checkout -b feature/test-pipeline`
2. Make a small change
3. Push the branch: `git push origin feature/test-pipeline`
4. Create a pull request to `main` or `master`
5. Check the **Actions** tab in GitHub to see the workflow running

### Test Deployment Workflow
1. Merge the pull request
2. Check the **Actions** tab to see the deployment workflow running

## Troubleshooting

### SonarCloud Analysis Fails
- Verify `SONAR_TOKEN` and `SONAR_ORGANIZATION` are correctly set in GitHub Secrets
- Check that the project key matches in SonarCloud: `customer-service`
- Ensure your SonarCloud token has permission to analyze projects
- Verify the organization key is correct (find it at SonarCloud → My Organizations)

### Coverage Below 70%
- Review the JaCoCo report in the workflow artifacts
- Add more unit tests to increase coverage
- Check that test files are properly located in `src/test/java`

### Build Fails for Other Microservices
- The workflow uses `continue-on-error: true` for other services
- Ensure each microservice has a valid `pom.xml`
- Each service must be buildable independently

## Local Testing

To test the build and coverage locally:

```bash
# Run tests with coverage
mvn clean test

# Generate coverage report
mvn jacoco:report

# View coverage report
open target/site/jacoco/index.html

# Run SonarCloud analysis locally
mvn sonar:sonar \
  -Dsonar.projectKey=customer-service \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=YOUR_SONAR_ORGANIZATION \
  -Dsonar.login=YOUR_SONAR_TOKEN
```
