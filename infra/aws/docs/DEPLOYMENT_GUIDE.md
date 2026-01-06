# AWS CloudFormation Infrastructure Deployment Guide

## Overview
This is a production-grade, account-agnostic AWS infrastructure stack for deploying:
- Single EC2 Docker host (Ubuntu 22.04)
- MySQL RDS database (publicly accessible)
- VPC with public subnet
- Security groups with IP-restricted database access

## Stack Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      AWS Account                             │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ VPC (10.0.0.0/16) - network-stack                    │   │
│  │ ┌──────────────────────────────────────────────────┐ │   │
│  │ │ Public Subnet (10.0.1.0/24)                      │ │   │
│  │ │ ┌───────────────────┐  ┌──────────────────────┐ │ │   │
│  │ │ │  EC2 Instance     │  │  RDS MySQL Instance  │ │ │   │
│  │ │ │  t3.micro         │  │  db.t3.micro         │ │ │   │
│  │ │ │  Docker host      │  │  Public IP (SG)      │ │ │   │
│  │ │ │                   │  │                      │ │ │   │
│  │ │ │ - Jenkins         │  │  Port: 3306          │ │ │   │
│  │ │ │ - Nginx           │  │  20 GB storage       │ │ │   │
│  │ │ │ - Docker Compose  │  │                      │ │ │   │
│  │ │ └─────────┬─────────┘  └──────────┬───────────┘ │ │   │
│  │ │           │ (EC2-SG)              │ (RDS-SG)    │ │   │
│  │ │           └──────────────────────┬┘             │ │   │
│  │ │                                  │               │ │   │
│  │ └──────────────────────────────────┼───────────────┘ │   │
│  │                                   │                  │   │
│  │  ┌─────────────────────────────────┘                 │   │
│  │  │ Internet Gateway (IGW)                            │   │
│  │  └─────────────────────────────────┬─────────────────┘   │
│  └──────────────────────────────────────────────────────────┘
│                                       │
│  ┌──────────────────────────────────┘
│  │ Internet / External Services
│
└─────────────────────────────────────────────────────────────┘
```

## Deployment Order

**Important**: Deploy stacks in this specific order due to cross-stack dependencies:

1. **network-stack.yaml** (no dependencies)
2. **security-stack.yaml** (depends on network-stack outputs)
3. **rds-stack.yaml** (depends on network-stack and security-stack outputs)
4. **ec2-stack.yaml** (depends on network-stack and security-stack outputs)

## Prerequisites

Before deploying, ensure:

1. **AWS Credentials Configured**
   ```bash
   aws configure
   # Enter: Access Key, Secret Key, Region (e.g., us-east-1), Output (json)
   ```

2. **EC2 Key Pair Created**
   ```bash
   # Create in AWS Console: EC2 → Key Pairs → Create key pair
   # Or via AWS CLI:
   aws ec2 create-key-pair --key-name my-key --query 'KeyMaterial' --output text > my-key.pem
   chmod 600 my-key.pem
   ```

3. **Environment Variables Set**
   ```bash
   # Store in .env or set in terminal
   export ENVIRONMENT=dev
   export AWS_REGION=us-east-1
   export KEY_PAIR_NAME=my-key
   export DEVELOPER_IP=203.0.113.45/32  # Your local IP (get from: curl ifconfig.me)
   ```

## Deployment Commands

### Stack 1: Network Stack

```bash
aws cloudformation create-stack \
  --stack-name ${ENVIRONMENT}-network-stack \
  --template-body file://network-stack.yaml \
  --parameters \
    ParameterKey=EnvironmentName,ParameterValue=${ENVIRONMENT} \
    ParameterKey=VpcCIDR,ParameterValue=10.0.0.0/16 \
    ParameterKey=PublicSubnetCIDR,ParameterValue=10.0.1.0/24 \
  --region ${AWS_REGION} \
  --tags \
    Key=Environment,Value=${ENVIRONMENT} \
    Key=ManagedBy,Value=CloudFormation
```

Wait for stack creation (check status):
```bash
aws cloudformation wait stack-create-complete \
  --stack-name ${ENVIRONMENT}-network-stack \
  --region ${AWS_REGION}
```

### Stack 2: Security Stack

```bash
aws cloudformation create-stack \
  --stack-name ${ENVIRONMENT}-security-stack \
  --template-body file://security-stack.yaml \
  --parameters \
    ParameterKey=EnvironmentName,ParameterValue=${ENVIRONMENT} \
    ParameterKey=DeveloperIPAddress,ParameterValue=${DEVELOPER_IP} \
    ParameterKey=DBPort,ParameterValue=3306 \
  --region ${AWS_REGION} \
  --tags \
    Key=Environment,Value=${ENVIRONMENT} \
    Key=ManagedBy,Value=CloudFormation
```

Wait:
```bash
aws cloudformation wait stack-create-complete \
  --stack-name ${ENVIRONMENT}-security-stack \
  --region ${AWS_REGION}
```

### Stack 3: RDS Stack

```bash
aws cloudformation create-stack \
  --stack-name ${ENVIRONMENT}-rds-stack \
  --template-body file://rds-stack.yaml \
  --parameters \
    ParameterKey=EnvironmentName,ParameterValue=${ENVIRONMENT} \
    ParameterKey=DBEngine,ParameterValue=mysql \
    ParameterKey=DBEngineVersion,ParameterValue=8.0.35 \
    ParameterKey=DBInstanceClass,ParameterValue=db.t3.micro \
    ParameterKey=AllocatedStorage,ParameterValue=20 \
    ParameterKey=DBName,ParameterValue=identitydb \
    ParameterKey=DBUsername,ParameterValue=admin \
    ParameterKey=DBPassword,ParameterValue='YourSecurePassword123!' \
    ParameterKey=BackupRetentionDays,ParameterValue=0 \
    ParameterKey=PubliclyAccessible,ParameterValue=true \
    ParameterKey=StorageEncrypted,ParameterValue=false \
  --region ${AWS_REGION} \
  --tags \
    Key=Environment,Value=${ENVIRONMENT} \
    Key=ManagedBy,Value=CloudFormation
```

**Important**: Replace `YourSecurePassword123!` with a strong password (8+ chars, mix of upper/lower/special).

Wait:
```bash
aws cloudformation wait stack-create-complete \
  --stack-name ${ENVIRONMENT}-rds-stack \
  --region ${AWS_REGION}
```

### Stack 4: EC2 Stack

```bash
aws cloudformation create-stack \
  --stack-name ${ENVIRONMENT}-ec2-stack \
  --template-body file://ec2-stack.yaml \
  --parameters \
    ParameterKey=EnvironmentName,ParameterValue=${ENVIRONMENT} \
    ParameterKey=InstanceType,ParameterValue=t3.micro \
    ParameterKey=KeyPairName,ParameterValue=${KEY_PAIR_NAME} \
    ParameterKey=InstanceName,ParameterValue=docker-host \
    ParameterKey=RootVolumeSize,ParameterValue=20 \
    ParameterKey=RootVolumeType,ParameterValue=gp3 \
    ParameterKey=EnableMonitoring,ParameterValue=false \
  --region ${AWS_REGION} \
  --capabilities CAPABILITY_NAMED_IAM \
  --tags \
    Key=Environment,Value=${ENVIRONMENT} \
    Key=ManagedBy,Value=CloudFormation
```

Wait:
```bash
aws cloudformation wait stack-create-complete \
  --stack-name ${ENVIRONMENT}-ec2-stack \
  --region ${AWS_REGION}
```

## Retrieve Outputs

After all stacks are created, retrieve connection details:

```bash
# Get all stack outputs
aws cloudformation describe-stacks \
  --stack-name ${ENVIRONMENT}-ec2-stack \
  --region ${AWS_REGION} \
  --query 'Stacks[0].Outputs' \
  --output table

# Get RDS endpoint
aws cloudformation describe-stacks \
  --stack-name ${ENVIRONMENT}-rds-stack \
  --region ${AWS_REGION} \
  --query 'Stacks[0].Outputs[?OutputKey==`RDSEndpoint`].OutputValue' \
  --output text

# Get EC2 public IP
aws cloudformation describe-stacks \
  --stack-name ${ENVIRONMENT}-ec2-stack \
  --region ${AWS_REGION} \
  --query 'Stacks[0].Outputs[?OutputKey==`ElasticIPAddress`].OutputValue' \
  --output text
```

## Post-Deployment Steps

### 1. Connect to EC2 Instance

```bash
EC2_IP=$(aws cloudformation describe-stacks \
  --stack-name ${ENVIRONMENT}-ec2-stack \
  --region ${AWS_REGION} \
  --query 'Stacks[0].Outputs[?OutputKey==`ElasticIPAddress`].OutputValue' \
  --output text)

ssh -i /path/to/${KEY_PAIR_NAME}.pem ubuntu@${EC2_IP}
```

### 2. Verify Installations

```bash
# On EC2 instance
docker --version
docker compose version
nginx -v
docker ps  # Should work without sudo (ubuntu user in docker group)
```

### 3. Test RDS Connectivity

```bash
# From EC2 instance
RDS_ENDPOINT=$(aws cloudformation describe-stacks \
  --stack-name ${ENVIRONMENT}-rds-stack \
  --region us-east-1 \
  --query 'Stacks[0].Outputs[?OutputKey==`RDSEndpoint`].OutputValue' \
  --output text)

mysql -h ${RDS_ENDPOINT} -u admin -p -e "SHOW DATABASES;"
# Enter password when prompted
```

### 4. Test Nginx

```bash
curl http://${EC2_IP}/health
# Should return: healthy
```

## Migrating to New AWS Account

### Account-to-Account Migration Steps

1. **No Code Changes Required** ✅
   - Templates are completely account-agnostic
   - All references use CloudFormation Exports/Imports
   - No hardcoded account IDs or region assumptions

2. **Create New EC2 Key Pair** in new account
   ```bash
   aws ec2 create-key-pair --key-name my-key --query 'KeyMaterial' --output text > my-key.pem
   chmod 600 my-key.pem
   ```

3. **Update Environment Variables**
   ```bash
   export AWS_PROFILE=new-account  # If using named profiles
   export AWS_REGION=us-east-1     # Can be different region
   export ENVIRONMENT=dev
   export KEY_PAIR_NAME=my-key
   ```

4. **Deploy Stacks in Same Order** (use commands above)

5. **Backup RDS from Old Account** (optional)
   ```bash
   # Create RDS snapshot before cleanup
   aws rds create-db-snapshot \
     --db-instance-identifier dev-identity-db \
     --db-snapshot-identifier dev-identity-db-backup \
     --region us-east-1
   ```

## Cleanup (Delete Infrastructure)

**Important**: This deletes ALL resources permanently. RDS has DeletionPolicy: Snapshot (auto-backup).

```bash
# Delete in REVERSE order
aws cloudformation delete-stack --stack-name ${ENVIRONMENT}-ec2-stack --region ${AWS_REGION}
aws cloudformation wait stack-delete-complete --stack-name ${ENVIRONMENT}-ec2-stack --region ${AWS_REGION}

aws cloudformation delete-stack --stack-name ${ENVIRONMENT}-rds-stack --region ${AWS_REGION}
aws cloudformation wait stack-delete-complete --stack-name ${ENVIRONMENT}-rds-stack --region ${AWS_REGION}

aws cloudformation delete-stack --stack-name ${ENVIRONMENT}-security-stack --region ${AWS_REGION}
aws cloudformation wait stack-delete-complete --stack-name ${ENVIRONMENT}-security-stack --region ${AWS_REGION}

aws cloudformation delete-stack --stack-name ${ENVIRONMENT}-network-stack --region ${AWS_REGION}
aws cloudformation wait stack-delete-complete --stack-name ${ENVIRONMENT}-network-stack --region ${AWS_REGION}

echo "All stacks deleted"
```

## Cost Estimation (Free Tier)

- **EC2 t3.micro**: Free (750 hours/month)
- **RDS db.t3.micro**: Free (750 hours/month, single-AZ)
- **VPC + Subnet**: Free
- **Data Transfer (out)**: 1 GB/month free
- **CloudWatch Logs**: 5 GB ingestion free, 1 GB retention free

**Total**: ~$0/month within free tier limits

## Troubleshooting

### Stack Creation Fails

```bash
# View detailed error
aws cloudformation describe-stack-events \
  --stack-name ${ENVIRONMENT}-ec2-stack \
  --region ${AWS_REGION} \
  --query 'StackEvents[?ResourceStatus==`CREATE_FAILED`]'
```

### EC2 Instance Not Starting

```bash
# Check user-data logs
aws ssm start-session --target <INSTANCE_ID>
# Then: tail -f /var/log/user-data.log
```

### RDS Not Accessible

Check security group rules:
```bash
aws ec2 describe-security-groups \
  --group-ids <RDS_SG_ID> \
  --region ${AWS_REGION} \
  --query 'SecurityGroups[0].IpPermissions'
```

### Can't SSH to EC2

1. Verify key permissions: `chmod 600 my-key.pem`
2. Verify security group allows port 22 from your IP
3. Wait 5 minutes for instance to initialize

## Security Best Practices Applied

✅ **Network Isolation**
- VPC with private CIDR space (no default VPC)
- Public subnet only where needed

✅ **Database Security**
- RDS in security group (no 0.0.0.0/0 access)
- IP-restricted access (EC2 + developer IP only)
- Credentials as parameters (NoEcho: true)

✅ **EC2 Security**
- SSH, HTTP, HTTPS ports restricted
- IAM role for future AWS service access
- CloudWatch logging for audit trail

✅ **Account Safety**
- CloudFormation stack protection possible
- RDS DeletionPolicy: Snapshot (auto-backup)
- All resources tagged for cost allocation

## Production Adjustments

To harden for production:

1. **RDS**
   ```yaml
   PubliclyAccessible: false          # No public internet
   StorageEncrypted: true             # Encrypt at rest
   BackupRetentionDays: 7             # 7-day backups
   MultiAZ: true                      # High availability
   ```

2. **EC2**
   ```yaml
   InstanceType: t3.small             # More resources
   RootVolumeType: gp3                # Better performance
   EnableMonitoring: true             # CloudWatch details
   ```

3. **Security**
   ```yaml
   DeveloperIPAddress: x.x.x.x/32     # Specific IPs only
   # Add SSH IP restrictions
   ```

## Support Resources

- [AWS CloudFormation User Guide](https://docs.aws.amazon.com/cloudformation/)
- [RDS MySQL Documentation](https://docs.aws.amazon.com/rds/latest/UserGuide/USER_Databases.html)
- [EC2 Linux Documentation](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/)
- [VPC Best Practices](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Subnets.html)

---

**Created**: 2026-01-06
**Last Updated**: 2026-01-06
**Template Version**: 1.0.0

