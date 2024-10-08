provider "aws" {
  region = var.aws_region
}

resource "aws_instance" "minikube" {
  ami           = var.ami
  instance_type = var.instance_type
  key_name      = var.key_name

  user_data = <<-EOT
              #!/bin/bash
              sudo apt-get update -y

              sudo apt-get install -y docker.io
              sudo systemctl enable docker
              sudo systemctl start docker
              sudo usermod -aG docker $USER && newgrp docker
              sudo systemctl restart docker
              sleep 5

              curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
              chmod +x minikube
              sudo install minikube /usr/local/bin/
              
              wget https://get.helm.sh/helm-v3.12.0-linux-amd64.tar.gz
              tar -xvf  helm-v3.12.0-linux-amd64.tar.gz
              sudo mv linux-amd64  /usr/local/bin
              rm helm-v3.12.0-linux-amd64.tar.gz
                  
              touch /tmp/docker_minikube_installed	
              EOT

  vpc_security_group_ids = [aws_security_group.minikube_sg.id]

  tags = {
    Name = "Minikube-EC2"
  }
}

# Poll for instance status to ensure user data script completes
resource "null_resource" "wait_for_minikube_instance" {
  depends_on = [aws_instance.minikube]
    
  provisioner "remote-exec" {
    inline = [
      "while [ ! -f /tmp/docker_minikube_installed ]; do sleep 10; done",
      "sudo usermod -aG docker $USER",
      "sudo -u ubuntu nohup minikube start -p test &",
      "echo 'Starting minikube'",        
              
      "while ! sudo -u ubuntu minikube kubectl -p test -- get nodes > /dev/null 2>&1; do",
      "echo 'Waiting for Kubernetes API to be ready...'",
      "sleep 10",
      "done",
      "echo 'Minikube is ready!'",
      
      "git clone https://github.com/kkritikos/book-full-spring.git",
	  "cd book-full-spring",
	  "git checkout kubernetes-ci-cd",
	  "echo 'Repo cloned & branch checked out!'",
	
	  "sudo -u ubuntu minikube kubectl -p test -- apply -f 'kubernetes/keel.yaml'",
      "sudo -u ubuntu minikube kubectl -p test -- apply -f 'kubernetes/minikube/*.yaml'",
      #"sudo -u ubuntu nohup minikube tunnel -p test &",
      "touch /tmp/app_depl_complete"
    ]

    connection {
      type        = "ssh"
      user        = "ubuntu"
      private_key = file("C:/users/kiria/Desktop/cloud_test.pem")
      host        = aws_instance.minikube.public_ip
    }
  }

  provisioner "remote-exec" {
    inline = [
      "while [ ! -f /tmp/app_depl_complete ]; do sleep 10; done",
      "echo 'App deployment script completed'"
    ]

    connection {
      type        = "ssh"
      user        = "ubuntu"
      private_key = file("C:/users/kiria/Desktop/cloud_test.pem")
      host        = aws_instance.minikube.public_ip
    }
  }
}

resource "aws_security_group" "minikube_sg" {
  name        = "minikube-sg"
  description = "Allow SSH and Minikube API access"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]  # Allow SSH access
  }

  ingress {
    from_port   = 8443
    to_port     = 8443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]  # Allow access to Minikube API (Change CIDR for security)
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
