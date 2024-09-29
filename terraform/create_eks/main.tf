provider "aws" {
  region = var.region  # Change to your desired region
}

resource "aws_ec2_tag" "public_subnet_tags_1" {
  count    = length(var.vpc_subnets)
  
  resource_id = var.vpc_subnets[count.index]

  key   = "kubernetes.io/role/elb"
  value = "1"
}

resource "aws_ec2_tag" "public_subnet_tags_2" {
  count    = length(var.vpc_subnets)
  
  resource_id = var.vpc_subnets[count.index]

  key   = "kubernetes.io/cluster/${var.eks_name}"
  value = "shared"
}

module "eks" {
  source          = "terraform-aws-modules/eks/aws"
  cluster_name    = var.eks_name
  cluster_version = "1.25"
  
  cluster_endpoint_public_access  = true
  
  # VPC configuration
  vpc_id     = var.vpc_id
  subnet_ids = var.vpc_subnets 
  
  cluster_addons = {
    coredns                = {}
    eks-pod-identity-agent = {}
    kube-proxy             = {}
    vpc-cni                = {}
  }
  
  eks_managed_node_groups = {
    my_node_group = {
      instance_types = ["m5.xlarge"]
      key_name = var.key_name

      min_size     = 1
      max_size     = 2
      desired_size = 1
    }
  }
  
  enable_cluster_creator_admin_permissions = true

  tags = {
    "Environment" = "dev"
    "Name"        = "my-eks-cluster"
    Terraform   = "true"
  }
}

data "aws_eks_cluster_auth" "auth" {
  name = module.eks.cluster_name
}

provider "kubernetes" {
  host                   = module.eks.cluster_endpoint
  token                  = data.aws_eks_cluster_auth.auth.token
  cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
}

provider "helm" {
  kubernetes {
    host                   = module.eks.cluster_endpoint
    token                  = data.aws_eks_cluster_auth.auth.token
    cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
  }
}

module "create_addons" {
  source = "./modules/add_ons"
  cluster_name = module.eks.cluster_name
  oidc_url = module.eks.oidc_provider
  oidc_arn = module.eks.oidc_provider_arn
  vpc_id = var.vpc_id
  region = var.region
  depends_on = [aws_ec2_tag.public_subnet_tags_1, aws_ec2_tag.public_subnet_tags_2, module.eks]
}

module "app"{
  source = "./modules/app"
  depends_on = [module.create_addons]
}
