provider "aws" {
  region = var.region
}

data "terraform_remote_state" "images" {
  backend = "local"

  config = {
    path = "../create_images/terraform.tfstate"  # Path to the state file
  }
}

resource "aws_instance" "db" {
  ami           = data.terraform_remote_state.images.outputs.db_ami_id
  instance_type = var.instance_type_db
  key_name      = var.key_name
  vpc_security_group_ids = [aws_security_group.db_sg.id]
  tags = {
    Name = "Book DB"
  }

}

resource "aws_instance" "app" {
  ami           = data.terraform_remote_state.images.outputs.app_ami_id
  instance_type = var.instance_type_app
  key_name      = var.key_name
  vpc_security_group_ids = [aws_security_group.app_sg.id]
  tags = {
    Name = "spring-boot-app-${count.index}"
  }

  user_data = <<-EOF
              #!/bin/bash
              export DB_HOST=${aws_instance.db.public_ip}
              export DB_NAME=${var.db_name}
              export DB_USER=${var.db_user}
              export DB_PASSWORD=${var.db_password}
              cd /home/ubuntu/app/book-spring
              nohup java -jar target/${var.jar_name}.jar > /var/log/spring-boot-app.log 2>&1 &
              echo "User data script executed at $(date)" >> /var/log/user-data.log
              EOF

  count = 2
}

resource "aws_lb" "app_lb" {
  name               = "app-load-balancer"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.lb_sg.id]
  subnets            = var.subnets
}

resource "aws_lb_target_group" "app_tg" {
  name        = "app-target-group"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "instance"
  health_check {
    path                = "/api/books"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 5
    unhealthy_threshold = 2
    matcher             = "200"
  }

}

resource "aws_lb_listener" "app_lb_listener" {
  load_balancer_arn = aws_lb.app_lb.arn
  port              = 80
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app_tg.arn
  }

}

resource "aws_lb_target_group_attachment" "app_tg_attachment" {
  target_group_arn = aws_lb_target_group.app_tg.arn
  target_id        = aws_instance.app[count.index].id
  port             = 8080

  count = 2
}

resource "aws_security_group" "lb_sg" {
  name   = "lb-security-group"
  vpc_id = var.vpc_id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

}

resource "aws_security_group" "db_sg" {
  name   = "db-security-group"
  vpc_id = var.vpc_id

  ingress{
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  ingress{
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

}

resource "aws_security_group" "app_sg" {
  name   = "app-security-group"
  vpc_id = var.vpc_id

  ingress{
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  ingress{
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

}