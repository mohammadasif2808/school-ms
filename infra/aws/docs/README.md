# AWS CloudFormation Infrastructure Stack

**Production-Grade | Account-Agnostic | Modular | Reusable**

## Overview

This is a **four-stack CloudFormation infrastructure** designed for deploying a single EC2 Docker host with MySQL database in AWS. The infrastructure is:

✅ **Production-ready** - Security best practices, logging, monitoring  
✅ **Account-agnostic** - Deploy identically across multiple AWS accounts  
✅ **Region-agnostic** - No hardcoded regions or AZs  
✅ **Modular** - Stacks can be deployed/deleted independently  
✅ **Reusable** - Future-proof for 7+ microservices  
✅ **Cost-optimized** - Completely within AWS Free Tier

## Stack Architecture

### 4-Stack Design

```
┌─────────────────────────────────────────┐
│         network-stack                   │
│  • VPC (10.0.0.0/16)                   │
│  • Public Subnet (10.0.1.0/24)          │
│  • Internet Gateway                     │
│  • Route Tables                         │
│  • Network ACLs                         │
└────────────────┬────────────────────────┘
                 │ (Exports)
    ┌────────────┴────────────┐
    │                         │
┌───▼──────────────────┐  ┌──▼──────────────────┐
│ security-stack       │  │ rds-stack           │
│ • EC2 Security Group │  │ • RDS MySQL 8.0.35 │
│   (SSH, HTTP, HTTPS) │  │ • db.t3.micro       │
│ • RDS Security Group │  │ • 20 GB storage     │
│   (IP-restricted)    │  │ • Public accessible │
└───┬──────────────────┘  └──┬─────────────────┘
    │                         │
    └────────────┬────────────┘
                 │ (Imports)
                 ▼
         ┌──────────────────┐
         │  ec2-stack       │
         │ • EC2 instance   │
         │   Ubuntu 22.04   │
         │ • Docker         │
         │ • Docker Compose │
         │ • Nginx          │
         └──────────────────┘
```

## Quick Start

### 1. Prerequisites

- AWS Account with free tier credits available
- AWS CLI installed and configured
- Bash shell (Linux, macOS, WSL on Windows)
- EC2 Key Pair created

```bash
# Install AWS CLI (if needed)
# https://aws.amazon.com/cli/

# Configure AWS credentials
aws configure
# Enter: Access Key, Secret Key, Region (e.g., us-east-1), Output (json)

# Verify configuration
aws sts get-caller-identity

# Create EC2 Key Pair
aws ec2 create-key-pair --key-name my-key --query 'KeyMaterial' --output text > my-key.pem
chmod 600 my-key.pem
```

### 2. Customize Parameters

```bash
# Copy and edit parameters
cp parameters.sh.template parameters.sh
# Edit with your values:
# - ENVIRONMENT=dev
# - AWS_REGION=us-east-1
# - KEY_PAIR_NAME=my-key
# - DEVELOPER_IP_ADDRESS=YOUR_PUBLIC_IP/32 (get from: curl ifconfig.me)
# - DB_PASSWORD=YourSecurePassword123!
```

### 3. Deploy Infrastructure

```bash
# Make scripts executable
chmod +x deploy.sh cleanup.sh

# Deploy all stacks
./deploy.sh
# Confirm when prompted
# Wait ~10-15 minutes for full deployment
```

### 4. Access Infrastructure

```bash
# Get EC2 public IP
aws cloudformation describe-stacks \
  --stack-name dev-ec2-stack \
  --region us-east-1 \
  --query 'Stacks[0].Outputs[?OutputKey==`ElasticIPAddress`].OutputValue' \
  --output text

# SSH to EC2
ssh -i my-key.pem ubuntu@<ELASTIC_IP>

# Verify installations
docker --version
docker compose version
nginx -v
```

## Files Included

### CloudFormation Templates

| File | Purpose | Exports |
|------|---------|---------|
| `network-stack.yaml` | VPC, Subnets, IGW | VpcId, PublicSubnetId |
| `security-stack.yaml` | Security Groups | EC2SecurityGroupId, RDSSecurityGroupId |
| `rds-stack.yaml` | MySQL Database | RDSEndpoint, DBName, RDSConnectionString |
| `ec2-stack.yaml` | Docker Host | InstanceId, ElasticIPAddress, InstancePublicIP |

### Automation Scripts

| File | Purpose |
|------|---------|
| `deploy.sh` | Deploy all 4 stacks in correct order |
| `cleanup.sh` | Delete all stacks in reverse order |
| `parameters.sh` | Configuration parameters for deployment |

### Documentation

| File | Purpose |
|------|---------|
| `README.md` | This file |
| `DEPLOYMENT_GUIDE.md` | Detailed deployment instructions |
| `ARCHITECTURE.md` | Architecture decisions and patterns |

## Deployment Guide

### Full Deployment

```bash
# 1. Load parameters
source parameters.sh

# 2. Validate AWS setup
aws sts get-caller-identity
aws ec2 describe-key-pairs --key-names ${KEY_PAIR_NAME}

# 3. Deploy stacks
./deploy.sh
```

### Manual Stack Deployment

If you prefer to deploy stacks individually:

```bash
# Stack 1: Network
aws cloudformation create-stack \
  --stack-name dev-network-stack \
  --template-body file://network-stack.yaml \
  --parameters \
    ParameterKey=EnvironmentName,ParameterValue=dev \
    ParameterKey=VpcCIDR,ParameterValue=10.0.0.0/16 \
    ParameterKey=PublicSubnetCIDR,ParameterValue=10.0.1.0/24 \
  --region us-east-1

# Wait for completion
aws cloudformation wait stack-create-complete --stack-name dev-network-stack --region us-east-1

# Stack 2: Security
aws cloudformation create-stack \
  --stack-name dev-security-stack \
  --template-body file://security-stack.yaml \
  --parameters \
    ParameterKey=EnvironmentName,ParameterValue=dev \
    ParameterKey=DeveloperIPAddress,ParameterValue=YOUR_IP/32 \
    ParameterKey=DBPort,ParameterValue=3306 \
  --region us-east-1

# Wait for completion
aws cloudformation wait stack-create-complete --stack-name dev-security-stack --region us-east-1

# Stack 3: RDS
aws cloudformation create-stack \
  --stack-name dev-rds-stack \
  --template-body file://rds-stack.yaml \
  --parameters \
    ParameterKey=EnvironmentName,ParameterValue=dev \
    ParameterKey=DBPassword,ParameterValue=YourPassword123! \
  --region us-east-1

# Wait for completion (takes ~5-10 minutes)
aws cloudformation wait stack-create-complete --stack-name dev-rds-stack --region us-east-1

# Stack 4: EC2
aws cloudformation create-stack \
  --stack-name dev-ec2-stack \
  --template-body file://ec2-stack.yaml \
  --capabilities CAPABILITY_NAMED_IAM \
  --parameters \
    ParameterKey=EnvironmentName,ParameterValue=dev \
    ParameterKey=KeyPairName,ParameterValue=my-key \
  --region us-east-1

# Wait for completion
aws cloudformation wait stack-create-complete --stack-name dev-ec2-stack --region us-east-1
```

## Post-Deployment

### 1. Verify EC2 Instance

```bash
# Get instance IP
EC2_IP=$(aws ec2 describe-instances \
  --filters "Name=tag:Name,Values=dev-docker-host" \
  --query 'Reservations[0].Instances[0].PublicIpAddress' \
  --region us-east-1 \
  --output text)

# SSH to instance
ssh -i my-key.pem ubuntu@${EC2_IP}

# Verify Docker
docker ps
docker --version
docker compose version

# Verify Nginx
systemctl status nginx
curl http://localhost/health
```

### 2. Verify RDS Database

```bash
# From EC2 instance or local machine (if in DEVELOPER_IP_ADDRESS)
RDS_ENDPOINT=$(aws cloudformation describe-stacks \
  --stack-name dev-rds-stack \
  --region us-east-1 \
  --query 'Stacks[0].Outputs[?OutputKey==`RDSEndpoint`].OutputValue' \
  --output text)

# Connect to database
mysql -h ${RDS_ENDPOINT} -u admin -p identitydb
# Enter password when prompted

# Show databases
SHOW DATABASES;

# Exit
exit
```

### 3. Check CloudWatch Logs

```bash
# RDS error logs
aws logs tail /aws/rds/instance/dev-identity-db/error --follow

# EC2 user-data logs (SSH first)
ssh -i my-key.pem ubuntu@${EC2_IP}
tail -f /var/log/user-data.log
```

## Account Migration

### Moving to New AWS Account

The templates are **100% account-agnostic**. To migrate:

1. **Create EC2 Key Pair** in new account
   ```bash
   aws ec2 create-key-pair --key-name my-key --profile new-account
   ```

2. **Update AWS credentials**
   ```bash
   aws configure --profile new-account
   export AWS_PROFILE=new-account
   ```

3. **Deploy using same scripts**
   ```bash
   ./deploy.sh
   ```

4. **Backup old account RDS** (optional)
   ```bash
   aws rds create-db-snapshot \
     --db-instance-identifier dev-identity-db \
     --db-snapshot-identifier dev-identity-db-backup-2025 \
     --profile old-account
   ```

**No code changes required!** All outputs use cross-stack references.

## Security Features

### Network Security
✅ Custom VPC (no default VPC assumption)  
✅ Public subnet for web-facing services  
✅ Network ACLs for stateless filtering  
✅ Internet Gateway for controlled egress  

### EC2 Security
✅ Security group with port restrictions  
✅ SSH, HTTP, HTTPS only  
✅ IAM role for future AWS service access  
✅ CloudWatch logging  

### Database Security
✅ Security group with IP-restricted access  
✅ Only EC2 and developer IP can access  
✅ No 0.0.0.0/0 ingress  
✅ Database credentials as parameters (NoEcho)  
✅ Optional encryption at rest  

### Data Backups
✅ RDS auto-backup on deletion (DeletionPolicy: Snapshot)  
✅ 0-35 day retention configurable  
✅ Backup window during low-traffic hours  

## Cost Estimation

All resources within **AWS Free Tier**:

| Resource | Free Tier Limit | Actual Usage | Cost |
|----------|-----------------|--------------|------|
| EC2 t3.micro | 750 hours/month | 730 hours | $0 |
| RDS db.t3.micro | 750 hours/month | 730 hours | $0 |
| RDS Storage | 20 GB/month | 20 GB | $0 |
| Data Transfer (out) | 1 GB/month | <1 GB | $0 |
| **Monthly Total** | | | **$0** |

**Note**: After free tier expires, estimate ~$15-20/month for dev environment.

## Cleanup

### Delete All Infrastructure

```bash
# Deletes in REVERSE dependency order
./cleanup.sh
# Type: DELETE ALL (required confirmation)
```

### Manual Deletion

```bash
# REVERSE order: EC2 → RDS → Security → Network
aws cloudformation delete-stack --stack-name dev-ec2-stack --region us-east-1
aws cloudformation wait stack-delete-complete --stack-name dev-ec2-stack --region us-east-1

aws cloudformation delete-stack --stack-name dev-rds-stack --region us-east-1
aws cloudformation wait stack-delete-complete --stack-name dev-rds-stack --region us-east-1

aws cloudformation delete-stack --stack-name dev-security-stack --region us-east-1
aws cloudformation wait stack-delete-complete --stack-name dev-security-stack --region us-east-1

aws cloudformation delete-stack --stack-name dev-network-stack --region us-east-1
aws cloudformation wait stack-delete-complete --stack-name dev-network-stack --region us-east-1
```

## Troubleshooting

### Stack Creation Fails

Check stack events for errors:
```bash
aws cloudformation describe-stack-events \
  --stack-name dev-ec2-stack \
  --region us-east-1 \
  --query 'StackEvents[?ResourceStatus==`CREATE_FAILED`]' \
  --output table
```

### Can't SSH to EC2

1. Verify security group allows port 22:
   ```bash
   aws ec2 describe-security-groups \
     --group-ids sg-xxxxx \
     --region us-east-1 \
     --query 'SecurityGroups[0].IpPermissions'
   ```

2. Verify key pair is correct:
   ```bash
   chmod 600 my-key.pem
   ssh -i my-key.pem -v ubuntu@<IP>  # Verbose mode
   ```

3. Wait for EC2 instance to finish initialization (2-3 minutes)

### RDS Not Accessible

1. Verify security group allows port 3306:
   ```bash
   aws ec2 describe-security-groups \
     --group-ids sg-xxxxx \
     --region us-east-1
   ```

2. Verify your IP is in DEVELOPER_IP_ADDRESS:
   ```bash
   curl ifconfig.me  # Your public IP
   ```

3. Verify RDS is publicly accessible:
   ```bash
   aws rds describe-db-instances \
     --db-instance-identifier dev-identity-db \
     --region us-east-1 \
     --query 'DBInstances[0].PubliclyAccessible'
   ```

### Docker Commands Fail Without Sudo

Ubuntu user must be added to docker group:
```bash
# From EC2 (after SSH)
groups ubuntu  # Should show: ubuntu adm dialout sudo docker

# If not, reboot:
sudo reboot

# After reboot, try:
docker ps  # Should work without sudo
```

## Advanced Configuration

### For Production Deployment

Edit `parameters.sh`:

```bash
# Higher instance class
EC2_INSTANCE_TYPE=t3.small

# RDS encryption
STORAGE_ENCRYPTED=true

# RDS backups
BACKUP_RETENTION_DAYS=7

# Restricted SSH
DEVELOPER_IP_ADDRESS=YOUR_OFFICE_IP/32

# Multi-AZ (add to rds-stack.yaml)
MultiAZ: true

# CloudWatch monitoring
ENABLE_MONITORING=true
```

### Custom CIDR Blocks

```bash
# Change VPC/subnet CIDR if overlap
VPC_CIDR=10.1.0.0/16
PUBLIC_SUBNET_CIDR=10.1.1.0/24
```

### Different Database Engine

```bash
DB_ENGINE=postgres
DB_ENGINE_VERSION=15.2
```

## File Structure

```
infra/aws/
├── network-stack.yaml          # VPC, subnets, IGW
├── security-stack.yaml         # Security groups
├── rds-stack.yaml              # MySQL database
├── ec2-stack.yaml              # Docker host
├── deploy.sh                   # Deployment automation
├── cleanup.sh                  # Cleanup automation
├── parameters.sh               # Configuration (customize this)
├── README.md                   # This file
└── DEPLOYMENT_GUIDE.md         # Detailed deployment steps
```

## Next Steps

After infrastructure is deployed:

1. **Deploy Jenkins** in Docker container on EC2
2. **Deploy identity-service** microservice
3. **Set up CI/CD pipelines** for automated builds
4. **Configure Nginx** reverse proxy routing
5. **Add CloudWatch monitoring** for all services
6. **Set up automated backups** and disaster recovery

## Support & Resources

- [AWS CloudFormation Documentation](https://docs.aws.amazon.com/cloudformation/)
- [RDS MySQL Documentation](https://docs.aws.amazon.com/rds/latest/UserGuide/)
- [EC2 Linux Documentation](https://docs.aws.amazon.com/ec2/index.html)
- [VPC Networking Guide](https://docs.aws.amazon.com/vpc/latest/userguide/)

## License

Internal use only - School Management System infrastructure

## Change Log

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2026-01-06 | Initial release - 4-stack design |

---

**Infrastructure Version**: 1.0.0  
**Last Updated**: 2026-01-06  
**Template Format**: CloudFormation YAML  
**Status**: Production-Ready ✅

