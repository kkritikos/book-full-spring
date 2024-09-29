variable "region" {
  description = "The name of the AWS region to use"
  type        = string
  default     = "us-west-2"
}

variable "key_name" {
  description = "The name of the key-pair to use"
  type        = string
  default     = "cloud_test"
}

variable "eks_name" {
  description = "The name of the EKS cluster"
  type        = string
  default     = "my-eks-cluster"
}

variable "vpc_id" {
  description = "The id of VPC to which to attach the EKS cluster"
  type        = string
  default     = "vpc-40767025"
}

variable "vpc_subnets" {
  description = "The ids of VPC public subnets to which to deploy the EKS cluster"
  type        = list(string)
  default     = ["subnet-d1a08ea6","subnet-d83421bd"]
}