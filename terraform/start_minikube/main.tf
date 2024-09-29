provider "local" {
  # The local provider allows executing local commands on your machine.
}

resource "null_resource" "start_minikube" {
  provisioner "local-exec" {
    command = <<-EOT
      minikube start --driver=docker
    EOT
  }
  triggers = {
    minikube_start = timestamp() # Re-run if there is any change in minikube state
  }
}

# Wait for Minikube to be ready
resource "null_resource" "wait_for_minikube" {
  depends_on = [null_resource.start_minikube]

  provisioner "local-exec" {
    command = <<EOT
      :check_kubectl
      minikube kubectl -- get nodes >nul 2>&1
      if errorlevel 1 (
        echo "Waiting for Kubernetes API to be ready..."
        timeout /t 10
        goto check_kubectl
      )
      echo "Minikube is ready!"
    EOT
  }
  triggers = {
    wait_for_minikube = timestamp()
  }
}

# Apply YAML manifests after Minikube is ready
resource "null_resource" "apply_manifests" {
  depends_on = [null_resource.wait_for_minikube]


  # Use for_each to loop through each manifest file
  for_each = fileset(var.path, "*.yaml")

  provisioner "local-exec" {
    # Apply each manifest file individually
    command = "kubectl apply -f ${var.path}/${each.value}"
  }
  triggers = {
    apply_manifests = timestamp()
  }
}
