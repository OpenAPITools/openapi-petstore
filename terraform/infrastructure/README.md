The configuration is organized across multiple files:

`versions.tf` sets the Terraform version to at least 1.2. It also sets versions for the providers used by the configuration.

`variables.tf` contains a region variable that controls where to create the EKS cluster

`vpc.tf` provisions a VPC, subnets, and availability zones using the AWS VPC Module. The module creates a new VPC for this tutorial so it doesn't impact your existing cloud environment and resources.

`security-groups.tf` provisions the security groups the EKS cluster will use

`eks-cluster.tf` uses the AWS EKS Module to provision an EKS Cluster and other required resources, including Auto Scaling Groups, Security Groups, IAM Roles, and IAM Policies.