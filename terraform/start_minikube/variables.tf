variable "path" {
  description = "Path to the application's YAML files"
  type        = string
  default     = "../../kubernetes/minikube"
}

variable "keel_path" {
  description = "Path to the Keel's YAML file"
  type        = string
  default     = "../../kubernetes/keel.yaml"
}