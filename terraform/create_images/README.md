This Terraform project enables to manage the creation and deletion of VM images. One VM image for a MySQL DBMS and one VM image for the 
RESTful service of our book management micro-service. As such, the images cover the two main components of the micro-service. To create the images, 
you need first to initialize the project via terraform init and then to apply an execution plan via terraform apply. The VMs from which the 
images are created need to be destroyed afterwards. This can be done via the following command: 
terraform destroy -target=aws_instance.db -target=aws_instance.app
In case you also want to destroy the two images, you run: terraform destroy. 