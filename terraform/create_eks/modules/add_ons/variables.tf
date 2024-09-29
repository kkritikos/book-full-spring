variable "cluster_name" {
  description = "The name of the cluster"
  type        = string
  default     = "my-eks-cluster"
}

variable "vpc_id" {
  description = "The id of VPC to which to attach the EKS cluster"
  type        = string
  default     = "vpc-40767025"
}

variable "oidc_url" {
  description = "The URL of OIDC"
  type        = string
  default     = ""
}

variable "oidc_arn" {
  description = "The ARN identifier of OIDC"
  type        = string
  default     = ""
}

variable "region" {
  description = "The name of the AWS region to use"
  type        = string
  default     = "us-west-2"
}