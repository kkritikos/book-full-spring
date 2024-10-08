variable aws_region {
	description = "AWS region on which to create the VM instance"
	type = string 
	default = "us-west-2"
}

variable key_name {
	description = "Name of the SSH keypair to access the instance"
	type = string 
	default = "cloud_test"
}

variable instance_type {
	description = "Name of instance type to use"
	type = string 
	default = "t2.medium"
}

variable ami {
	description = "AMI to use for creating the VM instance"
	type = string 
	default = "ami-0aff18ec83b712f05" # Ubuntu Server 24.04 LTS AMI
}

variable "path" {
  description = "Path to the application's YAML files"
  type        = string
  default     = "book-full-spring/kubernetes/minikube"
}

variable "keel_path" {
  description = "Path to the Keel's YAML file"
  type        = string
  default     = "book-full-spring/kubernetes/keel.yaml"
}